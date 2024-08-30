package com.kontranik.koreader.compose.ui.reader

import android.graphics.Point
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.ui.components.BookReaderTextview
import com.kontranik.koreader.ui.components.BookReaderTextview.BookReaderTextviewListener
import kotlinx.coroutines.launch

@Composable
fun BookReaderTextviewViewAsComposable(
    onSetTextView: (BookReaderTextview) -> Unit,
    modifier: Modifier = Modifier,
    bookReaderViewModel: BookReaderViewModel,

){

    AndroidView(
        factory={ ctx ->
            BookReaderTextview(ctx, bookReaderViewModel).apply{
                onSetTextView(this)
            }
        },
        modifier=modifier
    )
}