package com.kontranik.koreader.compose.ui.settings

import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.shared.FontSizeWidget
import com.kontranik.koreader.compose.ui.settings.elements.SettingsCard
import com.kontranik.koreader.compose.ui.settings.elements.SettingsCheckbox
import com.kontranik.koreader.compose.ui.settings.elements.SettingsList
import com.kontranik.koreader.compose.ui.settings.elements.SettingsTitle
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLight
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import kotlinx.coroutines.launch


@Composable
fun TextSettingsScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    val fonts by settingsViewModel.fonts

    TextSettingsContent(
        modifier = modifier,
        drawerState = drawerState,
        navigateBack = { coroutineScope.launch { navigateBack() }},
        textSize = settingsViewModel.pageViewSettings.value.textSize,
        onChangeTextSize = { coroutineScope.launch { settingsViewModel.changeFontSize(it) } },
        lineSpacingMultiplier = settingsViewModel.pageViewSettings.value.lineSpacingMultiplier,
        onChangeLineSpacingMultiplier= { coroutineScope.launch { settingsViewModel.changeLineSpacingMultiplier(it) }},
        letterSpacing = settingsViewModel.pageViewSettings.value.letterSpacing,
        onChangeLetterSpacing = { coroutineScope.launch { settingsViewModel.changeLetterSpacing(it) }},
        showSystemFonts = settingsViewModel.showSystemFonts.value,
        onChangeShowSystemFonts = {coroutineScope.launch { settingsViewModel.changeShowSystemFonts(it) }},
        showNotoFonts = settingsViewModel.showNotoFonts.value,
        onChangeShowNotoFonts = {coroutineScope.launch { settingsViewModel.changeShowNotoFonts(it) }},
        fonts = fonts,
        onChangeFont = { type, typefaceRecord ->
            coroutineScope.launch {
                settingsViewModel.changeFont(type, typefaceRecord)
            }
        },
    )
}


@Composable
fun TextSettingsContent(
    textSize: Float,
    onChangeTextSize: (Float) -> Unit,
    lineSpacingMultiplier: Float,
    onChangeLineSpacingMultiplier: (Float) -> Unit,
    letterSpacing: Float,
    onChangeLetterSpacing: (Float) -> Unit,
    showSystemFonts: Boolean,
    onChangeShowSystemFonts: (Boolean) -> Unit,
    showNotoFonts: Boolean,
    onChangeShowNotoFonts: (Boolean) -> Unit,
    fonts: Map<TextType, TypefaceRecord>,
    onChangeFont: (TextType, TypefaceRecord) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
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
                    SettingsTitle(text = stringResource(id = R.string.text),
                        modifier = Modifier.padding(bottom = paddingSmall))
                }

                item {
                    SettingsCard(title = stringResource(id = R.string.text)) {
                        Column {
                            FontSizeWidget(
                                textSize = textSize,
                                onChangeTextSize = onChangeTextSize,
                                selectedFont = fonts[TextType.Normal]!!.getTypeface(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            SettingsList(
                                title = stringResource(id = R.string.linespacing_title),
                                entries = stringArrayResource(id = R.array.line_spacing_entries).toList(),
                                entryValues = stringArrayResource(id = R.array.line_spacing_values).toList(),
                                defaultValue = lineSpacingMultiplier.toString(),
                                icon = R.drawable.ic_baseline_format_line_spacing_24,
                                onChange = { onChangeLineSpacingMultiplier(it.toFloat()) },
                                showDefaultValue = true,
                            )
                            SettingsList(
                                title = stringResource(id = R.string.letterspacing_title),
                                entries = stringArrayResource(id = R.array.letter_spacing_entries).toList(),
                                entryValues = stringArrayResource(id = R.array.letter_spacing_values).toList(),
                                defaultValue = letterSpacing.toString(),
                                icon = R.drawable.ic_letter_spacing,
                                onChange = { onChangeLetterSpacing(it.toFloat()) },
                                showDefaultValue = true,
                            )
                        }
                    }
                }
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.font_header),
                        modifier = Modifier.padding(top = paddingMedium)
                    ) {
                        Column {
                            SettingsCheckbox(value = showSystemFonts,
                                label = stringResource(id = R.string.showSystemFonts_title),
                                onChange = onChangeShowSystemFonts)
                            SettingsCheckbox(value = showNotoFonts,
                                label = stringResource(id = R.string.show_noto_fonts_title),
                                onChange = onChangeShowNotoFonts)

                            Spacer(modifier = Modifier.height(paddingMedium))

                            SettingsFontPicker(
                                title = stringResource(id = R.string.text_normal),
                                style = Typeface.NORMAL,
                                typefaceRecord = fonts[TextType.Normal]!!,
                                showSystemFonts = showSystemFonts,
                                shoNotoFonts = showNotoFonts,
                                onChange = {
                                    onChangeFont(TextType.Normal, it)
                                }
                            )
                            SettingsFontPicker(
                                title = stringResource(id = R.string.text_bold),
                                style = Typeface.BOLD,
                                typefaceRecord = fonts[TextType.Bold]!!,
                                showSystemFonts = showSystemFonts,
                                shoNotoFonts = showNotoFonts,
                                onChange = {
                                    onChangeFont(TextType.Bold, it)
                                }
                            )
                            SettingsFontPicker(
                                title = stringResource(id = R.string.text_italic),
                                style = Typeface.ITALIC,
                                typefaceRecord = fonts[TextType.Italic]!!,
                                showSystemFonts = showSystemFonts,
                                shoNotoFonts = showNotoFonts,
                                onChange = {
                                    onChangeFont(TextType.Italic, it)
                                }
                            )
                            SettingsFontPicker(
                                title = stringResource(id = R.string.text_bolditalic),
                                style = Typeface.BOLD_ITALIC,
                                typefaceRecord = fonts[TextType.BoldItalic]!!,
                                showSystemFonts = showSystemFonts,
                                shoNotoFonts = showNotoFonts,
                                onChange = {
                                    onChangeFont(TextType.BoldItalic, it)
                                }
                            )
                            SettingsFontPicker(
                                title = stringResource(id = R.string.text_monospace),
                                style = Typeface.NORMAL,
                                typefaceRecord = fonts[TextType.Monospace]!!,
                                showSystemFonts = showSystemFonts,
                                shoNotoFonts = showNotoFonts,
                                onChange = { onChangeFont(TextType.Monospace, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@PreviewPortraitLight
@Composable
private fun ColorThemeSettingsPreview() {
    AppTheme {
        Surface {
            TextSettingsContent(
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                textSize = 12f,
                onChangeTextSize = {},
                lineSpacingMultiplier = 1.2f,
                onChangeLineSpacingMultiplier= {},
                letterSpacing = 1.2f,
                onChangeLetterSpacing = {},
                showSystemFonts = false,
                onChangeShowSystemFonts = {},
                showNotoFonts = false,
                onChangeShowNotoFonts = {},
                fonts = defaultsFonts,
                onChangeFont = {_, _ ->},
            )
        }
    }

}