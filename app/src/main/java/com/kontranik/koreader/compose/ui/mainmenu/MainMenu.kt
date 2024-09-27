package com.kontranik.koreader.compose.ui.mainmenu

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoDialog
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import com.kontranik.koreader.compose.ui.settings.PREFS_FILE
import com.kontranik.koreader.compose.ui.settings.PREF_BOOK_PATH
import com.kontranik.koreader.database.BookStatusViewModel
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToOpenFile: () -> Unit,
    navigateToLastOpened: () -> Unit,
    navigateToBookmarks: (bookPath: String) -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToOpdsNetworkLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAuthor: (authorId: Long) -> Unit,
    bookStatusViewModel: BookStatusViewModel,
    bookReaderViewModel: BookReaderViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val bookPath = rememberSaveable { mutableStateOf<String?>(null) }

    var bookInfoPath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        val prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

        if (prefs.contains(PREF_BOOK_PATH)) {
            bookPath.value = prefs.getString(PREF_BOOK_PATH, null)
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.main_menu,
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
                }
            )

        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                MainMenuItem(
                    painterId = R.drawable.ic_folder_black_24dp,
                    menuTextId = R.string.openfile,
                    onClick = { coroutineScope.launch { navigateToOpenFile() } })
                MainMenuItem(
                    painterId = R.drawable.ic_iconmonstr_book_time,
                    menuTextId = R.string.last_opened,
                    onClick = { coroutineScope.launch { navigateToLastOpened() } })
                MainMenuItem(
                    painterId = R.drawable.baseline_local_library_24,
                    menuTextId = R.string.library,
                    onClick = { coroutineScope.launch { navigateToLibrary() } })
                MainMenuItem(
                    painterId = R.drawable.networking_1,
                    menuTextId = R.string.opds,
                    onClick = { coroutineScope.launch { navigateToOpdsNetworkLibrary() } })
                MainMenuItem(
                    painterId = R.drawable.ic_baseline_settings_24,
                    menuTextId = R.string.settings,
                    onClick = { coroutineScope.launch { navigateToSettings() } })

            }
            bookPath.value?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_iconmonstr_book_info),
                        contentDescription = stringResource(id = R.string.bookinfo),
                        modifier = Modifier
                            .padding(paddingSmall)
                            .size(32.dp)
                            .clickable(
                                onClick = {
                                    bookInfoPath = it
                                },
                                role = Role.Button,
                            ),
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_iconmonstr_bookmark),
                        contentDescription = stringResource(id = R.string.bookmarklist),
                        modifier = Modifier
                            .padding(paddingSmall)
                            .size(32.dp)
                            .clickable(
                                onClick = { coroutineScope.launch { navigateToBookmarks(it) } },
                                role = Role.Button,
                            ),
                    )
                }
            }

            bookInfoPath?.let {
                BookInfoDialog(
                    bookPath = it,
                    navigateBack = { bookInfoPath = null },
                    deleteBook = { path ->
                        coroutineScope.launch {
                            bookInfoPath = null
                            bookStatusViewModel.deleteByPath(path)
                            bookReaderViewModel.bookPath.postValue(null)
                        }
                    },
                    navigateToReader = { path ->
                        coroutineScope.launch {
                            bookInfoPath = null
                            navigateBack()
                        }
                    },
                    navigateToAuthor = { authorId ->
                        coroutineScope.launch {
                            bookInfoPath = null
                            navigateToAuthor(authorId)
                        }
                    }
                )
            }

        }

    }
}

@Composable
fun MainMenuItem(
    @DrawableRes painterId: Int,
    @StringRes menuTextId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = paddingMedium)
    ) {
        Icon(
            painter = painterResource(id = painterId),
            contentDescription = stringResource(id = menuTextId),
            modifier = Modifier
                .padding(paddingSmall)
                .size(32.dp)
        )
        Text(
            text = stringResource(id = menuTextId),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(start = paddingSmall)
                .padding(vertical = paddingMedium)
        )
    }
}
