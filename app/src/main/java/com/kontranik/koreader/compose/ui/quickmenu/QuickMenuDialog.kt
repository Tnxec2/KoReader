package com.kontranik.koreader.compose.ui.quickmenu

import android.content.Context
import android.graphics.Typeface
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.settings.PREFS_FILE
import com.kontranik.koreader.compose.ui.settings.PREF_BOOK_PATH
import com.kontranik.koreader.compose.ui.shared.getLetterSpacing
import com.kontranik.koreader.compose.ui.shared.getLineSpacings
import com.kontranik.koreader.compose.ui.shared.getThemes


@Composable
fun QuickMenuDialog(
    onClose: () -> Unit,
    onAddBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    selectedColorTheme: Int,
    onChangeColorThemeQuickMenuDialog: (colorTheme: String, colorThemeIndex: Int) -> Unit,
    textSize: Float,
    onChangeTextSizeQuickMenuDialog: (textSize: Float) -> Unit,
    selectedFont: Typeface,
    textSizeInfoArea: Float,
    onChangeTextSizeInfoAreaQuickMenuDialog: (Float) -> Unit,
    selectedFontInfoArea: Typeface,
    lineSpacingMultiplier: Float,
    onChangeLineSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,
    letterSpacing: Float,
    onChangeLetterSpacingQuickMenuDialog: (letterSpacing: Float) -> Unit,
    onFinishQuickMenuDialog: (
        textSize: Float,
        textSizeInfoArea: Float,
        lineSpacingMultiplier: Float,
        letterSpacing: Float,
        colorThemeIndex: Int) -> Unit,
    onOpenBookInfo: () -> Unit,
) {
    val context = LocalContext.current

    var bookPath by remember {mutableStateOf<String?>(null)}

    val themes by remember {mutableStateOf(getThemes(context))}
    var colorThemeIndex by remember {
        mutableIntStateOf(selectedColorTheme)
    }

    var textSizeState by remember { mutableFloatStateOf(
        textSize
    ) }

    var textSizeInfoAreaState by remember { mutableFloatStateOf(
        textSizeInfoArea
    ) }

    val itemsLineSpacing by remember {mutableStateOf(getLineSpacings().map{it.toString()})}
    var lineSpacingMultiplierState by remember {
        mutableFloatStateOf(lineSpacingMultiplier)
    }

    val itemsLetterSpacing by remember {mutableStateOf(getLetterSpacing().map{it.toString()})}
    var letterSpacingState by remember {
        mutableFloatStateOf(letterSpacing)
    }

    LaunchedEffect(Unit) {
        val settings = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        bookPath = settings?.getString(PREF_BOOK_PATH, null)
    }

    fun saveQuickSettings() {
        onFinishQuickMenuDialog(textSizeState, textSizeInfoAreaState, lineSpacingMultiplierState, letterSpacingState, colorThemeIndex)
        onClose()
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onClose() }
    ) {
        Card(Modifier.padding(paddingSmall)) {

        QuickMenuDialogContent(
            themes = themes,
            colorThemePosition = colorThemeIndex,
            onChangeTheme = { pos, item ->
                colorThemeIndex = pos
                onChangeColorThemeQuickMenuDialog(item, pos)
            },
            textSize = textSizeState,
            onChangeTextSize = {
                textSizeState = it
                onChangeTextSizeQuickMenuDialog(it)
           },
            selectedFont = selectedFont,
            textSizeInfoArea = textSizeInfoAreaState,
            onChangeTextSizeInfoArea = {
                textSizeInfoAreaState = it
                onChangeTextSizeInfoAreaQuickMenuDialog(it)
            },
            selectedFontInfoArea = selectedFontInfoArea,
            itemsLineSpacing = itemsLineSpacing,
            itemsLetterSpacing = itemsLetterSpacing,
            lineSpacingMultiplier = lineSpacingMultiplierState,
            onChangeLineSpacing = {
                lineSpacingMultiplierState=it
                onChangeLineSpacingQuickMenuDialog(it)
            },
            letterSpacing = letterSpacingState,
            onChangeLetterSpacing = {
                letterSpacingState=it
                onChangeLetterSpacingQuickMenuDialog(it)
            },
            onAddBookmark = {onAddBookmark()},
            onOpenBookmarks = {onOpenBookmarks()},
            onClose = {onClose()},
            onOpenBookInfo = { onOpenBookInfo() },
            saveQuickSettings = {saveQuickSettings()},
            modifier = Modifier.padding(paddingMedium)
        )
        }
    }
}
