package com.kontranik.koreader.compose.ui.shared

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

@Composable
fun VolumeButtonsHandler(
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    DisposableEffect(context) {
        val keyEventDispatcher = ViewCompat.OnUnhandledKeyEventListenerCompat { _, event ->
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    if (event.action==KeyEvent.ACTION_UP)
                        onVolumeUp()
                    true
                }

                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    if (event.action==KeyEvent.ACTION_UP)
                        onVolumeDown()
                    true
                }
                else -> {
                    false
                }
            }
        }

        ViewCompat.addOnUnhandledKeyEventListener(view, keyEventDispatcher)

        onDispose {
            ViewCompat.removeOnUnhandledKeyEventListener(view, keyEventDispatcher)
        }
    }
}