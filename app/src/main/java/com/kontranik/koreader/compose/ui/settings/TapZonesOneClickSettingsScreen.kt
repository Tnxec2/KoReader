package com.kontranik.koreader.compose.ui.settings

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
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
        topLeft = settingsViewModel.tapOneAction.value[ScreenZone.TopLeft]!!.name,
        topLeftTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.TopLeft]!!.toTitle()),
        onChangeTopLeft = { settingsViewModel.changeOneClick(ScreenZone.TopLeft, it)},
        topCenter = settingsViewModel.tapOneAction.value[ScreenZone.TopCenter]!!.name,
        topCenterTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.TopCenter]!!.toTitle()),
        onChangeTopCenter = { settingsViewModel.changeOneClick(ScreenZone.TopCenter, it)},
        topRight = settingsViewModel.tapOneAction.value[ScreenZone.TopRight]!!.name,
        topRightTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.TopRight]!!.toTitle()),
        onChangeTopRight = { settingsViewModel.changeOneClick(ScreenZone.TopRight, it)},
        middleLeft = settingsViewModel.tapOneAction.value[ScreenZone.MiddleLeft]!!.name,
        middleLeftTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.MiddleLeft]!!.toTitle()),
        onChangeMiddleLeft = { settingsViewModel.changeOneClick(ScreenZone.MiddleLeft, it)},
        middleCenter = settingsViewModel.tapOneAction.value[ScreenZone.MiddleCenter]!!.name,
        middleCenterEnabled = false,
        middleCenterTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.MiddleCenter]!!.toTitle()),
        onChangeMiddleCenter = { },
        middleRight = settingsViewModel.tapOneAction.value[ScreenZone.MiddleRight]!!.name,
        middleRightTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.MiddleRight]!!.toTitle()),
        onChangeMiddleRight = { settingsViewModel.changeOneClick(ScreenZone.MiddleRight, it)},
        bottomLeft = settingsViewModel.tapOneAction.value[ScreenZone.BottomLeft]!!.name,
        bottomLeftTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.BottomLeft]!!.toTitle()),
        onChangeBottomLeft = { settingsViewModel.changeOneClick(ScreenZone.BottomLeft, it)},
        bottomCenter = settingsViewModel.tapOneAction.value[ScreenZone.BottomCenter]!!.name,
        bottomCenterTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.BottomCenter]!!.toTitle()),
        onChangeBottomCenter = { settingsViewModel.changeOneClick(ScreenZone.BottomCenter, it)},
        bottomRight = settingsViewModel.tapOneAction.value[ScreenZone.BottomRight]!!.name,
        bottomRightTitle = stringResource(id = settingsViewModel.tapOneAction.value[ScreenZone.BottomRight]!!.toTitle()),
        onChangeBottomRight = { settingsViewModel.changeOneClick(ScreenZone.BottomRight, it)},
    )
}