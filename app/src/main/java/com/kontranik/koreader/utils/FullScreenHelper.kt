package com.kontranik.koreader.utils

import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kontranik.koreader.R

class FullScreenHelper {

    companion object {
        fun isFullScreen(window: Window): Boolean {
            return window.attributes.flags and
                    WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
        }

        fun setFullScreen(full: Boolean, activity: AppCompatActivity) {
            if (full == isFullScreen(activity.window)) {
                return
            }
            val window: Window = activity.window
            if (full) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            if (full) {
                activity.actionBar?.hide()
            } else {
                activity.actionBar?.show()
            }
            //updateSizeInfo()
        }
    }
}