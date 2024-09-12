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
fun TapZonesInfoAreaClickSettingsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    OneLineTapZonesClickSettingsContent(
        drawerState = drawerState,
        modifier = modifier,
        title = stringResource(id = R.string.tapzones_info_area_click_header),
        navigateBack = { coroutineScope.launch { navigateBack() }},
        left = settingsViewModel.tapInfoAreaAction.value[ScreenZone.TopLeft]!!.name,
        leftTitle = stringResource(id = settingsViewModel.tapInfoAreaAction.value[ScreenZone.TopLeft]!!.toTitle()),
        onChangeLeft = { settingsViewModel.changeInfoAreaClick(ScreenZone.TopLeft, it)},
        center = settingsViewModel.tapInfoAreaAction.value[ScreenZone.TopCenter]!!.name,
        centerTitle = stringResource(id = settingsViewModel.tapInfoAreaAction.value[ScreenZone.TopCenter]!!.toTitle()),
        onChangeCenter = { settingsViewModel.changeInfoAreaClick(ScreenZone.TopCenter, it)},
        right = settingsViewModel.tapInfoAreaAction.value[ScreenZone.TopRight]!!.name,
        rightTitle = stringResource(id = settingsViewModel.tapInfoAreaAction.value[ScreenZone.TopRight]!!.toTitle()),
        onChangeRight = { settingsViewModel.changeInfoAreaClick(ScreenZone.TopRight, it)},
    )
}