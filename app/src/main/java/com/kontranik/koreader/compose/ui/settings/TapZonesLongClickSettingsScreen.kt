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
fun TapZonesLongClickSettingsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    TapZonesClickSettingsContent(
        drawerState = drawerState,
        modifier = modifier,
        title = stringResource(id = R.string.tapzones_long_click_header),
        navigateBack = { coroutineScope.launch { navigateBack() }},
        topLeft = settingsViewModel.tapLongAction.value[ScreenZone.TopLeft]!!.name,
        topLeftTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.TopLeft]!!.toTitle()),
        onChangeTopLeft = { settingsViewModel.changeLongClick(ScreenZone.TopLeft, it)},
        topCenter = settingsViewModel.tapLongAction.value[ScreenZone.TopCenter]!!.name,
        topCenterTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.TopCenter]!!.toTitle()),
        onChangeTopCenter = { settingsViewModel.changeLongClick(ScreenZone.TopCenter, it)},
        topRight = settingsViewModel.tapLongAction.value[ScreenZone.TopRight]!!.name,
        topRightTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.TopRight]!!.toTitle()),
        onChangeTopRight = { settingsViewModel.changeLongClick(ScreenZone.TopRight, it)},
        middleLeft = settingsViewModel.tapLongAction.value[ScreenZone.MiddleLeft]!!.name,
        middleLeftTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.MiddleLeft]!!.toTitle()),
        onChangeMiddleLeft = { settingsViewModel.changeLongClick(ScreenZone.MiddleLeft, it)},
        middleCenter = settingsViewModel.tapLongAction.value[ScreenZone.MiddleCenter]!!.name,
        middleCenterEnabled = true,
        middleCenterTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.MiddleCenter]!!.toTitle()),
        onChangeMiddleCenter = { settingsViewModel.changeLongClick(ScreenZone.MiddleCenter, it)},
        middleRight = settingsViewModel.tapLongAction.value[ScreenZone.MiddleRight]!!.name,
        middleRightTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.MiddleRight]!!.toTitle()),
        onChangeMiddleRight = { settingsViewModel.changeLongClick(ScreenZone.MiddleRight, it)},
        bottomLeft = settingsViewModel.tapLongAction.value[ScreenZone.BottomLeft]!!.name,
        bottomLeftTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.BottomLeft]!!.toTitle()),
        onChangeBottomLeft = { settingsViewModel.changeLongClick(ScreenZone.BottomLeft, it)},
        bottomCenter = settingsViewModel.tapLongAction.value[ScreenZone.BottomCenter]!!.name,
        bottomCenterTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.BottomCenter]!!.toTitle()),
        onChangeBottomCenter = { settingsViewModel.changeLongClick(ScreenZone.BottomCenter, it)},
        bottomRight = settingsViewModel.tapLongAction.value[ScreenZone.BottomRight]!!.name,
        bottomRightTitle = stringResource(id = settingsViewModel.tapLongAction.value[ScreenZone.BottomRight]!!.toTitle()),
        onChangeBottomRight = { settingsViewModel.changeLongClick(ScreenZone.BottomRight, it)},
    )
}

