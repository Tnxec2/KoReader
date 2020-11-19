package com.kontranik.koreader.reader

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.FileListAdapter
import com.kontranik.koreader.utils.PrefsHelper
import java.io.File
import java.util.*


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class FileChooseActivity : AppCompatActivity(), FileListAdapter.FileListAdapterClickListener {

    private var listView: RecyclerView? = null
    private var fileListAdapter: FileListAdapter? = null
    private var fileItemList: MutableList<FileItem> = ArrayList()
    private val rootPaths: MutableList<String> = ArrayList()

    private var selectedPath: String? = null

    private var settings: SharedPreferences? = null
    private var prefEditor: SharedPreferences.Editor? = null

    private var pathsPosition: HashMap<String, Int> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filechoose)

        val close = findViewById<ImageButton>(R.id.imageButton_filechoose_close)
        close.setOnClickListener {
            finish()
        }

        val back = findViewById<ImageButton>(R.id.imageButton_filechoose_back)
        back.setOnClickListener {
            goBack()
        }

        val storage = findViewById<ImageButton>(R.id.imageButton_filechoose_goto_storage)
        storage.setOnClickListener {
            storageList()
        }

        listView = findViewById(R.id.reciclerView_files)
        fileListAdapter = FileListAdapter(this, fileItemList, this)
        listView!!.adapter = fileListAdapter

        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
        loadPrefs()
        loadPath()
    }

    private fun openBook(fileItem: FileItem) {
        savePrefs()
        openBook(fileItem.path)
    }

    private fun openBook(path: String) {
        val data = Intent()
        data.putExtra(ReaderActivity.PREF_TYPE, ReaderActivity.PREF_TYPE_OPEN_BOOK)
        data.putExtra(PrefsHelper.PREF_BOOK_PATH, path)
        setResult(RESULT_OK, data)
        finish()
    }

    private  fun goBack() {
        if ( fileItemList[0].name == FileHelper.BACKDIR ) {
            onFilelistItemClickListener(0)
        }
    }

    private fun loadPath() {
        if ( selectedPath == null ) storageList()
        else getFileList(selectedPath!!)
    }

    private fun getFileList(fileItem: FileItem) {
        selectedPath = fileItem.path
        getFileList(fileItem.path)
    }

    private fun getFileList(path: String) {
        fileItemList.clear()
        fileItemList.addAll(FileHelper.getFileList(path))
        fileListAdapter!!.notifyDataSetChanged()

        if ( pathsPosition.containsKey(path)) {
            listView!!.scrollToPosition(pathsPosition[path]!!)
        }
    }

    private fun loadPrefs() {
        if ( settings!!.contains(PREF_LAST_PATH) ) {
            selectedPath = settings!!.getString(PREF_LAST_PATH, null)
        }
    }

    private fun savePrefs() {
        prefEditor = settings!!.edit()
        prefEditor!!.putString(PREF_LAST_PATH, selectedPath)
        prefEditor!!.apply()
    }

    private fun storageList() {
        selectedPath = "storage"
        fileItemList.clear()
        rootPaths.clear()
        fileItemList.addAll(FileHelper.storageList)
        for (fileItem in fileItemList) {
            val parent = File(fileItem.path).parent
            if (!rootPaths.contains(parent)) rootPaths.add(parent)
        }
        fileListAdapter!!.notifyDataSetChanged()
    }

    companion object {
        private const val PREFS_FILE = "OpenFileActivitySettings"
        const val PREF_LAST_PATH = "LastPath"
    }

    override fun onFilelistItemClickListener(position: Int) {
        val selectedFileItem = fileItemList[position]

        if (selectedFileItem.isDir) {
            val lm = listView!!.layoutManager as LinearLayoutManager
            pathsPosition[selectedPath!!] = lm.findFirstVisibleItemPosition()
            if (rootPaths.contains(selectedFileItem.path)) storageList() else getFileList(selectedFileItem)
        } else {
            openBook(selectedFileItem)
        }
    }
}