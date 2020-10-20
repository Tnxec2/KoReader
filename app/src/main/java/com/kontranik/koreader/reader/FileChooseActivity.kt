package com.kontranik.koreader.reader

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.FileListAdapter
import java.io.File
import java.util.*


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class FileChooseActivity : AppCompatActivity() {

    private var listView: ListView? = null
    private var fileListAdapter: FileListAdapter? = null
    private var fileItemList: MutableList<FileItem> = ArrayList()
    private val rootPaths: MutableList<String> = ArrayList()

    private var selectedPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filechoose)

        listView = findViewById(R.id.listView_files)
        fileListAdapter = FileListAdapter(this, R.layout.filelist_item, fileItemList)
        listView!!.adapter = fileListAdapter

        val itemListener = OnItemClickListener { parent, v, position, id ->
            val selectedFileItem = parent.getItemAtPosition(position) as FileItem
            if (selectedFileItem.isDir) {
                if (rootPaths.contains(selectedFileItem.path)) storageList else getFileList(selectedFileItem)
            } else {
                backToReader(selectedFileItem)
            }
        }
        listView!!.onItemClickListener = itemListener

        val extras = intent.extras
        if (extras != null && extras.containsKey(ReaderActivity.INTENT_PATH)) {
            selectedPath = extras.getString(ReaderActivity.PREF_LAST_PATH);
        }
        loadPath()
    }

    private fun backToReader(fileItem: FileItem) {
        backToReader(fileItem.path)
    }

    private fun backToReader(path: String) {
        val data = Intent()
        data.putExtra(ReaderActivity.PREF_LAST_PATH, selectedPath)
        data.putExtra(ReaderActivity.PREF_BOOK_PATH, path)
        setResult(RESULT_OK, data)
        finish()
    }

    private fun loadPath() {
        if ( selectedPath == null ) storageList
        else getFileList(selectedPath!!)
    }

    private fun getFileList(fileItem: FileItem) {
        selectedPath = fileItem.path
        getFileList(fileItem.path)
    }

    private fun getFileList(path: String) {
        fileItemList.clear()
        fileItemList.addAll(FileHelper.getFileList(path))
        fileListAdapter!!.notifyDataSetInvalidated()
    }

    private val storageList: Unit
        get() {
            fileItemList.clear()
            rootPaths.clear()
            fileItemList.addAll(FileHelper.getStorageList())
            for (fileItem in fileItemList) {
                val parent = File(fileItem.path).parent
                if (!rootPaths.contains(parent)) rootPaths.add(parent)
            }
            fileListAdapter!!.notifyDataSetInvalidated()
        }

    companion object {

    }
}