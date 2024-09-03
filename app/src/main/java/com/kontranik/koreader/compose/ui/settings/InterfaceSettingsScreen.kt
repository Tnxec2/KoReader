package com.kontranik.koreader.compose.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.settings.elements.SettingsCard
import com.kontranik.koreader.compose.ui.settings.elements.SettingsList
import com.kontranik.koreader.compose.ui.settings.elements.SettingsTitle
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLandscapeLightDark
import kotlinx.coroutines.launch


@Composable
fun InterfaceSettingsScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    InterfaceContent(
        drawerState = drawerState,
        navigateBack = { coroutineScope.launch { navigateBack() }},
        modifier = modifier,
        interfaceTheme = settingsViewModel.interfaceTheme.value,
        onChangeInterfaceTheme = {coroutineScope.launch { settingsViewModel.changeinterfaceTheme(it) }},
        brightness = settingsViewModel.brightness.value,
        onChangeBrightness = {coroutineScope.launch { settingsViewModel.changebrightness(it) }},
        orientation = settingsViewModel.orientation.value,
        onChangeOrientation = {coroutineScope.launch { settingsViewModel.changeorientation(it) }},
    )
}

@Composable
fun InterfaceContent(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    interfaceTheme: String,
    onChangeInterfaceTheme: (String) -> Unit,
    brightness: String,
    onChangeBrightness: (String) -> Unit,
    orientation: String,
    onChangeOrientation: (String) -> Unit,
) {


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
            LazyColumn(
                Modifier
            ) {
                item {
                    SettingsTitle(
                        text = stringResource(id = R.string.interface_header),
                        modifier = Modifier.padding(bottom = paddingSmall)
                    )
                }

                item {
                    SettingsCard(

                    ) {
                        Column {

                            SettingsList(
                                title = stringResource(id = R.string.interface_theme_title),
                                entries = getStringArrayFromResourceArray(res = interface_entries),
                                entryValues = interface_values.toList(),
                                defaultValue = interfaceTheme,
                                icon = R.drawable.ic_baseline_preview_24,
                                onChange = { onChangeInterfaceTheme(it) },
                                showDefaultValue = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            SettingsList(
                                title = stringResource(id = R.string.brightness_title),
                                entries = getStringArrayFromResourceArray(res = brightness_entries),
                                entryValues = brightness_values.toList(),
                                defaultValue = brightness,
                                icon = R.drawable.ic_baseline_brightness_medium_24,
                                onChange = { onChangeBrightness(it) },
                                showDefaultValue = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            SettingsList(
                                title = stringResource(id = R.string.orientation_title),
                                entries = getStringArrayFromResourceArray(res = orientation_enties),
                                entryValues = orientation_values.toList(),
                                defaultValue = orientation,
                                icon = R.drawable.ic_baseline_screen_rotation_24,
                                onChange = { onChangeOrientation(it) },
                                showDefaultValue = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
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
            InterfaceContent(
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                interfaceTheme = interfaceThemeDefault,
                onChangeInterfaceTheme = {},
                brightness = brightnessDefault,
                onChangeBrightness = {},
                orientation = orientationDefault,
                onChangeOrientation = {}
            )
        }
    }

}