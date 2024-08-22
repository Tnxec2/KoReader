package com.kontranik.koreader.compose.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLandscapeLightDark
import kotlinx.coroutines.launch

data class SettingsItem(
    @StringRes val title: Int,
    @DrawableRes val drawable:  Int?,
    val onClick: () -> Unit,
)

@Composable
fun RootSettingsScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToInterfaceSettings: () -> Unit,
    navigateToColorThemeSettings: () -> Unit,
    navigateToTextSettings: () -> Unit,
    navigateToTapZonesSettings: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()

    SettingsContent(
        drawerState = drawerState,
        navigateToInterfaceSettings = { coroutineScope.launch { navigateToInterfaceSettings() } },
        navigateToColorThemeSettings = { coroutineScope.launch { navigateToColorThemeSettings() } },
        navigateToTextSettings = { coroutineScope.launch { navigateToTextSettings() } },
        navigateToTapZonesSettings = { coroutineScope.launch { navigateToTapZonesSettings() } },
        navigateBack = { coroutineScope.launch { navigateBack() }},
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateToInterfaceSettings: () -> Unit,
    navigateToColorThemeSettings: () -> Unit,
    navigateToTextSettings: () -> Unit,
    navigateToTapZonesSettings: () -> Unit,
    navigateBack: () -> Unit,
) {
    val settingsItems = listOf<SettingsItem>(
        SettingsItem(R.string.interface_header, R.drawable.ic_iconmonstr_eye_3, navigateToInterfaceSettings),
        SettingsItem(R.string.color_theme, R.drawable.ic_iconmonstr_paintbrush_10, navigateToColorThemeSettings),
        SettingsItem(R.string.text_header, R.drawable.ic_iconmonstr_text_3, navigateToTextSettings),
        SettingsItem(R.string.tapzones_header, R.drawable.ic_iconmonstr_cursor_31, navigateToTapZonesSettings),
    )

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.settings,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = {  navigateBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },) }
    ) { padding ->
        LazyColumn(
            modifier
                .fillMaxSize()
                .padding(padding)
                .padding(paddingSmall)
        ) {
            items(settingsItems) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                        .clickable { item.onClick() }
                ) {
                    item.drawable?.let { Icon(painter = painterResource(id = it),
                        contentDescription = stringResource(id = item.title),
                        modifier = Modifier.padding(paddingSmall)) }
                    Text(text = stringResource(id = item.title),
                        modifier = Modifier.padding(paddingSmall).weight(1f))
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
            SettingsContent(
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                navigateToTextSettings = {},
                navigateToInterfaceSettings = {},
                navigateToColorThemeSettings = {},
                navigateToTapZonesSettings = {},
            )
        }
    }

}