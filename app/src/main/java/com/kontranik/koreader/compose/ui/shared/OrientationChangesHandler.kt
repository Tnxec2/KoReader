package com.kontranik.koreader.compose.ui.shared

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun OrientationChangesHandler(
    portraitLayout: @Composable () -> Unit,
    landscapeLayout: @Composable  () -> Unit,
    ) {

    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        landscapeLayout.invoke()
    } else {
        portraitLayout.invoke()
    }
}