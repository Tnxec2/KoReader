package com.kontranik.koreader.compose.ui.openfile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.compose.ui.settings.OPENFILE_EXTERNAL_PATHS
import com.kontranik.koreader.compose.ui.settings.PREFS_FILE
import com.kontranik.koreader.compose.ui.settings.PREF_BOOK_PATH
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem
import androidx.core.net.toUri
import androidx.core.content.edit

class OpenFileViewModel : ViewModel()  {
    private var externalPaths = mutableListOf<String>()
    private var selectedDocumentFileUriString = MutableLiveData<String?>()
    private var lastPath: String? = null

    val fileItemList = mutableStateOf(listOf<FileItem>())

    val scrollToDocumentFileUriString = mutableStateOf<String?>(null)

    val isVisibleImageButtonFilechooseAddStorage = mutableStateOf(false)
    val showConfirmSelectStorageDialog = mutableStateOf(false)


    init {
        start()

        selectedDocumentFileUriString.observeForever {
            loadPath(it)
        }
    }

    fun start() {
        loadPrefs()
    }

    private fun loadPrefs() {
        val settings = KoReaderApplication.getContext()
            .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

        if ( settings.contains(OPENFILE_EXTERNAL_PATHS)) {
            val eP = settings.getStringSet(OPENFILE_EXTERNAL_PATHS, null)
            externalPaths = eP?.toMutableList() ?: mutableListOf()
        }

        if ( settings!!.contains(PREF_BOOK_PATH) ) {
            this.lastPath = settings.getString(PREF_BOOK_PATH, null)
            extractDirectory()
        }
    }

    private fun extractDirectory() {
        lastPath?.let {
            val index = it.lastIndexOf("%2F")
            val parent = if ( index > 0)  {
                it.substring(0, index)
            } else it
            val directoryUri = parent.toUri().toString()
            // val sf = DocumentFile.fromSingleUri(applicationContext, directoryUri)
            selectedDocumentFileUriString.postValue(directoryUri)
        }
    }

    private fun savePrefsExternalPaths() {
        val settings = KoReaderApplication.getContext()
            .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        settings.edit {
            putStringSet(OPENFILE_EXTERNAL_PATHS, externalPaths.toMutableSet())
        }
    }


    fun loadPath(selectedDocumentFileUriString: String?) {
        if ( selectedDocumentFileUriString == null ) {
            storageList()
            return
        }

        if (!lastPathInExternalPaths()) {
            storageList()
            return
        }

        getFileList(selectedDocumentFileUriString)

        lastPath?.let {
            for (pos in 0 until fileItemList.value.size) {
                val uriString = fileItemList.value[pos].uriString
                if (uriString == it) {
                    scrollToDocumentFileUriString.value = uriString
                    return
                }
            }
        }
    }

    private fun lastPathInExternalPaths(): Boolean {
        if ( lastPath == null ) return false
        return externalPaths.any { it == lastPath }
    }

    fun storageList() {
        isVisibleImageButtonFilechooseAddStorage.value = true

        val fileList = mutableListOf<FileItem>()
        if ( externalPaths.isEmpty() ) {
            showConfirmSelectStorageDialog.value = true
            return
        }

        try {
            val contentResolver = KoReaderApplication.getContext().contentResolver
            val takeFlags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            val iterator = externalPaths.iterator()
            while(iterator.hasNext()) {
                val path = iterator.next()
                val directoryUri = path.toUri()
                println("directoryUri: $directoryUri")
                contentResolver.takePersistableUriPermission(directoryUri, takeFlags)
                val documentsTree = DocumentFile.fromTreeUri(KoReaderApplication.getContext(), directoryUri)
                println("documentsTree $documentsTree isdir: ${documentsTree?.isDirectory}, can read ${documentsTree?.canRead()}")
                if ( documentsTree == null || !documentsTree.isDirectory || !documentsTree.canRead() ) {
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
        selectedDocumentFileUriString.postValue(fileItem.uriString)
    }

    private fun getFileList(documentFilePath: String?) {
        if ( documentFilePath == null) {
            storageList()
            return
        } else {
            val fl = FileHelper.getFileListDC(KoReaderApplication.getContext(), documentFilePath)
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