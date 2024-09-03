package com.kontranik.koreader.compose.ui.shared

import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun Html(text: String, modifier: Modifier = Modifier) {
    val textColor = MaterialTheme.colorScheme.onBackground

    AndroidView(factory = { context ->
        TextView(context).apply {
            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
            setTextColor(textColor.toArgb())
        }
    }, modifier = modifier)
}