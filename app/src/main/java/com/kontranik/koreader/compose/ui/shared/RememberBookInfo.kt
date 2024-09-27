package com.kontranik.koreader.compose.ui.shared

import android.graphics.Bitmap
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
import com.kontranik.koreader.KoReaderApplication

import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookInfoComposable
import com.kontranik.koreader.model.toBookInfoComposable
import com.kontranik.koreader.model.toBookInfoComposableForImageBitmap
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBitmap
import kotlinx.coroutines.launch

@Composable
fun rememberBookInfoUiStateForPath(
    bookPath: String,
    bookInfoComposable: BookInfoComposable
    ): MutableState<BookInfoUiState> {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val bookInfoState = remember {
        mutableStateOf(
            BookInfoUiState(
                bookInfoComposable,
                canDelete = false,
                exit = false
            )
        )
    }

    LaunchedEffect(key1 = bookPath) {
        coroutineScope.launch {
            val uri = Uri.parse(bookPath)
            val doc = DocumentFile.fromSingleUri(context, uri)

            val ebookHelper = EbookHelper.getHelper(bookPath)

            if (ebookHelper != null) {
                val bookInfo = ebookHelper.getBookInfoTemporary(bookPath)

                if (bookInfo != null) {
                    bookInfoState.value = BookInfoUiState(
                        bookInfo.toBookInfoComposable(),
                        canDelete = doc?.canRead() == true,
                        exit = false
                    )
                    KoReaderApplication.getApplicationScope().launch {
                        val libraryItemWithAuthors = KoReaderApplication.getContainer().
                            libraryItemRepository.getByPathWithAuthors(bookPath).firstOrNull()

                        libraryItemWithAuthors?.let { item ->
                            bookInfoState.value =
                                BookInfoUiState(
                                    item.toBookInfoComposableForImageBitmap(
                                        bookInfoState.value.bookInfoComposable.cover,
                                        bookInfoState.value.bookInfoComposable.annotation
                                    )
                                )
                        }
                    }
                } else {
                    bookInfoState.value = bookInfoState.value.copy(
                        exit = true
                    )
                }
            } else {
                bookInfoState.value = bookInfoState.value.copy(
                    exit = true
                )
            }
        }
    }

    return bookInfoState
}


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
            try {
                if (!fileItem.isDir) {
                    val contentUriPath = fileItem.uriString
                    if (contentUriPath != null) {
                        EbookHelper.getBookInfo(contentUriPath, fileItem)?.toBookInfoComposable()?.let {
                            val cover = ImageUtils.scaleBitmap(
                                it.cover!!.asAndroidBitmap(), 50, 100)
                                .asImageBitmap()
                            bookInfoState.value = it.copy(cover = cover)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
            item.toBookInfoComposable(
                cover = if (item.libraryItem.cover != null) {
                    ImageUtils.getImage(item.libraryItem.cover!!) ?: getBitmap(
                        context,
                        ImageEnum.Ebook
                    )
                } else {
                    getBitmap(context, ImageEnum.Ebook)
                }
            )
        )
    }

    return bookInfoState
}

data class BookInfoUiState(
    val bookInfoComposable: BookInfoComposable = BookInfoComposable(),
    val canDelete: Boolean = false,
    val exit: Boolean = false
)


data class BookInfoDetails(
    var title: String = "",
    var cover: Bitmap? = null,
    var authors: List<Author> = listOf(),
    val allAuthors: String = "",
    val filename: String = "",
    val path: String = "",
    var annotation: String = ""
)

fun BookInfo.toBookInfoDetails(): BookInfoDetails {
    return BookInfoDetails(
        title = title ?: "",
        cover = cover,
        authors = authors?.toList() ?: listOf(),
        allAuthors = authorsAsString(),
        filename = filename,
        path = path,
        annotation = annotation
    )
}