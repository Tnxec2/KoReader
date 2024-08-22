package com.kontranik.koreader.compose.ui.mainmenu

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.utils.PrefsHelper
import com.kontranik.koreader.compose.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToOpenFile: () -> Unit,
    navigateToLastOpened: () -> Unit,
    navigateToBookmarks: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToOpdsNetworkLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToBookInfo: (bookPath: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val bookPath = rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        val prefs = context.getSharedPreferences(ReaderActivity.PREFS_FILE, AppCompatActivity.MODE_PRIVATE)

        if ( prefs.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            bookPath.value = prefs.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }
    }


    Scaffold(
        topBar = {
            AppBar (
                title = R.string.main_menu,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        ))
                    }
                }
            )

        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->

        Column(Modifier.padding(padding)) {
        LazyColumn() {
            item { MainMenuItem(painterId = R.drawable.ic_folder_black_24dp, menuTextId = R.string.openfile, onClick = { coroutineScope.launch{ navigateToOpenFile() } }) }
            item { MainMenuItem(painterId = R.drawable.ic_iconmonstr_book_time, menuTextId = R.string.last_opened, onClick = { coroutineScope.launch{ navigateToLastOpened() } }) }
            item { MainMenuItem(painterId = R.drawable.ic_iconmonstr_bookmark, menuTextId = R.string.bookmarklist, onClick = { coroutineScope.launch{ navigateToBookmarks() } }) }
            item { MainMenuItem(painterId = R.drawable.baseline_local_library_24, menuTextId = R.string.library, onClick = { coroutineScope.launch{ navigateToLibrary() } }) }
            item { MainMenuItem(painterId = R.drawable.networking_1, menuTextId = R.string.opds, onClick = { coroutineScope.launch{ navigateToOpdsNetworkLibrary() } }) }
            item { MainMenuItem(painterId = R.drawable.ic_baseline_settings_24, menuTextId = R.string.settings, onClick = { coroutineScope.launch{ navigateToSettings() } }) }
        }

        bookPath.value?.let {
            Spacer(modifier = Modifier
                .weight(1f)
                .fillMaxWidth())
            Box(
                modifier = modifier
                    .clickable(
                        onClick = {coroutineScope.launch { navigateToBookInfo(it)}},
                        role = Role.Button,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_iconmonstr_book_info),
                    contentDescription = stringResource(id = R.string.bookinfo),
                    modifier = Modifier
                        .padding(paddingSmall)
                        .size(32.dp)
                )
            }
        }

        }

    }
}

@Composable
fun MainMenuItem(
    @DrawableRes painterId: Int,
    @StringRes menuTextId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
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
        )
    }
}

@Preview
@Composable
private fun MainMenuScreenPreview() {
    AppTheme {
        MainMenuScreen(
            drawerState = DrawerState(DrawerValue.Closed),
            navigateBack = {  },
            navigateToOpenFile = { },
            navigateToLastOpened = { },
            navigateToLibrary = {  },
            navigateToOpdsNetworkLibrary = { },
            navigateToSettings = { },
            navigateToBookInfo = {  },
            navigateToBookmarks = {  },
        )
    }

}