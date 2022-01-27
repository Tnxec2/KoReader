package com.kontranik.koreader.reader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.databinding.ActivityMainMenuBinding
import com.kontranik.koreader.utils.PrefsHelper

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageButtonMainMenuBack.setOnClickListener {
            finish()
        }

        binding.llMainMenuOpenFile.setOnClickListener {
            openFile()
        }

        binding.llMainMenuLastOpened.setOnClickListener {
            openLastOpened()
        }

        binding.llMainMenuSettings.setOnClickListener {
            settings()
        }

        var bookPath: String? = null
        val prefs = getSharedPreferences(ReaderActivity.PREFS_FILE, AppCompatActivity.MODE_PRIVATE)

        if ( prefs.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            bookPath = prefs.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        binding.imageButtonMainmenuBookinfo.setOnClickListener {
            openBookInfo(bookPath)
        }
        if ( bookPath == null) binding.imageButtonMainmenuBookinfo.visibility = View.GONE

    }

    private fun openFile() {
        Log.d("MainMenu", "openFile...")
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