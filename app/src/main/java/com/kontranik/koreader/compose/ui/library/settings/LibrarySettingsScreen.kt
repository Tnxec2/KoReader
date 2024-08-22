package com.kontranik.koreader.compose.ui.library.settings

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.appbar.AppBarAction
import com.kontranik.koreader.compose.ui.openfile.GetStorageToOpen
import com.kontranik.koreader.compose.ui.shared.ConfirmDialog
import com.kontranik.koreader.compose.ui.library.LibraryViewModel
import com.kontranik.koreader.compose.theme.AppTheme
import kotlinx.coroutines.launch

const val PREFS_FILE = "LibraryActivitySettings"
const val PREF_SCAN_POINTS = "LibraryScanPoints"

@Composable
fun LibrarySettingsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    libraryViewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val settings = context.getSharedPreferences(
        PREFS_FILE,
        Context.MODE_PRIVATE)

    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()

    val scanPointList = remember {mutableStateOf<Set<String>>(setOf())    }
    val refreshInProgress = libraryViewModel.refreshInProgress.observeAsState(false)

    var showConfirmDialogRefreshLibrary by remember { mutableStateOf(false) }
    var showConfirmDialogClearLibrary by remember { mutableStateOf(false) }

    var showConfirmOpenScanpointDialog by remember { mutableStateOf(false) }
    var deleteScanPointPosition by remember { mutableStateOf<Int?>(null) }

    fun savePrefs() {
        val prefEditor = settings.edit()

        if ( scanPointList.value.isNotEmpty()) {
            prefEditor.putStringSet(PREF_SCAN_POINTS, scanPointList.value.toMutableSet())
        } else {
            prefEditor.remove(PREF_SCAN_POINTS)
        }
        prefEditor.apply()
    }


    val storagePicker = rememberLauncherForActivityResult(
         contract = GetStorageToOpen(),
         onResult = { uri ->
             uri?.let {
                 coroutineScope.launch {
                     println("storagePicker: $it")
                     scanPointList.value = scanPointList.value.plus(it.toString())
                     savePrefs()
                 }
             }
         })

    LaunchedEffect(key1 = Unit) {
        if ( settings.contains(PREF_SCAN_POINTS)) {
            scanPointList.value = settings.getStringSet(PREF_SCAN_POINTS, null) ?: setOf()
        }
        libraryViewModel.createNotificationChannel(context)
    }

    fun addToScanPoint() {
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Select directory or storage from dialog, and grant access")
            storagePicker.launch("*/*")
        }
    }

    if (showConfirmOpenScanpointDialog) {
        ConfirmDialog(
            title = stringResource(R.string.title_select_scanpoint),
            text = stringResource(R.string.sure_select_scanpoint),
            isCancelable = false,
            onDismissRequest = { showConfirmOpenScanpointDialog = false },
            onConfirmation = {
                showConfirmOpenScanpointDialog = false
                addToScanPoint()
            })
    }

    if (showConfirmDialogClearLibrary) {
        ConfirmDialog(
            title = stringResource(R.string.clear_library),
            text = stringResource(R.string.are_you_sure_to_clear_the_whole_library),
            isCancelable = false,
            onDismissRequest = { showConfirmDialogClearLibrary = false },
            onConfirmation = {
                showConfirmDialogClearLibrary = false
                libraryViewModel.deleteAll()
            })
    }

    if (showConfirmDialogRefreshLibrary) {
        ConfirmDialog(
            title = stringResource(R.string.refresh_library),
            text = stringResource(R.string.are_you_sure_to_refresh_the_library),
            isCancelable = false,
            onDismissRequest = { showConfirmDialogRefreshLibrary = false },
            onConfirmation = {
                showConfirmDialogRefreshLibrary = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.start_refresh))
                    libraryViewModel.readRecursive(context, scanPointList.value)
                }
            })
    }

    deleteScanPointPosition?.let {
        ConfirmDialog(
            title = stringResource(R.string.title_delete_scanpoint),
            text = stringResource(R.string.sure_delete_scanpoint),
            onDismissRequest = { deleteScanPointPosition = null },
            onConfirmation = {
                scanPointList.value = scanPointList.value.filterIndexed { index, s -> index !=  deleteScanPointPosition }.toSet()
                savePrefs()
                deleteScanPointPosition = null
            })
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar (
                title = R.string.library_settings,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        ))
                    }
                },
                appBarActions = listOf{
                    if (scanPointList.value.isNotEmpty() && refreshInProgress.value.not() )
                        AppBarAction(appBarAction = AppBarAction(
                            icon = R.drawable.baseline_autorenew_24,
                            description = R.string.refresh_library,
                            onClick = { coroutineScope.launch {
                                libraryViewModel.readRecursive(context, scanPointList.value)
                            } }
                        ))
                    AppBarAction(appBarAction = AppBarAction(
                        icon = R.drawable.ic_iconmonstr_folder_add,
                        description = R.string.add_scan_point,
                        onClick = { showConfirmOpenScanpointDialog = true }
                    ))
                }
            )

        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->

        Column(Modifier.padding(padding)) {
            LazyColumn(state = listState, modifier = Modifier
                .weight(1f)) {
                itemsIndexed(
                    scanPointList.value.toList(),
                ) { index, item ->
                    LibrarySettingsScreenItem(
                        scanPoint = item,
                        onDeleteStorage = {
                            deleteScanPointPosition = index
                        },
                        onUpdateLibrary = {
                            coroutineScope.launch {
                                libraryViewModel.readRecursive(context, setOf(item))
                            }
                        }
                    )
                    if (index < scanPointList.value.size-1)
                        HorizontalDivider()
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(paddingSmall)
            ) {
                OutlinedButton(
                    onClick = { showConfirmDialogClearLibrary = true }) {
                Icon(painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                    contentDescription = stringResource(id = R.string.clear_library))
                Text(text = stringResource(id = R.string.clear_library))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibrarySettingsScreenItem(
    scanPoint: String,
    onDeleteStorage: () -> Unit,
    onUpdateLibrary: () -> Unit,
    modifier: Modifier = Modifier) {

    val context = LocalContext.current
    var showPopup by rememberSaveable { mutableStateOf(false) }

    val directoryUri = remember {
        mutableStateOf(Uri.parse(scanPoint)
            ?: throw IllegalArgumentException("Must pass URI of directory to open"))
    }
    val scanPointText = remember {
        derivedStateOf {
            DocumentFile.fromTreeUri(
                context, directoryUri.value)?.uri?.pathSegments?.last() ?: scanPoint
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    showPopup = true
                }
            )
            .fillMaxWidth()
            .padding(paddingMedium)
    ) {
        Text(
                text = scanPointText.value,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {  showPopup = true }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "popup")
        }

        if (showPopup) {
            LibrarySettingsPopupMenu(
                scanpoint = scanPointText.value,
                onDelete = { showPopup = false; onDeleteStorage() },
                onUpdateLibrary = { showPopup = false; onUpdateLibrary() },
                onClose = { showPopup = false })
        }
    }
}

@PreviewLightDark
@Composable
private fun LibrarySettingsScreenItemPreview() {
    AppTheme {
        Surface {
            LibrarySettingsScreenItem(
            scanPoint = "Scanpoint",
            onUpdateLibrary = {},
            onDeleteStorage = {},
        )
        }
    }
}