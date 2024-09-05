package com.kontranik.koreader.compose.ui.bookmarks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.navigation.NavigationDestination
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.appbar.AppBarAction
import com.kontranik.koreader.database.BookmarksViewModel
import com.kontranik.koreader.database.model.Bookmark
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import kotlinx.coroutines.launch

object BoomkmarksScreenDestination : NavigationDestination {
    override val route = "BookmarksScreen"
    override val titleRes = R.string.bookmarklist
    const val PATH_ARG = "path"
    val routeWithArgs = "$route?path={$PATH_ARG}"
}

@Composable
fun BoomkmarksScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToReader: () -> Unit,
    modifier: Modifier = Modifier,
    bookReaderViewModel: BookReaderViewModel,
    bookmarksViewModel: BookmarksViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    val allBookmarksState = bookmarksViewModel.mAllBookmarks.collectAsState(initial = listOf())

    Scaffold(
        topBar = {
            AppBar (
                title = R.string.bookmarklist,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                            )
                        )
                    }
                },
                appBarActions = listOf {
                    AppBarAction(appBarAction = AppBarAction(
                        icon = R.drawable.ic_iconmonstr_bookmark_add,
                        description = R.string.add_bookmark,
                        onClick = {
                            coroutineScope.launch {
                                bookReaderViewModel.addBookmarkForCurrentPage()
                            }
                        }
                    ))
                }
            )

        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->

        Column(Modifier.padding(padding)) {
            if (allBookmarksState.value.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_bookmarks),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                )
            }
            LazyColumn(state = listState) {
                itemsIndexed(
                    allBookmarksState.value,
                    key = {
                            pos, item -> item.id ?: pos
                    }
                ) { index, item ->
                    BookmarksItem(
                        bookmark = item,
                        onOpen = {
                            coroutineScope.launch {
                                bookReaderViewModel.goToBookmark(item)
                                navigateToReader()
                            }
                        },
                        onDelete = {
                            coroutineScope.launch {
                                bookmarksViewModel.delete(item)
                            }
                        }
                    )
                    if (index < allBookmarksState.value.size-1)
                        HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarksItem(
    bookmark: Bookmark,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var showPopup by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .combinedClickable(
                onClick = { onOpen() },
                onLongClick = { showPopup = true }
            )
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .padding(paddingMedium)
                .fillMaxWidth()) {
            Text(
                text = "${bookmark.text}…",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${bookmark.sort}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (showPopup) {
            BookmarkPopupMenu(
                onDelete = { showPopup = false; onDelete() },
                onOpen = { showPopup = false; onOpen() },
                onClose = { showPopup = false })
        }
    }
}

@PreviewLightDark
@Composable
private fun BookmarksItemPreview() {

    AppTheme {
        Surface {
            BookmarksItem(
                bookmark = Bookmark(text = "this is a bookmarktext", sort = "bookmark position", path = "/path/to/book"),
                onOpen = {  },
                onDelete = {  },
            )
        }
    }
}