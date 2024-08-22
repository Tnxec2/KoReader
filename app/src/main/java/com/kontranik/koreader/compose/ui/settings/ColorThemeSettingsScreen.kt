package com.kontranik.koreader.compose.ui.settings

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R
import kotlinx.coroutines.launch


@Composable
fun ColorThemeSettingsScreen(
    modifier: Modifier = Modifier,
    themeIndex: Int,
    title: String = stringResource(id = R.string.color_theme_indexed_header, themeIndex+1),
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    val colors by settingsViewModel.colors[themeIndex]!!

    ColorThemeSettingsContent(
        title = title,
        drawerState = drawerState,
        navigateBack = { coroutineScope.launch { navigateBack() }},
        modifier = modifier,
        colorBackground = colors.colorBackground,
        onChangeColorBackground = { coroutineScope.launch { settingsViewModel.changeColorBackground(themeIndex, it) }},
        onSetDefaultColorBackground = { coroutineScope.launch { settingsViewModel.setDefaultColorBackground(themeIndex) }},
        colorText = colors.colorsText,
        onChangeColorText = { coroutineScope.launch { settingsViewModel.changeColorText(themeIndex, it) }},
        onSetDefaultColorText = { coroutineScope.launch { settingsViewModel.setDefaultColorText(themeIndex) }},
        colorLink = colors.colorsLink,
        onChangeColorLink = { coroutineScope.launch { settingsViewModel.changeColorLink(themeIndex, it) }},
        onSetDefaultColorLink = { coroutineScope.launch { settingsViewModel.setDefaultColorLink(themeIndex) }},
        colorInfoArea = colors.colorsInfo,
        onChangeColorInfoArea = { coroutineScope.launch { settingsViewModel.changeColorInfo(themeIndex, it) }},
        onSetDefaultColorInfoArea = { coroutineScope.launch { settingsViewModel.setDefaultColorInfo(themeIndex) }},
        tileBackgroundImage = colors.backgroundImageTiledRepeat,
        onChangeTileBackgroundImage = { coroutineScope.launch { settingsViewModel.changeTileBackgroundImage(themeIndex, it) }},
        showBackgroundImage = colors.showBackgroundImage,
        onChangeShowBackgroundImage ={ coroutineScope.launch { settingsViewModel.changeShowBackgroundImage(themeIndex, it) }},
        marginTop = colors.marginTop,
        onChangeMarginTop = { coroutineScope.launch { settingsViewModel.changeMarginTop(themeIndex, it) }},
        marginBottom = colors.marginBottom,
        onChangeMarginBottom = { coroutineScope.launch { settingsViewModel.changeMarginBottom(themeIndex, it) }},
        marginLeft = colors.marginLeft,
        onChangeMarginLeft = { coroutineScope.launch { settingsViewModel.changeMarginLeft(themeIndex, it) }},
        marginRight = colors.marginRight,
        onChangeMarginRight = { coroutineScope.launch { settingsViewModel.changeMarginRight(themeIndex, it) }},
        backgroundImage = colors.backgroundImageUri,
        onChangeBackgroundImage = { coroutineScope.launch { settingsViewModel.changeBackgroundImage(themeIndex, it.toString()) }},
    )
}
