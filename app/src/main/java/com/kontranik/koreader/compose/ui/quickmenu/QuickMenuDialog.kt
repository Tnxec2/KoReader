package com.kontranik.koreader.compose.ui.quickmenu

import android.content.Context
import android.graphics.Typeface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.kontranik.koreader.compose.ui.settings.PREFS_FILE
import com.kontranik.koreader.compose.ui.settings.PREF_BOOK_PATH
import com.kontranik.koreader.compose.ui.shared.getLetterSpacing
import com.kontranik.koreader.compose.ui.shared.getLineSpacings
import com.kontranik.koreader.compose.ui.shared.getThemes
import com.kontranik.koreader.model.PageViewSettings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMenuDialog(
    onClose: () -> Unit,
    onAddBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onChangeColorThemeQuickMenuDialog: (colorTheme: String, colorThemeIndex: Int) -> Unit,
    onChangeTextSizeQuickMenuDialog: (textSize: Float) -> Unit,
    onChangeLineSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,
    onChangeLetterSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,
    onFinishQuickMenuDialog: (textSize: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorThemeIndex: Int) -> Unit,
    onOpenBookInfo: () -> Unit,
    selectedColorTheme: Int,
    pageViewSettings: PageViewSettings,
    selectedFont: Typeface
) {
    val context = LocalContext.current

    var bookPath by remember {mutableStateOf<String?>(null)}

    val themes by remember {mutableStateOf(getThemes(context))}
    var colorThemeIndex by remember {
        mutableIntStateOf(selectedColorTheme)
    }

    var textSize by remember { mutableFloatStateOf(
        pageViewSettings.textSize
    ) }

    val itemsLineSpacing by remember {mutableStateOf(getLineSpacings().map{it.toString()})}
    var lineSpacingMultiplier by remember {
        mutableFloatStateOf(pageViewSettings.lineSpacingMultiplier)
    }

    val itemsLetterSpacing by remember {mutableStateOf(getLetterSpacing().map{it.toString()})}
    var letterSpacing by remember {
        mutableFloatStateOf(pageViewSettings.letterSpacing)
    }

    LaunchedEffect(Unit) {
        val settings = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        bookPath = settings?.getString(PREF_BOOK_PATH, null)
    }

    fun saveQuickSettings() {
        onFinishQuickMenuDialog(textSize, lineSpacingMultiplier, letterSpacing, colorThemeIndex)
        onClose()
    }

    ModalBottomSheet(
        onDismissRequest = { onClose() }
    ) {
        QuickMenuDialogContent(
            themes = themes,
            colorThemePosition = colorThemeIndex,
            onChangeTheme = { pos, item ->
                colorThemeIndex = pos
                onChangeColorThemeQuickMenuDialog(item, pos)
            },
            textSize = textSize,
            onChangeTextSize = {
                textSize = it
                onChangeTextSizeQuickMenuDialog(it)
           },
            selectedFont = selectedFont,
            itemsLineSpacing = itemsLineSpacing,
            itemsLetterSpacing = itemsLetterSpacing,
            lineSpacingMultiplier = lineSpacingMultiplier,
            onChangeLineSpacing = {
                lineSpacingMultiplier=it
                onChangeLineSpacingQuickMenuDialog(it)
            },
            letterSpacing = letterSpacing,
            onChangeLetterSpacing = {
                letterSpacing=it
                onChangeLetterSpacingQuickMenuDialog(it)
            },
            onAddBookmark = {onAddBookmark()},
            onOpenBookmarks = {onOpenBookmarks()},
            onClose = {onClose()},
            onOpenBookInfo = { onOpenBookInfo() },
            saveQuickSettings = {saveQuickSettings()},
        )
    }
}
