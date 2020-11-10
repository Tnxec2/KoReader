package com.kontranik.koreader.reader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.kontranik.koreader.R

class MainMenuActivity : AppCompatActivity() {


    private val REQUEST_ACCESS_TYPE_OPENFILE = 123
    private val REQUEST_ACCESS_TYPE_SETTINGS = 124

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

        val settings = findViewById<LinearLayout>(R.id.ll_main_menu_settings)
        settings.setOnClickListener {
            settings()
        }
    }


    private fun openFile() {
        Toast.makeText(applicationContext, "OpenFile", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, FileChooseActivity::class.java)
        startActivityForResult(intent, REQUEST_ACCESS_TYPE_OPENFILE)
    }

    private fun settings() {
        Toast.makeText(applicationContext, "Settings", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode==REQUEST_ACCESS_TYPE_OPENFILE ){
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
    }
}