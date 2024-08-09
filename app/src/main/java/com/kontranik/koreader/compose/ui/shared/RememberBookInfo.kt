package com.kontranik.koreader.compose.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.kontranik.koreader.model.BookInfoComposable
import com.kontranik.koreader.model.toBookInfoComposable
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBitmap
import kotlinx.coroutines.launch

@Composable
fun rememberBookInfo(fileItem: FileItem): MutableState<BookInfoComposable> {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val bookInfoState = remember {
        mutableStateOf(
            BookInfoComposable(fileItem, getBitmap(context, fileItem.image).asImageBitmap())
        )
    }

    LaunchedEffect(key1 = fileItem) {
        coroutineScope.launch {
            if (!fileItem.isDir) {
                val contentUriPath = fileItem.uriString
                if (contentUriPath != null) {
                    EbookHelper.getBookInfo(context, contentUriPath, fileItem)?.toBookInfoComposable()?.let {
                        val cover = ImageUtils.scaleBitmap(
                            it.cover.asAndroidBitmap(), 50, 100)
                            .asImageBitmap()
                        bookInfoState.value = it.copy(cover = cover)
                    }
                }
            }
        }
    }

    return bookInfoState
}