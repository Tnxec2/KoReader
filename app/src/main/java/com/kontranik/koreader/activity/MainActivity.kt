package com.kontranik.koreader.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kontranik.koreader.compose.MainCompose
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide()

        requestWindowFeature(Window.FEATURE_NO_TITLE)


        // Vollbildmodus aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())


        // enableEdgeToEdge()
        setContent {
            MainCompose(
                activity = this
            )
        }
    }


}