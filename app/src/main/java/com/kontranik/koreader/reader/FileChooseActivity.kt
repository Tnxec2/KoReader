package com.kontranik.koreader.reader

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.databinding.ActivityFilechooseBinding
import com.kontranik.koreader.utils.*


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class FileChooseActivity : AppCompatActivity(),
        FileListAdapter.FileListAdapterClickListener,
        BookInfoFragment.BookInfoListener{
    
    private lateinit var binding: ActivityFilechooseBinding

    private var externalPaths = mutableSetOf<String>()

    private var fileItemList: MutableList<FileItem> = ArrayList()

    private var selectedDocumentFileUriString: String? = null
    private var lastPaht: String? = null

    private var settings: SharedPreferences? = null
    private var prefEditor: SharedPreferences.Editor? = null

    private var pathsPosition: HashMap<String, Int> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityFilechooseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButtonFilechooseClose.setOnClickListener {
            finish()
        }

        binding.imageButtonFilechooseBack.setOnClickListener {
            goBack()
        }

        binding.imageButtonFilechooseGotoStorage.setOnClickListener {
            storageList()
        }

        binding.imageButtonFilechooseAddStorage.setOnClickListener {
            storageAdd()
        }
        binding.imageButtonFilechooseAddStorage.visibility = View.GONE

        binding.reciclerViewFiles.adapter = FileListAdapter(this, fileItemList, this)

        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
        loadPrefs()
        loadPath()
    }

    private fun openBook(fileItem: FileItem) {
        openBook(fileItem.uriString)
    }

    private fun openBook(uriString: String?) {
        if ( uriString == null) return
        savePrefs(uriString)
        val data = Intent()
        data.putExtra(ReaderActivity.PREF_TYPE, ReaderActivity.PREF_TYPE_OPEN_BOOK)
        data.putExtra(PrefsHelper.PREF_BOOK_PATH, uriString)
        setResult(RESULT_OK, data)
        finish()
    }

    private  fun goBack() {
        if ( fileItemList[0].name == FileHelper.BACKDIR ) {
            onFilelistItemClickListener(0)
        }
    }

    private fun loadPath() {
        if ( selectedDocumentFileUriString == null ) storageList()
        else {
            getFileList(selectedDocumentFileUriString)

            if ( lastPaht != null) {
                for (pos in 0 until fileItemList.size) {
                    val uriString = fileItemList[pos].uriString
                    if (uriString == lastPaht!!) {
                        (binding.reciclerViewFiles.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(pos, 0)
                        break
                    }
                }
            }
        }
    }

    private fun getFileList(fileItem: FileItem) {
        binding.imageButtonFilechooseAddStorage.visibility = View.GONE
        selectedDocumentFileUriString = fileItem.uriString
        getFileList(fileItem.uriString)
    }

    private fun getFileList(documentFilePath: String?) {
        if ( documentFilePath == null) {
            storageList()
            return
        } else {
            fileItemList.clear()
            binding.reciclerViewFiles.adapter = null
            // val fl = FileHelper.getFileList(applicationContext, documentFile)

            val fl = FileHelper.getFileListDC(applicationContext, documentFilePath)
            fileItemList.addAll(fl)
            binding.reciclerViewFiles.adapter = FileListAdapter(this, fileItemList, this)

            if (pathsPosition.containsKey(documentFilePath)) {
               //binding.reciclerViewFiles.scrollToPosition(pathsPosition[documentFilePath]!!)
                (binding.reciclerViewFiles.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(pathsPosition[documentFilePath]!!, 0)
            }
        }
    }

    private fun loadPrefs() {
        if ( settings!!.contains(PREF_LAST_PATH) ) {
            lastPaht = settings!!.getString(PREF_LAST_PATH, null)
            if ( lastPaht != null) {
                val index = lastPaht!!.lastIndexOf("%2F")
                val parent = if ( index > 0)  {
                        lastPaht!!.substring(0, index)
                    } else lastPaht
                val directoryUri = Uri.parse(parent).toString()
                // val sf = DocumentFile.fromSingleUri(applicationContext, directoryUri)
                selectedDocumentFileUriString = directoryUri
            }
        }

        if ( settings!!.contains(PREF_EXTERNAL_PATHS)) {
            val eP = settings!!.getStringSet(PREF_EXTERNAL_PATHS, null)
            if ( eP != null) {
                externalPaths.addAll(eP)
            }
        }
    }

    private fun savePrefs(uriString: String?) {
        prefEditor = settings!!.edit()
        if ( uriString != null) prefEditor!!.putString(PREF_LAST_PATH, uriString)
        prefEditor!!.putStringSet(PREF_EXTERNAL_PATHS, externalPaths)
        prefEditor!!.apply()
    }

    private fun storageList() {
        if ( externalPaths.isEmpty() ) {
            performFileSearch()
            return
        }
        fileItemList.clear()
        binding.reciclerViewFiles.adapter = null

        for ( path in externalPaths ) {

            val directoryUri = Uri.parse(path)
                    ?: throw IllegalArgumentException("Must pass URI of directory to open")
            val documentsTree = DocumentFile.fromTreeUri(application, directoryUri)
            if ( documentsTree == null || ! documentsTree.isDirectory || ! documentsTree.canRead() ) {
                externalPaths.remove(path)
            } else {
                //val childDocuments = documentsTree.listFiles()
                var name = documentsTree.name
                if ( name == null) name = documentsTree.uri.toString()
                fileItemList.add(FileItem(ImageEnum.SD, name = name, path = documentsTree.uri.pathSegments.last(), documentsTree.uri.toString(), isDir = true, isRoot = false, null))
            }
        }
        fileItemList.sortBy { it.name }

        binding.reciclerViewFiles.adapter = FileListAdapter(this, fileItemList, this)

        binding.imageButtonFilechooseAddStorage.visibility = View.VISIBLE
    }

    override fun onFilelistItemClickListener(position: Int) {
        val selectedFileItem = fileItemList[position]

        if ( position > 0) {
            if ( selectedDocumentFileUriString != null)
                pathsPosition.remove(selectedFileItem.uriString)
                pathsPosition[selectedDocumentFileUriString!!] = position
        }

        if (selectedFileItem.isDir) {
            binding.reciclerViewFiles.layoutManager as LinearLayoutManager
            if ( selectedFileItem.isRoot ) {
                if ( externalPaths.contains(selectedFileItem.uriString) )
                    getFileList(selectedFileItem)
                else
                    storageList()
            } else {
                getFileList(selectedFileItem)
            }
        } else {
            //openBook(selectedFileItem)
            if ( selectedFileItem.uriString != null) {
                openBookInfo(selectedFileItem.uriString)
            }
        }
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment = BookInfoFragment.newInstance(bookUri)
            bookInfoFragment.setListener(this)
            bookInfoFragment.show(supportFragmentManager, "fragment_bookinfo")
        }
    }

    override fun onBookInfoFragmentReadBook(bookUri: String) {
        openBook(bookUri)
    }

    private fun storageAdd() {
        performFileSearch()
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    private fun performFileSearch() {
        Toast.makeText(applicationContext, "Select directory or storage from dialog, and grant access", Toast.LENGTH_LONG).show()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return

            contentResolver.takePersistableUriPermission(
                    directoryUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.d(TAG, directoryUri.toString())
            externalPaths.add(directoryUri.toString())
            savePrefs(uriString = null)
            storageList()
        }
    }

    companion object {
        const val PREFS_FILE = "OpenFileActivitySettings"
        const val PREF_LAST_PATH = "LastPath"
        const val PREF_EXTERNAL_PATHS = "ExternalPaths"
        const val TAG = "FileChooseActivity"

        private const val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e
    }
}