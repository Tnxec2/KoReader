package com.kontranik.koreader.compose.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.settings.elements.SettingsButton
import com.kontranik.koreader.compose.ui.settings.elements.SettingsList
import com.kontranik.koreader.compose.ui.settings.elements.SettingsTitle
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLandscapeLightDark
import kotlinx.coroutines.launch


@Composable
fun TapZonesSettingsScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToSettingsTapZonesOneClick: () -> Unit,
    navigateToSettingsTapZonesDoubleClick: () -> Unit,
    navigateToSettingsTapZonesLongClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    TapZonesSettingsContent(
        drawerState = drawerState,
        navigateBack = { coroutineScope.launch { navigateBack() }},
        modifier = modifier,
        navigateToSettingsTapZonesOneClick = navigateToSettingsTapZonesOneClick,
        navigateToSettingsTapZonesDoubleClick = navigateToSettingsTapZonesDoubleClick,
        navigateToSettingsTapZonesLongClick = navigateToSettingsTapZonesLongClick,
    )
}

@Composable
fun TapZonesSettingsContent(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToSettingsTapZonesOneClick: () -> Unit,
    navigateToSettingsTapZonesDoubleClick: () -> Unit,
    navigateToSettingsTapZonesLongClick: () -> Unit,
) {
    val settingsItems = listOf(
        SettingsItem(R.string.tapzones_one_click_header, null, navigateToSettingsTapZonesOneClick),
        SettingsItem(R.string.tapzones_double_click_header, null, navigateToSettingsTapZonesDoubleClick),
        SettingsItem(R.string.tapzones_long_click_header, null, navigateToSettingsTapZonesLongClick),
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
        Column(
            modifier
                .padding(padding)
                .padding(paddingSmall)
                .fillMaxSize()
        ) {

            SettingsTitle(text = stringResource(id = R.string.tapzones_header))

            LazyColumn(
                modifier
                    .fillMaxSize()
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
                        item.drawable?.let {
                            Icon(painter = painterResource(id = it),
                                contentDescription = stringResource(id = item.title),
                                modifier = Modifier.padding(paddingSmall)
                            )
                        }
                        Text(text = stringResource(id = item.title),
                            modifier = Modifier.padding(paddingSmall).weight(1f))
                    }
                }
            }
        }
    }
}


@PreviewPortraitLandscapeLightDark
@Composable
private fun TapZonesSettingsContentPreview() {
    AppTheme {
        Surface {
            TapZonesSettingsContent(
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                navigateToSettingsTapZonesOneClick = {},
                navigateToSettingsTapZonesDoubleClick = {},
                navigateToSettingsTapZonesLongClick = {}
            )
        }
    }

}