package com.kontranik.koreader.compose.ui.shared

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookInfoComposable
import com.kontranik.koreader.model.toBookInfoComposable
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBitmap
import kotlinx.coroutines.launch

@Composable
fun rememberBookInfoForFileItem(fileItem: FileItem): MutableState<BookInfoComposable> {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val bookInfoState = remember {
        mutableStateOf(
            fileItem.toBookInfoComposable(getBitmap(context, fileItem.image).asImageBitmap())
        )
    }

    LaunchedEffect(key1 = fileItem) {
        coroutineScope.launch {
            if (!fileItem.isDir) {
                val contentUriPath = fileItem.uriString
                if (contentUriPath != null) {
                    EbookHelper.getBookInfo(contentUriPath, fileItem)?.toBookInfoComposable()?.let {
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

@Composable
fun rememberBookInfoForBookStatus(bookStatus: BookStatus, bookStatusViewModel: BookStatusViewModel?): MutableState<BookInfoComposable> {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val bookInfoState = remember {
        mutableStateOf(
            bookStatus.toBookInfoComposable(getBitmap(context, ImageEnum.Ebook).asImageBitmap())
        )
    }

    LaunchedEffect(key1 = bookStatus) {
        coroutineScope.launch {
            if (bookStatus.cover == null) {
                val contentUriPath = bookStatus.path
                if (contentUriPath != null) {
                    if (!FileHelper.contentFileExist(context, bookStatus.path)) {
                        bookStatusViewModel?.delete(bookStatus.id!!)
                    } else {
                        val uri = Uri.parse(contentUriPath)
                        val doc = DocumentFile.fromSingleUri(context, uri)
                        if (doc != null) {
                            val bookInfoTemp: BookInfo? =
                                EbookHelper.getBookInfoTemporary(contentUriPath)
                            bookInfoTemp?.let { bookInfo ->
                                bookInfoState.value = bookInfoState.value.copy(
                                    cover = bookInfo.cover?.let { it1 ->
                                        ImageUtils.scaleBitmap(
                                            it1,
                                            50,
                                            100
                                        ).asImageBitmap()
                                    } ?: bookInfoState.value.cover,
                                    authors = bookInfo.authors ?: mutableListOf(),
                                    authorsAsString = bookInfo.authorsAsString()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    return bookInfoState
}

@Composable
fun rememberBookInfoForLibraryItem(item: LibraryItemWithAuthors): MutableState<BookInfoComposable> {
    val context = LocalContext.current

    val bookInfoState = remember {
        mutableStateOf(
            item.toBookInfoComposable(cover = if (item.libraryItem.cover != null) {
                ImageUtils.getImage(item.libraryItem.cover!!) ?: getBitmap(
                    context,
                    ImageEnum.Ebook
                )
            } else {
                getBitmap(context, ImageEnum.Ebook)
            })
        )
    }

    return bookInfoState
}
