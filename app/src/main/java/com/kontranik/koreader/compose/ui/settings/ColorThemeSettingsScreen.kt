package com.kontranik.koreader.compose.ui.settings

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.navigation.NavigationDestination
import kotlinx.coroutines.launch

object ColorThemeSettingsDestination : NavigationDestination {
    override val route = "ColorThemeSettings"
    override val titleRes = R.string.color_theme_header
    const val THEME_INDEX = "themeIndex"
    val routeWithArgs = "$route/{$THEME_INDEX}"
}

@Composable
fun ColorThemeSettingsScreen(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
    colorThemeSettingsViewModel: ColorThemeSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val themeIndex by colorThemeSettingsViewModel.themeIndexState

    val colors by settingsViewModel.colors[themeIndex]!!

    ColorThemeSettingsContent(
        title = stringResource(id = R.string.color_theme_indexed_header, themeIndex+1),
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
        colorBookmark = colors.colorBookmark,
        onChangeColorBookmark = { coroutineScope.launch { settingsViewModel.changeColorBookmark(themeIndex, it) }},
        onSetDefaultColorBookmark = { coroutineScope.launch { settingsViewModel.setDefaultColorBookmark(themeIndex) }},
        tileBackgroundImage = colors.backgroundImageTiledRepeat,
        onChangeTileBackgroundImage = { coroutineScope.launch { settingsViewModel.changeTileBackgroundImage(themeIndex, it) }},
        stetchBackgroundImage = colors.stetchBackgroundImage,
        onChangeStretchBackgroundImage = { coroutineScope.launch { settingsViewModel.changeStretchBackgroundImage(themeIndex, it) }},
        showBackgroundImage = colors.showBackgroundImage,
        onChangeShowBackgroundImage ={ coroutineScope.launch { settingsViewModel.changeShowBackgroundImage(themeIndex, it) }},
        marginTop = colors.marginTop.toString(),
        onChangeMarginTop = { coroutineScope.launch { settingsViewModel.changeMarginTop(themeIndex, it) }},
        marginBottom = colors.marginBottom.toString(),
        onChangeMarginBottom = { coroutineScope.launch { settingsViewModel.changeMarginBottom(themeIndex, it) }},
        marginLeft = colors.marginLeft.toString(),
        onChangeMarginLeft = { coroutineScope.launch { settingsViewModel.changeMarginLeft(themeIndex, it) }},
        marginRight = colors.marginRight.toString(),
        onChangeMarginRight = { coroutineScope.launch { settingsViewModel.changeMarginRight(themeIndex, it) }},
        backgroundImage = colors.backgroundImageUri,
        onChangeBackgroundImage = { coroutineScope.launch { settingsViewModel.changeBackgroundImage(themeIndex, it) }},
    )
}
