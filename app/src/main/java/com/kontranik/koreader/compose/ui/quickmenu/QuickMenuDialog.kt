package com.kontranik.koreader.compose.ui.quickmenu

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.compose.ui.shared.getLetterSpacing
import com.kontranik.koreader.compose.ui.shared.getLineSpacings
import com.kontranik.koreader.compose.ui.shared.getThemes
import com.kontranik.koreader.utils.PrefsHelper
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import com.kontranik.koreader.compose.theme.defaultLetterSpacing
import com.kontranik.koreader.compose.theme.defaultLineSpacingMultiplier
import com.kontranik.koreader.compose.theme.defaultTextSize
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMenuDialog(
    onFinishQuickMenuDialog: (textSize: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorTheme: String) -> Unit,
    onClose: () -> Unit,
    onAddBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onChangeColorThemeQuickMenuDialog: (colorTheme: String, colorThemeIndex: Int) -> Unit,
    onChangeTextSizeQuickMenuDialog: (textSize: Float) -> Unit,
    onChangeLineSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,
    onChangeLetterSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,
    onOpenBookInfo: (String) -> Unit,
) {
    val context = LocalContext.current

    var bookPath by remember {mutableStateOf<String?>(null)}

    var themes by remember {mutableStateOf(getThemes(context))}
    var colorTheme by remember {mutableStateOf("")}
    var colorThemePosition by remember { mutableIntStateOf(0) }

    var textSize by remember { mutableFloatStateOf(defaultTextSize) }

    val itemsLineSpacing by remember {mutableStateOf(getLineSpacings().map{it.toString()})}
    var lineSpacingMultiplier by remember {mutableFloatStateOf(0f)}

    val itemsLetterSpacing by remember {mutableStateOf(getLetterSpacing().map{it.toString()})}
    var letterSpacing by remember {mutableFloatStateOf(0f)}


    var selectedFont by remember {mutableStateOf<TypefaceRecord>(TypefaceRecord.DEFAULT)}

    LaunchedEffect(Unit) {
        val settings = context.getSharedPreferences(ReaderActivity.PREFS_FILE, AppCompatActivity.MODE_PRIVATE)
        bookPath = settings?.getString(PrefsHelper.PREF_BOOK_PATH, null)

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        colorTheme = prefs.getString(
            PrefsHelper.PREF_KEY_COLOR_SELECTED_THEME,
            PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT)
            ?: PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT

        val lineSpacingMultiplierString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, defaultLineSpacingMultiplier.toString() )
        if ( lineSpacingMultiplierString != null) lineSpacingMultiplier = lineSpacingMultiplierString.toFloat()

        val letterSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, defaultLetterSpacing.toString() )
        if ( letterSpacingString != null) letterSpacing = letterSpacingString.toFloat()

        textSize = prefs.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)

        println("$textSize, $defaultTextSize")

        val fontpath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL, null)
        val fontname = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL, TypefaceRecord.DEFAULT.name) ?: TypefaceRecord.DEFAULT.name
        selectedFont = if ( fontpath != null ) {
            val f = File(fontpath)
            if (f.exists() && f.isFile && f.canRead())
                TypefaceRecord(fontname, f)
            else
                TypefaceRecord.DEFAULT
        } else {
            TypefaceRecord(fontname)
        }
    }

    fun saveQuickSettings() {
        val prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        prefEditor.putString(PrefsHelper.PREF_KEY_COLOR_SELECTED_THEME, colorTheme)
        prefEditor.putFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, textSize)
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, lineSpacingMultiplier.toString())
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, letterSpacing.toString())
        prefEditor.apply()

        // Return Data back to activity through the implemented listener
        onFinishQuickMenuDialog(textSize, lineSpacingMultiplier, letterSpacing, colorTheme)
    }

    ModalBottomSheet(onDismissRequest = { onClose() }) {
        QuickMenuDialogContent(
            themes = themes,
            colorThemePosition = colorThemePosition,
            onChangeTheme = { pos, item ->
                colorTheme = item
                colorThemePosition = pos
                onChangeColorThemeQuickMenuDialog(item, pos)
            },
            textSize = textSize,
            onChangeTextSize = {
                textSize = it
                onChangeTextSizeQuickMenuDialog(it)
           },
            selectedFont = selectedFont.getTypeface(),
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
            onOpenBookInfo = { bookPath?.let{onOpenBookInfo(it)}},
            saveQuickSettings = {saveQuickSettings()},
        )
    }
}
