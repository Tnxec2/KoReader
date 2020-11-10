package com.kontranik.koreader.reader


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.kontranik.koreader.R


class CustomActionModeCallback(context: Context) : ActionMode.Callback {
    private val context: Context
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menu!!.clear()
        mode!!.menuInflater.inflate(R.menu.text_selected_menu_custom, menu)
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuitem_text_select_bookmark_set -> {
                Toast.makeText(context, "Bookmark!", Toast.LENGTH_SHORT).show()
                mode.finish()
                return true
            }
            R.id.menuitem_text_select_bookmark_share -> {
                Toast.makeText(context, "Share!", Toast.LENGTH_SHORT).show()
                mode.finish()
                return true
            }
            R.id.menuitem_text_select_bookmark_copy -> {
                mode.finish()
                return true
            }
            else -> return false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {}

    init {
        this.context = context
    }
}