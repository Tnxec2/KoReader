package com.kontranik.koreader.reader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.utils.PrefsHelper

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val close = findViewById<ImageButton>(R.id.imageButton_main_menu_back)
        close.setOnClickListener {
            finish()
        }

        val openFile = findViewById<LinearLayout>(R.id.ll_main_menu_open_file)
        openFile.setOnClickListener {
            openFile()
        }

        val lastOpenedLayout = findViewById<LinearLayout>(R.id.ll_main_menu_last_opened)
        lastOpenedLayout.setOnClickListener {
            openLastOpened()
        }

        val settings = findViewById<LinearLayout>(R.id.ll_main_menu_settings)
        settings.setOnClickListener {
            settings()
        }

        var bookPath: String? = null
        val prefs = getSharedPreferences(ReaderActivity.PREFS_FILE, AppCompatActivity.MODE_PRIVATE)

        if ( prefs.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            bookPath = prefs.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        val bookinfo = findViewById<ImageButton>(R.id.imageButton_mainmenu_bookinfo)
        bookinfo.setOnClickListener {
            openBookInfo(bookPath)
        }
        if ( bookPath == null) bookinfo.visibility = View.GONE

    }

    private fun openFile() {
        val intent = Intent(this, FileChooseActivity::class.java)
        startActivityForResult(intent, REQUEST_ACCESS_TYPE_OPENFILE)
    }

    private fun settings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivityForResult(intent, REQUEST_ACCESS_TYPE_SETTINGS)
    }

    private fun openLastOpened() {
        val intent = Intent( this, BookListActivity::class.java)
        startActivityForResult(intent, REQUEST_ACCESS_TYPE_OPENFILE)
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment: BookInfoFragment = BookInfoFragment.newInstance(bookUri)
            bookInfoFragment.show(supportFragmentManager, "fragment_bookinfo")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode==REQUEST_ACCESS_TYPE_OPENFILE || requestCode==REQUEST_ACCESS_TYPE_SETTINGS ){
            if(resultCode==RESULT_OK){
                if (data != null) {
                    setResult(RESULT_OK, data)
                    finish()
                }
            } else{
                Log.e(TAG, "onActivityResult: access error")
            }
        } else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val TAG = "MainMenuActivity"
        private const val REQUEST_ACCESS_TYPE_OPENFILE = 123
        private const val REQUEST_ACCESS_TYPE_SETTINGS = 124
    }
}