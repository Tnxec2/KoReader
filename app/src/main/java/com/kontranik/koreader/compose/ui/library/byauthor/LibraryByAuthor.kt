package com.kontranik.koreader.compose.ui.library.byauthor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.navigation.NavigationDestination
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.library.LibraryViewModel
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.compose.theme.AppTheme
import kotlinx.coroutines.launch

object LibraryByAuthorDestination : NavigationDestination {
    override val route = "LibraryByAuthor"
    override val titleRes = R.string.books_by_author
}

@Composable
fun LibraryByAuthorScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToAuthor: (Author) -> Unit,
    modifier: Modifier = Modifier,
    libraryViewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()

    val booksPagingState = libraryViewModel.libraryAuthorPageByFilter.collectAsLazyPagingItems()

    var filter by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = Unit) {
        libraryViewModel.loadAuthorPageInit()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar (
                title = R.string.books_by_author,
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
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingSmall)) {
                OutlinedTextField(
                    value = filter,
                    onValueChange = {
                        filter = it
                        coroutineScope.launch {
                            libraryViewModel.changeAuthorSearchText(filter)
                            listState.scrollToItem(0)
                        }
                    },
                    label = {
                        Text(text = stringResource(id = R.string.search_term))
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    coroutineScope.launch {
                        filter = ""
                        libraryViewModel.changeAuthorSearchText(filter)
                        listState.scrollToItem(0)
                    }
                }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_backspace_24),
                        contentDescription = stringResource(id = R.string.clear_search_text)
                    )
                }
            }

            LazyColumn(state = listState) {
                items(count = booksPagingState.itemCount,
                    key = { index -> booksPagingState[index]?.id ?: 0 }) { index ->
                    val item = booksPagingState[index]
                    item?.let {
                        AuthorItem(
                            item = it,
                            onClick = {
                                navigateToAuthor(it)
                            },
                            onDelete = {
                                // TODO: libraryViewModel.delete(it)
                            },
                        )
                        if (index < booksPagingState.itemCount-1)
                            HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AuthorItem(
    item: Author,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,) {

    var showPopup by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { showPopup = true }
            )
            .fillMaxWidth()
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.asString(),
                    modifier = Modifier.weight(1f)
                        .padding(paddingSmall)
                )
//                IconButton(onClick = { showPopup = true }) {
//                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "show popup")
//                }
            }
        }

        if (showPopup) {
            AuthorsItemPopupMenu(
                title = item.asString(),
                onDelete = { showPopup = false; onDelete() },
                onClose = { showPopup = false })
        }
    }
}

@PreviewLightDark
@Composable
private fun AuthorItemPreview() {

    AppTheme {
        Surface {
            AuthorItem(
                item = mocupAuthors[0],
                onClick = {  },
                onDelete = {  },
            )
        }
    }
}