package com.kontranik.koreader.compose.ui.opds

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.kontranik.koreader.utils.UrlHelper
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
    val context = LocalContext.current

    var entryDetails by remember {
        mutableStateOf<Entry?>(null)
    }

    val showEditDialog = remember { mutableStateOf(false) }
    val entryEditPos = remember { mutableStateOf<Int?>(null) }
    val entryEditDetails = remember { mutableStateOf(EntryEditDetails()) }

    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
    val entryDeletePos = remember { mutableStateOf<Int?>(null) }

    val showSearchInputDialog = remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar(
                title = R.string.opds,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
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
                                // todo: search
                            }
                        ))
                    if (canAdd.value)
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.baseline_add_24,
                            description = R.string.add_opds,
                            onClick = {
                                entryEditPos.value = null
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
        Column(Modifier.padding(padding)) {
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
                                entryDetails = item
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
                    coroutineScope.launch {
                        entryEditPos.value?.let {
                            onSaveOpdsOverviewEntry(it, entryEditDetails.value.title, entryEditDetails.value.url)
                        }
                    }
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

            if (showSearchInputDialog.value) CustomInputDialog(
                label = stringResource(id = R.string.search_term),
                onSave = {
                    showSearchInputDialog.value = false
                    onSearch()
                },
                onClose = {
                    showSearchInputDialog.value = false
                },
                initText = searchTerm.value,
                onChange = {
                    searchTerm.value = it
                }
            )
        }

        entryDetails?.let { entry ->
            OpdsEntryDetailsDialog(
                onClose = { entryDetails = null },
                entry = entry,
                navigateToOpdsEntryLink = { link ->
                    loadLink(link)
                    entryDetails = null
                },
                download = { e, link ->
                    download(e, link)
                },
                openInBrowser = { link ->
                    openInBrowser(link)
                },
                startUrl = startUrl.value
            )
        }
    }

}

@PreviewLightDark
@Composable
private fun OpdsContentPreview() {
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf(
            AppCompatResources.getDrawable(context, R.drawable.book_mockup)?.
            let { ImageUtils.drawableToBitmap(it).asImageBitmap() }
        )
    }

    val array = stringArrayResource(id = R.array.opds_list)

    val entrysState = remember {
        mutableStateOf(
            array.map {
                Entry(title = it.split("|").first())
            }
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
