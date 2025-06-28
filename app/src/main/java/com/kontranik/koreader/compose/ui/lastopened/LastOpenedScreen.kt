package com.kontranik.koreader.compose.ui.lastopened

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.shared.rememberBookInfoForBookStatus
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.model.BookStatus
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBytes
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoDialog
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import com.kontranik.koreader.model.BookInfoComposable
import kotlinx.coroutines.launch
import java.net.URLDecoder


@Composable
fun LastOpenedScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToReader: (bookPath: String) -> Unit,
    navigateToAuthor: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier,
    bookReaderViewModel: BookReaderViewModel,
    bookStatusViewModel: BookStatusViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    val lastOpenedBooksState = bookStatusViewModel.lastOpenedBooks.collectAsState(initial = listOf())

    var bookInfo by remember { mutableStateOf<BookInfoComposable?>(null) }


    Scaffold(
        topBar = {
            AppBar (
                title = R.string.last_opened,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        )
                        )
                    }
                },
            )

        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->

        Column(Modifier.padding(padding)) {
            LazyColumn(state = listState) {
                itemsIndexed(
                    lastOpenedBooksState.value,
                    key = {
                            _, item -> item.path!!
                    }
                ) { index, item ->
                    LastOpenedItem(
                        bookStatus = item,
                        onClick = { bookInfoComposable ->
                            bookInfo = bookInfoComposable
                        },
                        bookStatusViewModel = bookStatusViewModel
                    )
                    if (index < lastOpenedBooksState.value.size-1)
                        HorizontalDivider()
                }
            }
        }

        bookInfo?.let {
            BookInfoDialog(
                bookInfoComposable = it,
                navigateBack = { bookInfo = null },
                deleteBook = { path ->
                    coroutineScope.launch {
                        bookInfo = null
                        bookStatusViewModel.deleteByPath(path)
                        bookReaderViewModel.bookPath.postValue(null)
                    }
                },
                navigateToReader = { path ->
                    coroutineScope.launch {
                        bookInfo = null
                        bookReaderViewModel.changePath(path)
                        navigateToReader(path)
                    }
                },
                navigateToAuthor = { authorId ->
                    coroutineScope.launch {
                        bookInfo = null
                        navigateToAuthor(authorId)
                    }
                }
            )
        }
    }
}

@Composable
fun LastOpenedItem(
    bookStatus: BookStatus,
    onClick: (bookInfoComposable: BookInfoComposable) -> Unit,
    modifier: Modifier = Modifier,
    bookStatusViewModel: BookStatusViewModel? = null,
) {
    val bookInfoComposableState = rememberBookInfoForBookStatus(bookStatus, bookStatusViewModel)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = { onClick(bookInfoComposableState.value) },
            )
            .fillMaxWidth()
    ) {
        Image(
            bitmap = bookInfoComposableState.value.cover!!,
            contentDescription = bookInfoComposableState.value.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(horizontal = paddingMedium, vertical = paddingSmall)
                .size(width = 50.dp, height = 100.dp)
        )
        Column(
            Modifier
                .padding(end = paddingMedium)
                .fillMaxWidth()) {
            Text(
                text = bookInfoComposableState.value.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
            if (bookInfoComposableState.value.sequenceName.isNotEmpty()) Text(
            text = "${bookInfoComposableState.value.sequenceName} #${bookInfoComposableState.value.sequenceNumber}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
        )
            if (bookInfoComposableState.value.authorsAsString.isNotEmpty()) Text(
                text = bookInfoComposableState.value.authorsAsString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
            Text(
                text = URLDecoder.decode(bookInfoComposableState.value.path),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )

        }
    }
}

@PreviewLightDark
@Composable
private fun LastOpenedItemPreview() {
    val context = LocalContext.current
    val bitmap = AppCompatResources.getDrawable(context, R.drawable.book_mockup)?.let { ImageUtils.drawableToBitmap(it)}
    AppTheme {
        Surface {
            LastOpenedItem(
                bookStatus = BookStatus(
                    cover = getBytes(bitmap),
                    title = "Title",
                    authors = mocupAuthors.joinToString("; ", transform = {it.asString()}),
                    path =  "/path/to/book%25abc",
                    sequenceName = "Series Name",
                    sequenceNumber = "1",
                ),
                onClick = {  },
            )
        }
    }
}