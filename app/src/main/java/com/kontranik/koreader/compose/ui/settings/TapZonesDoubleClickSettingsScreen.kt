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
fun TapZonesDoubleClickSettingsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    TapZonesClickSettingsContent(
        drawerState = drawerState,
        modifier = modifier,
        title = stringResource(id = R.string.tapzones_double_click_header),
        navigateBack = { coroutineScope.launch { navigateBack() }},
        topLeft = settingsViewModel.tapDoubleAction.value[ScreenZone.TopLeft]!!.name,
        topLeftTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.TopLeft]!!.toTitle()),
        onChangeTopLeft = { settingsViewModel.changeDoubleClick(ScreenZone.TopLeft, it)},
        topCenter = settingsViewModel.tapDoubleAction.value[ScreenZone.TopCenter]!!.name,
        topCenterTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.TopCenter]!!.toTitle()),
        onChangeTopCenter = { settingsViewModel.changeDoubleClick(ScreenZone.TopCenter, it)},
        topRight = settingsViewModel.tapDoubleAction.value[ScreenZone.TopRight]!!.name,
        topRightTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.TopRight]!!.toTitle()),
        onChangeTopRight = { settingsViewModel.changeDoubleClick(ScreenZone.TopRight, it)},
        middleLeft = settingsViewModel.tapDoubleAction.value[ScreenZone.MiddleLeft]!!.name,
        middleLeftTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.MiddleLeft]!!.toTitle()),
        onChangeMiddleLeft = { settingsViewModel.changeDoubleClick(ScreenZone.MiddleLeft, it)},
        middleCenter = settingsViewModel.tapDoubleAction.value[ScreenZone.MiddleCenter]!!.name,
        middleCenterEnabled = true,
        middleCenterTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.MiddleCenter]!!.toTitle()),
        onChangeMiddleCenter = { settingsViewModel.changeDoubleClick(ScreenZone.MiddleCenter, it)},
        middleRight = settingsViewModel.tapDoubleAction.value[ScreenZone.MiddleRight]!!.name,
        middleRightTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.MiddleRight]!!.toTitle()),
        onChangeMiddleRight = { settingsViewModel.changeDoubleClick(ScreenZone.MiddleRight, it)},
        bottomLeft = settingsViewModel.tapDoubleAction.value[ScreenZone.BottomLeft]!!.name,
        bottomLeftTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.BottomLeft]!!.toTitle()),
        onChangeBottomLeft = { settingsViewModel.changeDoubleClick(ScreenZone.BottomLeft, it)},
        bottomCenter = settingsViewModel.tapDoubleAction.value[ScreenZone.BottomCenter]!!.name,
        bottomCenterTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.BottomCenter]!!.toTitle()),
        onChangeBottomCenter = { settingsViewModel.changeDoubleClick(ScreenZone.BottomCenter, it)},
        bottomRight = settingsViewModel.tapDoubleAction.value[ScreenZone.BottomRight]!!.name,
        bottomRightTitle = stringResource(id = settingsViewModel.tapDoubleAction.value[ScreenZone.BottomRight]!!.toTitle()),
        onChangeBottomRight = { settingsViewModel.changeDoubleClick(ScreenZone.BottomRight, it)},
    )
}

