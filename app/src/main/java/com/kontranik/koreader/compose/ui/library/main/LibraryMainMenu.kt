package com.kontranik.koreader.compose.ui.library.main

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.mainmenu.MainMenuItem
import com.kontranik.koreader.compose.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun LibraryMainMenuScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToBooksByTitle: () -> Unit,
    navigateToBooksByAuthor: () -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppBar (
                title = R.string.library,
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

        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

                    MainMenuItem(
                        painterId = R.drawable.ic_folder_black_24dp,
                        menuTextId = R.string.books_by_title,
                        onClick = { coroutineScope.launch { navigateToBooksByTitle() } })

                    MainMenuItem(
                        painterId = R.drawable.ic_folder_black_24dp,
                        menuTextId = R.string.books_by_author,
                        onClick = { coroutineScope.launch { navigateToBooksByAuthor() } })

                    MainMenuItem(
                        painterId = R.drawable.ic_baseline_settings_24,
                        menuTextId = R.string.library_settings,
                        onClick = { coroutineScope.launch { navigateToSettings() } })

        }
    }
}

@Preview
@Composable
private fun LibraryMainMenuScreenPreview() {
    AppTheme {
        LibraryMainMenuScreen(
            drawerState = DrawerState(DrawerValue.Closed),
            navigateBack = {  },
            navigateToBooksByTitle = { },
            navigateToBooksByAuthor = { },
            navigateToSettings = { },
        )
    }

}