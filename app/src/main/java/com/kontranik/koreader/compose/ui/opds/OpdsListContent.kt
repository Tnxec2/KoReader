package com.kontranik.koreader.compose.ui.opds

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.appbar.AppBarAction
import com.kontranik.koreader.compose.ui.shared.ConfirmDialog
import com.kontranik.koreader.compose.ui.shared.CustomInputDialog
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.EntryEditDetails
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.opds.model.toEntryEditDetails
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.launch


@Composable
fun OpdsListContent(
    drawerState: DrawerState,
    entrysState: MutableState<List<Entry>>,
    canAdd: MutableState<Boolean>,
    canSearch: MutableState<Boolean>,
    canReload: MutableState<Boolean>,
    startUrl: MutableState<String>,
    contentTitle: MutableState<String?>,
    contentSubTitle: MutableState<String?>,
    contentAuthor: MutableState<String?>,
    contentIcon: MutableState<ImageBitmap?>,
    searchTerm: MutableState<String>,
    onSearch: () -> Unit,
    navigateBack: () -> Unit,
    reloadPage: () -> Unit,
    onDelete: (Int) -> Unit,
    loadLink: (Link) -> Unit,
    download: (Entry, Link) -> Unit,
    openInBrowser: (Link) -> Unit,
    onSaveOpdsOverviewEntry: (pos: Int, title: String, url: String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {

    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val entryDetails = remember {
        mutableStateOf<Entry?>(null)
    }

    val showEditDialog = remember { mutableStateOf(false) }
    val entryEditPos = remember { mutableStateOf<Int?>(null) }
    val entryEditDetails = remember { mutableStateOf(EntryEditDetails()) }

    val showSearchInputDialog = remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar(
                title = if (entryDetails.value != null) R.string.opds_entry_details else R.string.opds,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = {
                        if (entryDetails.value == null)
                            coroutineScope.launch { navigateBack() }
                        else
                            entryDetails.value = null
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back
                            )
                        )
                    }
                },
                appBarActions = listOf {
                    if (canSearch.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.baseline_search_24,
                            description = R.string.search,
                            onClick = {
                                showSearchInputDialog.value = true
                            }
                        ))
                    if (canAdd.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.baseline_add_24,
                            description = R.string.add_opds,
                            onClick = {
                                entryEditPos.value = entrysState.value.size
                                entryEditDetails.value = EntryEditDetails()
                                showEditDialog.value = true
                            }
                        ))
                    if (canReload.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.baseline_autorenew_24,
                            description = R.string.reload,
                            onClick = {
                                reloadPage()
                            }
                        ))
                }
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        
        if (entryDetails.value == null) {
            OpdsList(
                listState = listState,
                contentTitle = contentTitle,
                contentSubTitle = contentSubTitle,
                contentAuthor = contentAuthor,
                contentIcon = contentIcon,
                searchTerm = searchTerm,
                onSearch = { onSearch() },
                showSearchInputDialog = showSearchInputDialog.value,
                onCloseSearchInputDialog = {showSearchInputDialog.value = false},
                entrysState = entrysState,
                startUrl = startUrl,
                onDelete = onDelete,
                loadLink = loadLink,
                showEditDialog = showEditDialog,
                entryEditPos = entryEditPos,
                entryEditDetails = entryEditDetails,
                openDetails = { entryDetails.value = it },
                onSaveOpdsOverviewEntry = { pos: Int, title: String, url: String ->
                    coroutineScope.launch {
                        onSaveOpdsOverviewEntry(pos, title, url)
                    }
                },
                modifier = Modifier.padding(padding)
            )
        } else {
            OpdsEntryDetailsContent(
                entry = entryDetails.value!!,
                navigateToOpdsEntryLink = { link ->
                    loadLink(link)
                    entryDetails.value = null
                },
                download = { e, link ->
                    download(e, link)
                },
                openInBrowser = { link ->
                    openInBrowser(link)
                },
                startUrl = startUrl.value,
                modifier = Modifier.padding(padding)
            )
        }
    }

}

@Composable
fun OpdsList(
    contentTitle: MutableState<String?>,
    contentSubTitle: MutableState<String?>,
    contentAuthor: MutableState<String?>,
    contentIcon: MutableState<ImageBitmap?>,
    showSearchInputDialog: Boolean,
    searchTerm: MutableState<String>,
    onSearch: () -> Unit,
    onCloseSearchInputDialog: () -> Unit,
    entrysState: MutableState<List<Entry>>,
    startUrl: MutableState<String>,
    onDelete: (Int) -> Unit,
    loadLink: (Link) -> Unit,
    showEditDialog: MutableState<Boolean>,
    entryEditPos: MutableState<Int?>,
    entryEditDetails: MutableState<EntryEditDetails>,
    onSaveOpdsOverviewEntry: (pos: Int, title: String, url: String) -> Unit,
    openDetails: (Entry) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),) {


    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
    val entryDeletePos = remember { mutableStateOf<Int?>(null) }



    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingMedium)
        ) {
            contentIcon.value?.let {
                Image(
                    bitmap = it,
                    contentDescription = stringResource(id = R.string.opds_icon),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(50.dp, 100.dp)
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = paddingSmall)) {
                contentTitle.value?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                contentSubTitle.value?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                contentAuthor.value?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        LazyColumn(state = listState) {
            itemsIndexed(
                items = entrysState.value,
                key = {
                        index, item -> index.toString() + item.title
                }
            ) { index, item ->
                OpdsItem(
                    entry = item,
                    startUrl = startUrl.value,
                    onClick = {
                        if (item.clickLink != null) {
                            item.clickLink.href?.let {
                                loadLink(item.clickLink)
                            }
                        } else {
                            openDetails(item)
                        }
                    },
                    onDelete = {
                        onDelete(index)
                    },
                    onEdit = {
                        entryEditDetails.value = item.toEntryEditDetails()
                        entryEditPos.value = index
                        showEditDialog.value = true
                    }
                )
                if (index < entrysState.value.size - 1)
                    HorizontalDivider()
            }

        }

        if (showEditDialog.value) OpdsOverviewEntryEditDialog(
            editDetailsMutableState = entryEditDetails,
            onSave = {
                entryEditPos.value?.let {
                    onSaveOpdsOverviewEntry(it, entryEditDetails.value.title, entryEditDetails.value.url)
                }
                showEditDialog.value = false
                entryEditPos.value = null
            },
            onClose = {
                showEditDialog.value = false
                entryEditDetails.value = EntryEditDetails()
                entryEditPos.value = null
            }
        )

        if (showDeleteConfirmationDialog.value) ConfirmDialog(
            title = stringResource(id = R.string.opds_item_delete),
            text = stringResource(id = R.string.sure_delete_opds_item),
            onDismissRequest = {
                showDeleteConfirmationDialog.value = false
                entryDeletePos.value = null
            },
            onConfirmation = {
                showDeleteConfirmationDialog.value = false
                entryDeletePos.value?.let { onDelete(it) }
                entryDeletePos.value = null
            }
        )

        if (showSearchInputDialog) CustomInputDialog(
            label = stringResource(id = R.string.search_term),
            onSave = {
                onCloseSearchInputDialog()
                onSearch()
            },
            onClose = {
                onCloseSearchInputDialog()
            },
            initText = searchTerm.value,
            onChange = {
                searchTerm.value = it
            }
        )
    }

}

@PreviewLightDark
@Composable
private fun OpdsContentPreview() {
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf(
            context.getDrawable(R.drawable.book_mockup)?.
            let { ImageUtils.drawableToBitmap(it).asImageBitmap() }
        )
    }


    val entrysState = remember {
        mutableStateOf(
            OPDS_LIST
        )
    }

    AppTheme {
        OpdsListContent(
            drawerState = DrawerState(DrawerValue.Closed),
            entrysState = entrysState,
            canAdd = remember { mutableStateOf(true) },
            canSearch = remember { mutableStateOf(true) },
            canReload = remember { mutableStateOf(true) },
            startUrl = remember { mutableStateOf(OVERVIEW) },
            contentTitle =  remember { mutableStateOf(OVERVIEW) },
            contentSubTitle =  remember { mutableStateOf("Subtitle") },
            contentAuthor =  remember { mutableStateOf("Author") },
            searchTerm =  remember { mutableStateOf("") },
            contentIcon = bitmap,
            navigateBack = {  },
            onSearch = {  },
            reloadPage = {  },
            onDelete = {  _ -> },
            onSaveOpdsOverviewEntry = { _, _, _ ->  },
            loadLink = {},
            download = {e, l ->},
            openInBrowser = {}
        )
    }
}
