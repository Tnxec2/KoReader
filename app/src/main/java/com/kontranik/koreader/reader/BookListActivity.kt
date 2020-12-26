package com.kontranik.koreader.reader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.database.BookStatusDatabaseAdapter
import com.kontranik.koreader.database.BookStatusService
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.utils.BookListAdapter
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.PrefsHelper

class BookListActivity : AppCompatActivity(), BookListAdapter.BookListAdapterClickListener {

    private var listView: RecyclerView? = null
    private var bookListAdapter: BookListAdapter? = null
    private var bookInfoList: MutableList<BookInfo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        val close = findViewById<ImageButton>(R.id.imageButton_booklist_back)
        close.setOnClickListener {
            finish()
        }

        listView = findViewById(R.id.reciclerView_booklist_list)
        bookListAdapter = BookListAdapter(this, bookInfoList, this)
        listView!!.adapter = bookListAdapter

        loadBooklist()
    }

    private fun openBook(bookInfo: BookInfo) {
        openBook(bookInfo.path)
    }

    private fun openBook(path: String) {
        val data = Intent()
        data.putExtra(ReaderActivity.PREF_TYPE, ReaderActivity.PREF_TYPE_OPEN_BOOK)
        data.putExtra(PrefsHelper.PREF_BOOK_PATH, path)
        setResult(RESULT_OK, data)
        finish()
    }

    private fun loadBooklist() {
        var typ = BOOKLIST_TYP_DEFAULT
        val extras = intent.extras
        if (extras != null) {
            typ = extras.getString(BOOKLIST_TYP, BOOKLIST_TYP_DEFAULT)
        }
        when (typ) {
            BOOKLIST_TYP_LAST_OPENED -> {
                val bookservice = BookStatusService(BookStatusDatabaseAdapter(this))
                val books = bookservice.getLastOpened(LAST_OPENED_COUNT)
                bookInfoList.clear()
                for ( bookstatus in books) {
                    if ( bookstatus.path != null) {
                        if ( ! FileHelper.contentFileExist(applicationContext, bookstatus.path) ){
                            bookservice.delete(bookstatus.id!!)
                        }

                        val bookInfo = BookInfo(
                                title = bookstatus.title,
                                cover = null,
                                authors = mutableListOf(),
                                filename = bookstatus.path!!,
                                path = bookstatus.path!!,
                                annotation = ""
                        )
                        bookInfoList.add(bookInfo)

                    }
                }
                bookListAdapter!!.notifyDataSetChanged()
            }
            else -> {}
        }
    }

    override fun onBooklistItemClickListener(position: Int) {
        val selectedBook = bookInfoList[position]
        openBook(selectedBook)
    }

    companion object {
        const val BOOKLIST_TYP = "typ"
        const val BOOKLIST_TYP_LAST_OPENED = "lastopened"
        const val BOOKLIST_TYP_DEFAULT = BOOKLIST_TYP_LAST_OPENED
        const val LAST_OPENED_COUNT = 10
    }
}