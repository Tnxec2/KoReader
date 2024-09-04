package com.kontranik.koreader.compose.ui.openfile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.scrollBy
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
import kotlinx.coroutines.launch

@Composable
fun OpenFileScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToBookInfo: (bookPath: String) -> Unit,
    modifier: Modifier = Modifier,
    fileChooseFragmentViewModel: OpenFileViewModel = viewModel(factory = AppViewModelProvider.Factory),
    libraryViewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()

    val fileItemListState = fileChooseFragmentViewModel.fileItemList

    var showConfirmOpenStorageDialog by remember { mutableStateOf(false) }
    var deleteStoragePosition by remember { mutableStateOf<Int?>(null) }

    val storagePicker = rememberLauncherForActivityResult(
         contract = GetStorageToOpen(),
         onResult = { uri ->
             uri?.let {
                 coroutineScope.launch {
                    println("storagePicker: $it")
                    fileChooseFragmentViewModel.addStoragePath(it.toString())
                 }
             }
         })

    LaunchedEffect(key1 = fileChooseFragmentViewModel.scrollToDocumentFileUriString) {
        fileChooseFragmentViewModel.scrollToDocumentFileUriString.value?.let {
            listState.scrollToItem(
                fileChooseFragmentViewModel.getPositionInFileItemList()
            )
            val layoutInfo = listState.layoutInfo
            val viewportHeight =
                layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset
            val lastItem = listState.layoutInfo.visibleItemsInfo.last()
            if (lastItem.offset + lastItem.size > viewportHeight) {
                listState.scrollBy(lastItem.offset + lastItem.size - viewportHeight.toFloat())
            }
        }
    }

    LaunchedEffect(key1 = fileChooseFragmentViewModel.showConfirmSelectStorageDialog) {
        showConfirmOpenStorageDialog = fileChooseFragmentViewModel.showConfirmSelectStorageDialog.value
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
                fileChooseFragmentViewModel.deleteStorage(it)
                deleteStoragePosition = null
            })
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
                    if (fileChooseFragmentViewModel.isVisibleImageButtonFilechooseAddStorage.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.ic_iconmonstr_folder_add,
                            description = R.string.add_to_storage,
                            onClick = { showConfirmOpenStorageDialog = true }
                        ))
                    if (!fileChooseFragmentViewModel.isVisibleImageButtonFilechooseAddStorage.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.ic_baseline_storage_24,
                            description = R.string.go_to_storage,
                            onClick = { fileChooseFragmentViewModel.storageList() }
                        ))
                    if (!fileChooseFragmentViewModel.isVisibleImageButtonFilechooseAddStorage.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.ic_baseline_arrow_back_24,
                            description = R.string.back,
                            onClick = { fileChooseFragmentViewModel.goBack() }
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
                        onClick = {
                            if (item.isDir)
                                fileChooseFragmentViewModel.onFilelistItemClick(index)
                            else
                                item.uriString?.let {coroutineScope.launch { navigateToBookInfo(it) } }
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
                        }
                    )
                    if (index < fileItemListState.value.size-1)
                        HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileMenuItem(
    fileItem: FileItem,
    onClick: () -> Unit,
    onDeleteStorage: () -> Unit,
    onUpdateLibrary: () -> Unit,
    modifier: Modifier = Modifier) {

    val bookInfoComposableState = rememberBookInfoForFileItem(fileItem)

    var showPopup by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = {
                    if (fileItem.isDir || fileItem.isStorage) showPopup = true
                }
            )
            .fillMaxWidth()
    ) {
        Image(
            bitmap = bookInfoComposableState.value.cover,
            contentDescription = bookInfoComposableState.value.title,
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
            if (bookInfoComposableState.value.authorsAsString.isNotEmpty()) Text(
                text = bookInfoComposableState.value.authorsAsString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
            Text(
                text = fileItem.path,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
        }
        if (showPopup) {
            FielItemPopupMenu(
                fileItem = fileItem,
                onDelete = { showPopup = false; onDeleteStorage() },
                onUpdateLibrary = { showPopup = false; onUpdateLibrary() },
                onClose = { showPopup = false })
        }
    }
}

@PreviewLightDark
@Composable
private fun FileMenuItemPreview() {
    val context = LocalContext.current
    val bitmap = context.getDrawable(R.drawable.book_mockup)?.let { ImageUtils.drawableToBitmap(it)}
    AppTheme {
        Surface {
        FileMenuItem(
            fileItem = FileItem(
                image = ImageEnum.Ebook,
             name = "Title",
             path =  "/path/to/book",
             uriString = null,
             isDir = false,
             isRoot = false,
             bookInfo = BookInfo(
                 title = "Book title",
                 cover = bitmap,
                 authors = mocupAuthors,
                 filename = "book",
                 path = "/path/to/book",
                annotation = "annotation"
             ),
             isStorage = false
            ),
            onClick = {  },
            onUpdateLibrary = {},
            onDeleteStorage = {},
        )
        }
    }
}