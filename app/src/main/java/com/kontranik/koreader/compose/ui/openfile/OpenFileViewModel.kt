package com.kontranik.koreader.compose.ui.openfile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem

const val OPENFILE_PREFS_FILE = "OpenFileActivitySettings"
const val OPENFILE_LAST_PATH = "LastPath"
const val OPENFILE_EXTERNAL_PATHS = "ExternalPaths"

class OpenFileViewModel(
    val context: Context) : ViewModel()  {
    private var externalPaths = mutableListOf<String>()
    private var selectedDocumentFileUriString: String? = null
    private var lastPath: String? = null

    val fileItemList = mutableStateOf(listOf<FileItem>())

    val scrollToDocumentFileUriString = mutableStateOf<String?>(null)

    val isVisibleImageButtonFilechooseAddStorage = mutableStateOf(false)
    val showConfirmSelectStorageDialog = mutableStateOf(false)


    init {
        loadPrefs()
        loadPath()
    }

    private fun loadPrefs() {
        val settings = context
            .getSharedPreferences(
                OPENFILE_PREFS_FILE,
                Context.MODE_PRIVATE)

        if ( settings!!.contains(OPENFILE_LAST_PATH) ) {
            this.lastPath = settings.getString(OPENFILE_LAST_PATH, null)
            extractDirectory()
        }

        if ( settings.contains(OPENFILE_EXTERNAL_PATHS)) {
            // settings!!.edit().remove(PREF_EXTERNAL_PATHS).apply()

            val eP = settings.getStringSet(OPENFILE_EXTERNAL_PATHS, null)
            if ( eP != null) {
                externalPaths = eP.toMutableList()
            } else {
                externalPaths = mutableListOf()
            }
        }
    }

    private fun extractDirectory() {
        lastPath?.let {
            val index = it.lastIndexOf("%2F")
            val parent = if ( index > 0)  {
                it.substring(0, index)
            } else it
            val directoryUri = Uri.parse(parent).toString()
            // val sf = DocumentFile.fromSingleUri(applicationContext, directoryUri)
            selectedDocumentFileUriString = directoryUri
        }
    }

    fun savePrefsOpenedBook(uriString: String) {
        val settings = context
            .getSharedPreferences(
                OPENFILE_PREFS_FILE,
                Context.MODE_PRIVATE)
        val prefEditor = settings.edit()
        prefEditor!!.putString(OPENFILE_LAST_PATH, uriString)
        prefEditor.apply()
    }

    private fun savePrefsExternalPaths() {
        val settings = context
            .getSharedPreferences(
                OPENFILE_PREFS_FILE,
                Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        prefEditor.putStringSet(OPENFILE_EXTERNAL_PATHS, externalPaths.toMutableSet())
        prefEditor.apply()
    }


    private fun loadPath() {
        if ( selectedDocumentFileUriString == null ) {
            storageList()
            return
        }

        var lastPathIsInStorageList = false
        lastPath?.let {
            externalPaths.forEach {
                if (it.contains(it)) lastPathIsInStorageList = true
            }
        }
        if (!lastPathIsInStorageList) {
            storageList()
            return
        }

        getFileList(selectedDocumentFileUriString)

        lastPath?.let {
            for (pos in 0 until fileItemList.value.size) {
                val uriString = fileItemList.value[pos].uriString
                if (uriString == it) {
                    scrollToDocumentFileUriString.value = uriString
                }
            }
        }
    }

    fun storageList() {
        isVisibleImageButtonFilechooseAddStorage.value = true

        val fileList = mutableListOf<FileItem>()
        if ( externalPaths.isEmpty() ) {
            showConfirmSelectStorageDialog.value = true
            return
        }

        try {
            val iterator = externalPaths.iterator()
            while(iterator.hasNext()) {
                val path = iterator.next()
                val directoryUri = Uri.parse(path)

                val documentsTree = DocumentFile.fromTreeUri(context, directoryUri)
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
        scrollToDocumentFileUriString.value = selectedDocumentFileUriString
        selectedDocumentFileUriString = fileItem.uriString
    }

    private fun getFileList(documentFilePath: String?) {
        if ( documentFilePath == null) {
            storageList()
            return
        } else {
            val fl = FileHelper.getFileListDC(context, documentFilePath)
            fileItemList.value = fl.toMutableList()
        }
    }

    fun getPositionInFileItemList(): Int {
        if (scrollToDocumentFileUriString.value == null) return 0
        val position = fileItemList.value.indexOfFirst { it.uriString.equals(scrollToDocumentFileUriString.value) }
        return if (position > 0) position
        else 0
    }

    fun removeBookFromList(uriString: String?) {
        if ( uriString != null) {
            val indexToRemove = fileItemList.value.indexOfFirst { it.uriString == uriString }
            fileItemList.value = fileItemList.value.filterIndexed{ index, _ -> index == indexToRemove }
        }
    }

    fun deleteStorage(position: Int) {
        externalPaths.removeAt(position)
        savePrefsExternalPaths()
        storageList()
    }

    fun addStoragePath(uri: String) {
        externalPaths.add(uri)
        savePrefsExternalPaths()
        storageList()
    }

    fun goBack() {
        if ( fileItemList.value[0].name == FileHelper.BACKDIR ) {
            onFilelistItemClick(0)
        }
    }

    fun onFilelistItemClick(position: Int) {
        val selectedFileItem = fileItemList.value[position]

        if (selectedFileItem.isDir) {
            if ( selectedFileItem.isRoot ) {
                if ( externalPaths.contains(selectedFileItem.uriString) )
                    getFileList(selectedFileItem)
                else
                    storageList()
            } else {
                getFileList(selectedFileItem)
            }
        }
    }
}