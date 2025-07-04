package com.kontranik.koreader.compose.ui.settings

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.settings.elements.SettingsButton
import com.kontranik.koreader.compose.ui.settings.elements.SettingsCard
import com.kontranik.koreader.compose.ui.settings.elements.SettingsList
import com.kontranik.koreader.compose.ui.settings.elements.SettingsTitle
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLandscapeLightDark
import kotlinx.coroutines.launch


@Composable
fun ColorSettingsScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToTheme: (Int) -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    ColorSettingsContent(
        drawerState = drawerState,
        navigateBack = { coroutineScope.launch { navigateBack() } },
        modifier = modifier,
        selectedTheme = settingsViewModel.selectedColorTheme.intValue,
        onChangeSelectedTheme = {
            coroutineScope.launch {
                settingsViewModel.changeselectedColorTheme(
                    it
                )
            }
        },
        navigateToTheme = { coroutineScope.launch { navigateToTheme(it) } }
    )
}

@Composable
fun ColorSettingsContent(
    selectedTheme: Int,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToTheme: (Int) -> Unit,
    onChangeSelectedTheme: (Int) -> Unit,
) {

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.settings,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier
                .padding(padding)
                .padding(paddingSmall)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {


            SettingsTitle(
                text = stringResource(id = R.string.color_theme),
                modifier = Modifier.padding(bottom = paddingSmall)
            )

            SettingsCard(
                title = stringResource(id = R.string.select_theme),
                modifier = Modifier.padding(bottom = paddingMedium)
            ) {
                val entries = getStringArrayFromResourceArray(res = selected_theme_entries)
                Column {
                    SettingsList(
                        title = stringResource(id = R.string.interface_theme_title),
                        entries = entries,
                        entryValues = entries.mapIndexed { index, s -> s },
                        defaultValue = entries[selectedTheme],
                        icon = R.drawable.ic_iconmonstr_paintbrush_10,
                        onChange = {
                            onChangeSelectedTheme(entries.indexOf(it))
                        },
                        showDefaultValue = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            SettingsCard(title = stringResource(id = R.string.select_theme)) {
                Column {
                    (0..4).toList().map { index ->
                        SettingsButton(
                            title = stringResource(
                                id = R.string.color_theme_indexed_header,
                                index + 1
                            ),
                            defaultValue = null,
                            onClick = { navigateToTheme(index) },
                            showDefaultValue = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 30.dp)
                        )
                    }
                }
            }
        }
    }


}


@PreviewPortraitLandscapeLightDark
@Composable
private fun SettingsContentPreview() {
    AppTheme {
        Surface {
            ColorSettingsContent(
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                selectedTheme = 0,
                onChangeSelectedTheme = {},
                navigateToTheme = {},
            )
        }
    }

}