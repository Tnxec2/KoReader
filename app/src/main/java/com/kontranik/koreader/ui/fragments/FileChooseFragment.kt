package com.kontranik.koreader.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.databinding.FragmentFilechooseBinding
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.ui.adapters.FileListAdapter
import com.kontranik.koreader.utils.ImageEnum


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class FileChooseFragment : DialogFragment(),
        FileListAdapter.FileListAdapterClickListener,
        BookInfoFragment.BookInfoListener{
    
    private lateinit var binding: FragmentFilechooseBinding
    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel

    private var externalPaths = mutableSetOf<String>()

    private var fileItemList: MutableList<FileItem> = ArrayList()

    private var oldSelectedDocumentFileUriString: String? = null
    private var selectedDocumentFileUriString: String? = null
    private var lastPaht: String? = null

    private var settings: SharedPreferences? = null
    private var prefEditor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFilechooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]

        binding.imageButtonFilechooseClose.setOnClickListener {
            dismiss()
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

        binding.reciclerViewFiles.adapter = FileListAdapter(requireContext(), fileItemList, this)

        settings = requireActivity()
            .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        loadPrefs()
        loadPath()
    }

    private fun openBook(uriString: String) {
        savePrefs(uriString)
        mReaderActivityViewModel.setBookPath(requireContext(), uriString)
        dismiss()
    }

    private fun deleteBook(uriString: String?) {
        if ( mReaderActivityViewModel.deleteBook(uriString)) {
            fileItemList.removeAll { it.uriString == uriString }
            binding.reciclerViewFiles.adapter?.notifyDataSetChanged()
        }
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
        oldSelectedDocumentFileUriString = selectedDocumentFileUriString
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

            val fl = FileHelper.getFileListDC(requireContext(), documentFilePath)
            fileItemList.addAll(fl)
            binding.reciclerViewFiles.adapter = FileListAdapter(requireContext(), fileItemList, this)

            (binding.reciclerViewFiles.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(getPositionInFileItemList(oldSelectedDocumentFileUriString), 0)
        }
    }

    private fun getPositionInFileItemList(path: String?): Int {
        if (path == null) return 0
        val position = fileItemList.indexOfFirst { it.isDir && it.uriString.equals(path) }
        return if (position > 0) position
        else 0
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
            val documentsTree = DocumentFile.fromTreeUri(requireContext(), directoryUri)
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

        binding.reciclerViewFiles.adapter = FileListAdapter(requireContext(), fileItemList, this)

        binding.imageButtonFilechooseAddStorage.visibility = View.VISIBLE
    }

    override fun onFilelistItemClickListener(position: Int) {
        val selectedFileItem = fileItemList[position]

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
            bookInfoFragment.show(requireActivity().supportFragmentManager, "fragment_bookinfo")
        }
    }

    override fun onBookInfoFragmentReadBook(bookUri: String) {
        openBook(bookUri)
    }

    override fun onBookInfoFragmentDeleteBook(bookUri: String) {
        deleteBook(bookUri)
    }

    private fun storageAdd() {
        performFileSearch()
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    private fun performFileSearch() {
        Toast.makeText(requireContext(), "Select directory or storage from dialog, and grant access", Toast.LENGTH_LONG).show()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION   // write permission to remove book
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return

            requireActivity().contentResolver.takePersistableUriPermission(
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