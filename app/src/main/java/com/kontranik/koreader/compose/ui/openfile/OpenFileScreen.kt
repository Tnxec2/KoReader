package com.kontranik.koreader.compose.ui.openfile

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.kontranik.koreader.compose.ui.appbar.AppBarAction
import com.kontranik.koreader.compose.ui.shared.ConfirmDialog
import com.kontranik.koreader.compose.ui.shared.rememberBookInfoForFileItem
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.compose.ui.library.LibraryViewModel
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoDialog
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.model.BookInfoComposable
import com.kontranik.koreader.model.toBookInfo
import kotlinx.coroutines.launch

@Composable
fun OpenFileScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToReader: (bookPath: String) -> Unit,
    navigateToAuthor: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier,
    bookReaderViewModel: BookReaderViewModel,
    bookStatusViewModel: BookStatusViewModel,
    openFileViewModel: OpenFileViewModel = viewModel(factory = AppViewModelProvider.Factory),
    libraryViewModel: LibraryViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()

    val fileItemListState = openFileViewModel.fileItemList

    var showConfirmOpenStorageDialog by openFileViewModel.showConfirmSelectStorageDialog
    var deleteStoragePosition by remember { mutableStateOf<Int?>(null) }

    val storagePicker = rememberLauncherForActivityResult(
         contract = GetStorageToOpen(),
         onResult = { uri ->
             uri?.let {
                 coroutineScope.launch {
                     context.contentResolver.takePersistableUriPermission(it,
                         Intent.FLAG_GRANT_READ_URI_PERMISSION
                             or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    openFileViewModel.addStoragePath(it.toString())
                 }
             }
         }
    )

    LaunchedEffect(key1 = openFileViewModel.scrollToDocumentFileUriString.value) {
        openFileViewModel.scrollToDocumentFileUriString.value?.let {
            listState.scrollToItem(
                openFileViewModel.getPositionInFileItemList()
            )
        }
    }

    fun addToStorage() {
        coroutineScope.launch {
            println("addToStorage")
            snackbarHostState.showSnackbar("Select directory or storage from dialog, and grant access")
            storagePicker.launch("*/*")
        }
    }


    if (showConfirmOpenStorageDialog) {
        ConfirmDialog(
            title = stringResource(R.string.title_select_storage),
            text = stringResource(R.string.sure_select_storage),
            isCancelable = false,
            onDismissRequest = { showConfirmOpenStorageDialog = false },
            onConfirmation = {
                showConfirmOpenStorageDialog = false
                addToStorage()
            })
    }

    deleteStoragePosition?.let {
        ConfirmDialog(
            title = stringResource(R.string.title_delete_storage),
            text = stringResource(R.string.sure_delete_storage),
            onDismissRequest = { deleteStoragePosition = null },
            onConfirmation = {
                openFileViewModel.deleteStorage(it)
                deleteStoragePosition = null
            })
    }

    var bookInfo by remember { mutableStateOf<BookInfoComposable?>(null) }
    bookInfo?.let {
        BookInfoDialog(
            bookInfoComposable = it,
            navigateBack = { bookInfo = null },
            deleteBook = { path ->
                coroutineScope.launch {
                    bookInfo = null
                    bookStatusViewModel.deleteByPath(path)
                    bookReaderViewModel.bookPath.postValue(null)
                    openFileViewModel.loadPath(null)
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

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar (
                title = R.string.choose_file,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        ))
                    }
                },
                appBarActions = listOf{
                    if (openFileViewModel.isVisibleImageButtonFilechooseAddStorage.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.ic_iconmonstr_folder_add,
                            description = R.string.add_to_storage,
                            onClick = { showConfirmOpenStorageDialog = true }
                        ))
                    if (!openFileViewModel.isVisibleImageButtonFilechooseAddStorage.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.ic_baseline_storage_24,
                            description = R.string.go_to_storage,
                            onClick = { openFileViewModel.storageList() }
                        ))
                    if (!openFileViewModel.isVisibleImageButtonFilechooseAddStorage.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.ic_baseline_arrow_back_24,
                            description = R.string.back,
                            onClick = { openFileViewModel.goBack() }
                        ))
                }
            )

        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->

        Column(Modifier.padding(padding)) {
            LazyColumn(state = listState) {
                itemsIndexed(
                    fileItemListState.value.toList(),
                    key = {
                        _, item -> item.name + item.path
                    }
                ) { index, item ->
                    FileMenuItem(
                        fileItem = item,
                        onClick = { bookInfoItem: BookInfoComposable ->
                            if (item.isDir)
                                openFileViewModel.onFilelistItemClick(index)
                            else
                                item.uriString?.let {
                                    openFileViewModel.scrollToDocumentFileUriString.value = it
                                    coroutineScope.launch { bookInfo = bookInfoItem }
                                }
                        },
                        onDeleteStorage = {
                            deleteStoragePosition = index
                        },
                        onUpdateLibrary = {
                            coroutineScope.launch {
                                item.uriString?.let {
                                    libraryViewModel.readRecursive(
                                        context,
                                        setOf(it))
                                }
                            }
                        },
                        onUpdateBookInfo = { bookInfoComposable ->
                            item.bookInfo = bookInfoComposable.toBookInfo()
                        }
                    )
                    if (index < fileItemListState.value.size-1)
                        HorizontalDivider()
                }
            }
        }
    }
}
