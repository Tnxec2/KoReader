package com.kontranik.koreader.compose.ui.library.bytitle

import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.navigation.NavigationDestination
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.library.LibraryViewModel
import com.kontranik.koreader.compose.ui.shared.rememberBookInfoForLibraryItem
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoDialog
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import com.kontranik.koreader.compose.ui.shared.ConfirmDialog
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.model.BookInfoComposable
import kotlinx.coroutines.launch

object LibraryByTitleDestination : NavigationDestination {
    override val route = "LibraryByTitle"
    override val titleRes = R.string.books_by_title
    const val KEY_AUTHOR_ID = "authorid"
    const val KEY_TITLE = "title"
    val routeWithArgs = "$route?authorid={$KEY_AUTHOR_ID}&title={$KEY_TITLE}"
}

@Composable
fun LibraryByTitleScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToReader: (bookPath: String) -> Unit,
    navigateToAuthor: (authorId: Long) -> Unit,
    bookReaderViewModel: BookReaderViewModel,
    bookStatusViewModel: BookStatusViewModel,
    modifier: Modifier = Modifier,
    libraryViewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()

    val booksPagingState = libraryViewModel.libraryTitlePageByFilter.collectAsLazyPagingItems()

    var filter by remember {
        mutableStateOf(libraryViewModel.libraryTitleSearchFilter.value ?: "")
    }

    val author by libraryViewModel.authorState

    var confirmDeleteBook by remember {
        mutableStateOf<Pair<Boolean, LibraryItemWithAuthors>?>(null)
    }

    var bookInfo by remember { mutableStateOf<BookInfoComposable?>(null) }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar (
                title = R.string.books_by_title,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        ))
                    }
                },
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(Modifier.padding(padding)) {
            author?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = paddingSmall)
                ) {
                    Text(text = stringResource(id = R.string.by_author, it.asString()))
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingSmall)) {
                OutlinedTextField(
                    value = filter,
                    onValueChange = {
                        filter = it
                        coroutineScope.launch {
                            libraryViewModel.changeTitleSearchText(filter)
                            listState.scrollToItem(0)
                        }
                    },
                    label = {
                        Text(text = stringResource(id = R.string.search_term))
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                    coroutineScope.launch {
                        filter = ""
                        libraryViewModel.changeTitleSearchText(filter)
                        listState.scrollToItem(0)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_backspace_24),
                        contentDescription = stringResource(id = R.string.clear_search_text)
                    )
                }
            }

            LazyColumn(state = listState) {
                items(
                    count = booksPagingState.itemCount,
                    key = booksPagingState.itemKey { item -> item.libraryItem.id.toString() }
                ) { index ->
                    booksPagingState[index]?.let {
                        BooksItem(
                            item = it,
                            onClick = { bookInfoComposable ->
                                coroutineScope.launch {
                                    val bookPathUri = it.libraryItem.path
                                    try {
                                        val inputStream =
                                            context.contentResolver.openInputStream(
                                                Uri.parse(bookPathUri)
                                            )
                                        inputStream?.close()
                                        bookInfo = bookInfoComposable
                                    } catch (e: Exception) {
                                        confirmDeleteBook = Pair(true, it)
                                    }
                                }
                            },
                            onDelete = {
                                confirmDeleteBook = Pair(false, it)
                            },
                            onUpdate = {
                                libraryViewModel.updateLibraryItem(it)
                            }
                        )
                        if (index < booksPagingState.itemCount-1)
                            HorizontalDivider()
                    }
                }
            }

            confirmDeleteBook?.let {
                ConfirmDialog(
                    title = if (it.first) "Book does not exist" else null,
                    text = "Are you sure you want to delete this book \"${it.second.libraryItem.title}\" from library?",
                    onDismissRequest = { confirmDeleteBook = null },
                    onConfirmation = {
                        confirmDeleteBook = null
                        libraryViewModel.delete(it.second)
                    }
                )
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BooksItem(
    item: LibraryItemWithAuthors,
    onClick: (bookInfoComposable: BookInfoComposable) -> Unit,
    onDelete: () -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier,) {

    var showPopup by rememberSaveable { mutableStateOf(false) }

    val bookInfoComposableState = rememberBookInfoForLibraryItem(item)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .combinedClickable(
                onClick = { onClick(bookInfoComposableState.value) },
                onLongClick = { showPopup = true }
            )
            .fillMaxWidth()
    ) {
        Image(
            bitmap = bookInfoComposableState.value.cover!!,
            contentDescription = bookInfoComposableState.value.title,
            modifier = Modifier
                .padding(horizontal = paddingMedium, vertical = paddingSmall)
                .size(width = 50.dp, height = 100.dp)
        )
        Column(
            Modifier
                .padding(end = paddingMedium)
                .fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = bookInfoComposableState.value.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showPopup = true }) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "show popup")
                }
            }
            if (bookInfoComposableState.value.sequenceName.isNotEmpty()) {
                Text(
                    text = "${bookInfoComposableState.value.sequenceName} #${bookInfoComposableState.value.sequenceNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                )
            }
            if (bookInfoComposableState.value.authorsAsString.isNotEmpty()) Text(
                text = bookInfoComposableState.value.authorsAsString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
            Text(
                text = bookInfoComposableState.value.filename,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
        }
        if (showPopup) {
            BooksItemPopupMenu(
                title = bookInfoComposableState.value.title,
                onDelete = { showPopup = false; onDelete() },
                onUpdate = { showPopup = false; onUpdate() },
                onClose = { showPopup = false })
        }
    }
}

@PreviewLightDark
@Composable
private fun BooksItemPreview() {
    val context = LocalContext.current
    val bitmap = AppCompatResources.getDrawable(context, R.drawable.book_mockup)?.let { ImageUtils.getBytes(ImageUtils.drawableToBitmap(it)) }
    AppTheme {
        Surface {
            BooksItem(
                item = LibraryItemWithAuthors(
                    libraryItem = LibraryItem(
                        cover = bitmap,
                        title = "Title",
                        path =  "/path/to/book",
                        sequenceName = "Series Name",
                        sequenceNumber = ""
                    ),
                    authors = mocupAuthors
                ),
                onClick = {  },
                onDelete = {  },
                onUpdate = {  },
            )
        }
    }
}