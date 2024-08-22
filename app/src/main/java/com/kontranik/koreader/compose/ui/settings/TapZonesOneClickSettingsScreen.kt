package com.kontranik.koreader.compose.ui.settings

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R
import com.kontranik.koreader.model.ScreenZone
import kotlinx.coroutines.launch


@Composable
fun TapZonesOneClickSettingsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    TapZonesClickSettingsContent(
        drawerState = drawerState,
        modifier = modifier,
        title = stringResource(id = R.string.tapzones_one_click_header),
        navigateBack = { coroutineScope.launch { navigateBack() }},
        topLeft = settingsViewModel.tapOneAction.value[ScreenZone.TopLeft]!!,
        onChangeTopLeft = { settingsViewModel.changeOneClick(ScreenZone.TopLeft, it)},
        topCenter = settingsViewModel.tapOneAction.value[ScreenZone.TopCenter]!!,
        onChangeTopCenter = { settingsViewModel.changeOneClick(ScreenZone.TopCenter, it)},
        topRight = settingsViewModel.tapOneAction.value[ScreenZone.TopRight]!!,
        onChangeTopRight = { settingsViewModel.changeOneClick(ScreenZone.TopRight, it)},
        middleLeft = settingsViewModel.tapOneAction.value[ScreenZone.MiddleLeft]!!,
        onChangeMiddleLeft = { settingsViewModel.changeOneClick(ScreenZone.MiddleLeft, it)},
        middleCenter = settingsViewModel.tapOneAction.value[ScreenZone.MiddleCenter]!!,
        onChangeMiddleCenter = { settingsViewModel.changeOneClick(ScreenZone.MiddleCenter, it)},
        middleRight = settingsViewModel.tapOneAction.value[ScreenZone.MiddleRight]!!,
        onChangeMiddleRight = { settingsViewModel.changeOneClick(ScreenZone.MiddleRight, it)},
        bottomLeft = settingsViewModel.tapOneAction.value[ScreenZone.BottomLeft]!!,
        onChangeBottomLeft = { settingsViewModel.changeOneClick(ScreenZone.BottomLeft, it)},
        bottomCenter = settingsViewModel.tapOneAction.value[ScreenZone.BottomCenter]!!,
        onChangeBottomCenter = { settingsViewModel.changeOneClick(ScreenZone.BottomCenter, it)},
        bottomRight = settingsViewModel.tapOneAction.value[ScreenZone.BottomRight]!!,
        onChangeBottomRight = { settingsViewModel.changeOneClick(ScreenZone.BottomRight, it)},
    )
}