package com.kontranik.koreader.ui.fragments

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem

const val PREFS_FILE = "OpenFileActivitySettings"
const val PREF_LAST_PATH = "LastPath"
const val PREF_EXTERNAL_PATHS = "ExternalPaths"

class FileChooseFragmentViewModel(val app: Application) : AndroidViewModel(app)  {
    private val externalPaths = MutableLiveData(mutableListOf<String>())

    val fileItemList = MutableLiveData(mutableListOf<FileItem>())

    val scrollToDocumentFileUriString = MutableLiveData<String?>()
    private val selectedDocumentFileUriString = MutableLiveData<String?>()
    private val lastPath = MutableLiveData<String?>()

    val isVisibleImageButtonFilechooseAddStorage = MutableLiveData<Boolean>(false)
    val showConfirmSelectStorageDialog = MutableLiveData<Boolean>(false)
    val showOpenBookInfo = MutableLiveData<String?>()
    val removedItemIndex = MutableLiveData<Int>()
    init {
        loadPrefs()
        loadPath()
    }

    private fun loadPrefs() {
        val settings = app
            .getSharedPreferences(
                PREFS_FILE,
                Context.MODE_PRIVATE)
        if ( settings!!.contains(PREF_LAST_PATH) ) {
            this.lastPath.value = settings.getString(PREF_LAST_PATH, null)
            extractDirectory()
        }

        if ( settings.contains(PREF_EXTERNAL_PATHS)) {
            // settings!!.edit().remove(PREF_EXTERNAL_PATHS).apply()

            val eP = settings.getStringSet(PREF_EXTERNAL_PATHS, null)
            if ( eP != null) {
                externalPaths.value = eP.toMutableList()
            } else {
                externalPaths.value = mutableListOf()
            }
        }
    }

    private fun extractDirectory() {
        if ( lastPath.value != null) {
            val index = lastPath.value!!.lastIndexOf("%2F")
            val parent = if ( index > 0)  {
                lastPath.value!!.substring(0, index)
            } else lastPath.value!!
            val directoryUri = Uri.parse(parent).toString()
            // val sf = DocumentFile.fromSingleUri(applicationContext, directoryUri)
            selectedDocumentFileUriString.value = directoryUri
        }
    }

    fun savePrefsOpenedBook(uriString: String) {
        val settings = app
            .getSharedPreferences(
                PREFS_FILE,
                Context.MODE_PRIVATE)
        val prefEditor = settings.edit()
        prefEditor!!.putString(PREF_LAST_PATH, uriString)
        prefEditor.apply()
    }

    private fun savePrefsExternalPaths() {
        val settings = app
            .getSharedPreferences(
                PREFS_FILE,
                Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        if ( externalPaths.value != null)
            prefEditor.putStringSet(PREF_EXTERNAL_PATHS, externalPaths.value!!.toMutableSet())
        prefEditor.apply()
    }


    private fun loadPath() {
        if ( selectedDocumentFileUriString.value == null ) storageList()

        var lastPathIsInStorageList = false
        if (lastPath.value != null) {
            externalPaths.value?.forEach {
                if (lastPath.value!!.contains(it)) lastPathIsInStorageList = true
            }
        }
        if (!lastPathIsInStorageList) storageList()

        getFileList(selectedDocumentFileUriString.value)

        if ( lastPath.value != null) {
            for (pos in 0 until fileItemList.value!!.size) {
                val uriString = fileItemList.value!![pos].uriString
                if (uriString == lastPath.value!!) {
                    //(binding.reciclerViewFiles.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(pos, 0)
                    scrollToDocumentFileUriString.value = uriString
                }
            }
        }
    }

    fun storageList() {
        isVisibleImageButtonFilechooseAddStorage.value = true

        if ( externalPaths.value == null || externalPaths.value!!.isEmpty() ) {
            showConfirmSelectStorageDialog.value = true
            return
        }

        val fileList = mutableListOf<FileItem>()
        try {
            val iterator = externalPaths.value?.iterator()
            while(iterator?.hasNext() == true) {
                val path = iterator.next()
                val directoryUri = Uri.parse(path)

                val documentsTree = DocumentFile.fromTreeUri(app.applicationContext, directoryUri)
                if ( documentsTree == null || ! documentsTree.isDirectory || ! documentsTree.canRead() ) {
                    iterator.remove()
                } else {
                    fileList.add(FileItem(documentsTree))
                }
            }
        } catch (e: Exception) {
            Log.e("STORAGELIST", e.localizedMessage, e)
        }

        fileList.sortBy { it.name }
        fileItemList.value = fileList
    }

    private fun getFileList(fileItem: FileItem) {
        isVisibleImageButtonFilechooseAddStorage.value = false
        getFileList(fileItem.uriString)
        scrollToDocumentFileUriString.value = selectedDocumentFileUriString.value
        selectedDocumentFileUriString.value = fileItem.uriString
    }

    private fun getFileList(documentFilePath: String?) {
        if ( documentFilePath == null) {
            storageList()
            return
        } else {
            val fl = FileHelper.getFileListDC(app.applicationContext, documentFilePath)
            fileItemList.value = fl.toMutableList()
        }
    }

    fun getPositionInFileItemList(): Int {
        if (scrollToDocumentFileUriString.value == null) return 0
        val position = fileItemList.value!!.indexOfFirst { it.uriString.equals(scrollToDocumentFileUriString.value) }
        return if (position > 0) position
        else 0
    }

    fun removeBookFromList(uriString: String?) {
        if ( uriString != null) {
            val index = fileItemList.value!!.indexOfFirst { it.uriString == uriString }
            fileItemList.value!!.removeAt(index)
            removedItemIndex.value = index
        }
    }

    fun deleteStorage(position: Int) {
        externalPaths.value!!.removeAt(position)
        savePrefsExternalPaths()
        storageList()
    }

    fun addStoragePath(uri: String) {
        externalPaths.value!!.add(uri)
        savePrefsExternalPaths()
        storageList()
    }

    fun goBack() {
        if ( fileItemList.value!![0].name == FileHelper.BACKDIR ) {
            onFilelistItemClick(0)
        }
    }

    fun onFilelistItemClick(position: Int) {
        val selectedFileItem = fileItemList.value!![position]

        if (selectedFileItem.isDir) {

            if ( selectedFileItem.isRoot ) {
                if ( externalPaths.value!!.contains(selectedFileItem.uriString) )
                    getFileList(selectedFileItem)
                else
                    storageList()
            } else {
                getFileList(selectedFileItem)
            }
        } else {
            //openBook(selectedFileItem)
            if ( selectedFileItem.uriString != null) {
                showOpenBookInfo.value = selectedFileItem.uriString
            }
        }
    }
}