package com.kontranik.koreader.compose.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource

import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.defaultLetterSpacing
import com.kontranik.koreader.compose.theme.defaultLineSpacingMultiplier
import com.kontranik.koreader.compose.theme.defaultTextSize
import com.kontranik.koreader.compose.theme.defaultTextSizeInfoArea
import com.kontranik.koreader.model.PageViewSettings
import com.kontranik.koreader.model.ScreenZone

import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord.Companion.MONO
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord.Companion.SANSSERIF
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord.Companion.SERIF
import java.util.Locale

const val PREFS_FILE = "KOREADER"

const val PREF_BOOK_PATH = "BookPath"
const val OPENFILE_EXTERNAL_PATHS = "ExternalPaths"

const val PREFS_interface_theme = "interface_theme"
const val PREFS_interface_brightness = "brightness"
const val PREFS_orientation = "orientation"

const val PREF_KEY_USE_SYSTEM_FONTS = "showSystemFonts"
const val PREF_KEY_SHOW_NOTO_FONTS = "showNotoFonts"

const val PREF_KEY_BOOK_TEXT_SIZE = "TextSize"
const val PREF_KEY_BOOK_TEXT_SIZE_INFO_AREA = "TextSizeInfoArea"
const val PREF_KEY_BOOK_LINE_SPACING = "LineSpacing"
const val PREF_KEY_BOOK_LETTER_SPACING = "LetterSpacing"

const val PREF_KEY_COLOR_BACK = "colorBackTheme"
const val PREF_KEY_COLOR_TEXT = "colorTextTheme"
const val PREF_KEY_COLOR_INFOTEXT = "colorInfoTheme"
const val PREF_KEY_COLOR_BOOKMARKS = "colorBookmarks"
const val PREF_KEY_COLOR_LINKTEXT = "colorLinkTheme"
const val PREF_KEY_MARGIN_TOP = "marginTopTheme"
const val PREF_KEY_MARGIN_BOTTOM = "marginBottomTheme"
const val PREF_KEY_MARGIN_BOTTOM_INFOAREA = "marginBottomInfoAreaTheme"
const val PREF_KEY_MARGIN_LEFT = "marginLeftTheme1"
const val PREF_KEY_MARGIN_RIGHT = "marginRightTheme"
const val PREF_KEY_COLOR_SELECTED_THEME_INDEX = "selected_theme_index"
const val PREF_KEY_BACKGROUND_IMAGE_URI = "backgroundImageTheme"
const val PREF_KEY_SHOW_BACKGROUND_IMAGE = "backgroundImageEnableTheme"
const val PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT = "backgroundImageTileTheme"
const val PREF_KEY_BACKGROUND_IMAGE_STRETCH = "backgroundImageStretchTheme"
const val PREF_SCREEN_BRIGHTNESS = "ScreenBrightness"

const val PREF_KEY_BOOK_FONT_NAME_NORMAL = "FontNameNormal"
const val PREF_KEY_BOOK_FONT_PATH_NORMAL = "FontPathNormal"
const val PREF_KEY_BOOK_FONT_NAME_BOLD = "FontNameBold"
const val PREF_KEY_BOOK_FONT_PATH_BOLD = "FontPathBold"
const val PREF_KEY_BOOK_FONT_NAME_ITALIC = "FontNameItalic"
const val PREF_KEY_BOOK_FONT_PATH_ITALIC = "FontPathItalic"
const val PREF_KEY_BOOK_FONT_NAME_BOLDITALIC = "FontNameItalicBold"
const val PREF_KEY_BOOK_FONT_PATH_BOLDITALIC = "FontPathItalicBold"
const val PREF_KEY_BOOK_FONT_NAME_MONOSPACE = "FontNameMonospace"
const val PREF_KEY_BOOK_FONT_PATH_MONOSPACE = "FontPathMonospace"
const val PREF_KEY_BOOK_FONT_NAME_INFO_AREA = "FontNameInfoArea"
const val PREF_KEY_BOOK_FONT_PATH_INFO_AREA = "FontPathInfoArea"

const val PREF_KEY_TAP_ONE_TOP_LEFT = "tapZoneOneClickTopLeft"
const val PREF_KEY_TAP_ONE_TOP_CENTER = "tapZoneOneClickTopCenter"
const val PREF_KEY_TAP_ONE_TOP_RIGHT = "tapZoneOneClickTopRight"
const val PREF_KEY_TAP_ONE_MIDDLE_LEFT = "tapZoneOneClickMiddleLeft"
const val PREF_KEY_TAP_ONE_MIDDLE_CENTER = "tapZoneOneClickMiddleCenter"
const val PREF_KEY_TAP_ONE_MIDDLE_RIGHT = "tapZoneOneClickMiddleRight"
const val PREF_KEY_TAP_ONE_BOTTOM_LEFT = "tapZoneOneClickBottomLeft"
const val PREF_KEY_TAP_ONE_BOTTOM_CENTER = "tapZoneOneClickBottomCenter"
const val PREF_KEY_TAP_ONE_BOTTOM_RIGHT = "tapZoneOneClickBottomRight"

const val PREF_KEY_TAP_DOUBLE_TOP_LEFT = "tapZoneDoubleClickTopLeft"
const val PREF_KEY_TAP_DOUBLE_TOP_CENTER = "tapZoneDoubleClickTopCenter"
const val PREF_KEY_TAP_DOUBLE_TOP_RIGHT = "tapZoneDoubleClickTopRight"
const val PREF_KEY_TAP_DOUBLE_MIDDLE_LEFT = "tapZoneDoubleClickMiddleLeft"
const val PREF_KEY_TAP_DOUBLE_MIDDLE_CENTER = "tapZoneDoubleClickMiddleCenter"
const val PREF_KEY_TAP_DOUBLE_MIDDLE_RIGHT = "tapZoneDoubleClickMiddleRight"
const val PREF_KEY_TAP_DOUBLE_BOTTOM_LEFT = "tapZoneDoubleClickBottomLeft"
const val PREF_KEY_TAP_DOUBLE_BOTTOM_CENTER = "tapZoneDoubleClickBottomCenter"
const val PREF_KEY_TAP_DOUBLE_BOTTOM_RIGHT = "tapZoneOneClickBottomRight"

const val PREF_COLOR_SELECTED_THEME_DEFAULT = 0
const val PREF_DEFAULT_MARGIN = 10
const val PREF_DEFAULT_MARGIN_BOTTOM = 2
const val PREF_DEFAULT_MARGIN_BOTTOM_INFOAREA = 2
const val interfaceThemeDefault = "Auto"
const val brightnessDefault = "System"
const val orientationDefault = "Sensor"



data class ThemeColors(
    val colorBackground: Color,
    val colorsText: Color,
    val colorsLink: Color,
    val colorsInfo: Color,
    val colorBookmark: Color,
    val showBackgroundImage: Boolean,
    val backgroundImageTiledRepeat: Boolean,
    val stetchBackgroundImage: Boolean,
    val backgroundImageUri: String?,
    val marginTop: Int = PREF_DEFAULT_MARGIN,
    val marginBottom: Int = PREF_DEFAULT_MARGIN_BOTTOM,
    val marginBottomInfoArea: Int = PREF_DEFAULT_MARGIN_BOTTOM_INFOAREA,
    val marginLeft: Int = PREF_DEFAULT_MARGIN,
    val marginRight: Int = PREF_DEFAULT_MARGIN,
)


val defaultColors = arrayOf(
    ThemeColors(
        Color(0xFFFBF0D9),
        Color(0xFF5F4B32),
        Color(0xFF2196F3),
        Color(0xFF5F4B32),
        Color(0xFFFF9800),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        stetchBackgroundImage = true,
        backgroundImageUri = null,
    ),
    ThemeColors(
        Color(0xFF000000),
        Color(0xFFFBF0D9),
        Color(0xFF2196F3),
        Color(0xFFFBF0D9),
        Color(0xFF94713F),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        stetchBackgroundImage = true,
        backgroundImageUri = null,
    ),
    ThemeColors(
        Color(0xff3e1104),
        Color(0xFFe57614),
        Color(0xFF005CA6),
        Color(0xFFe57614),
        Color(0xFF506B30),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        stetchBackgroundImage = true,
        backgroundImageUri = null,
    ),
    ThemeColors(
        Color(0xff000000),
        Color(0xFF008705),
        Color(0xFF00599F),
        Color(0xFF008705),
        Color(0xFF195A58),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        stetchBackgroundImage = true,
        backgroundImageUri = null,
    ),
    ThemeColors(
        Color(0xff000000),
        Color(0xFF8C8A83),
        Color(0xFF3D7198),
        Color(0xFF8C8A83),
        Color(0xFF3D7198),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        stetchBackgroundImage = true,
        backgroundImageUri = null,
    ),
)

val defaultsFonts = mapOf(
    TextType.Normal to TypefaceRecord(name = SANSSERIF),
    TextType.Bold to TypefaceRecord(name = SANSSERIF),
    TextType.Italic to TypefaceRecord(name = SERIF),
    TextType.BoldItalic to TypefaceRecord(name = SERIF),
    TextType.Monospace to TypefaceRecord(name = MONO),
    TextType.InfoArea to TypefaceRecord(name = SANSSERIF),
)

enum class Actions {
    None,
    PagePrev,
    PageNext,
    MainMenu,
    GoTo,
    Bookmarks,
    QuickMenu;

    fun toTitle(): Int {
        return when (this) {
            None -> R.string.tapzones_activity_none
            PagePrev -> R.string.tapzones_activity_page_prev
            PageNext -> R.string.tapzones_activity_page_next
            QuickMenu -> R.string.tapzones_activity_quick_menu
            MainMenu -> R.string.tapzones_activity_main_menu
            GoTo -> R.string.tapzones_activity_goto
            Bookmarks -> R.string.tapzones_activity_bookmarks
        }
    }
}



val defaultTapZoneOneTopLeft = Actions.PagePrev
val defaultTapZoneOneTopCenter = Actions.Bookmarks
val defaultTapZoneOneTopRight = Actions.PageNext
val defaultTapZoneOneMiddleLeft = Actions.PagePrev
val defaultTapZoneOneMiddleCenter = Actions.MainMenu
val defaultTapZoneOneMiddleRight = Actions.PageNext
val defaultTapZoneOneBottomLeft = Actions.PagePrev
val defaultTapZoneOneBottomCenter = Actions.GoTo
val defaultTapZoneOneBottomRight = Actions.PageNext

val defaultTapZoneDoubleTopLeft = Actions.PagePrev
val defaultTapZoneDoubleTopCenter = Actions.None
val defaultTapZoneDoubleTopRight = Actions.PageNext
val defaultTapZoneDoubleMiddleLeft = Actions.PagePrev
val defaultTapZoneDoubleMiddleCenter = Actions.QuickMenu
val defaultTapZoneDoubleMiddleRight = Actions.PageNext
val defaultTapZoneDoubleBottomLeft = Actions.PagePrev
val defaultTapZoneDoubleBottomCenter = Actions.GoTo
val defaultTapZoneDoubleBottomRight = Actions.PageNext

val interface_entries = arrayOf(
    R.string.InterfaceLight,
    R.string.InterfaceDark,
    R.string.InterfaceAutoSystem)
val interface_values = arrayOf(
    "Light",
    "Dark",
    "Auto")


val brightness_entries = arrayOf(
    R.string.brightness_values_system,
    R.string.brightness_values_manual)

val brightness_values = arrayOf(
    "System",
    "Manual")

val orientation_enties = arrayOf(
    R.string.orientation_values_sensor,
    R.string.orientation_values_portrait,
    R.string.orientation_values_portrait_sensor,
    R.string.orientation_values_landscape,
    R.string.orientation_values_landscape_sensor,
)
val orientation_values = arrayOf(
    "Sensor",
    "Portrait",
    "PortraitSensor",
    "Landscape",
    "LandscapeSensor"
)

val selected_theme_entries = arrayOf(
    R.string.color_theme1_header,
    R.string.color_theme2_header,
    R.string.color_theme3_header,
    R.string.color_theme4_header,
    R.string.color_theme5_header,
)

val tapzonen_entries = arrayOf(
    R.string.tapzones_activity_none,
    R.string.tapzones_activity_page_prev,
    R.string.tapzones_activity_page_next,
    R.string.tapzones_activity_quick_menu,
    R.string.tapzones_activity_main_menu,
    R.string.tapzones_activity_goto,
    R.string.tapzones_activity_bookmarks,
)
val tapzonen_values = arrayOf(
    "None",
    "PagePrev",
    "PageNext",
    "QuickMenu",
    "MainMenu",
    "GoTo",
    "Bookmarks"
)

val line_spacing_values = arrayOf(1f, 1.15f, 1.5f, 2.0f, 2.5f, 3.0f)
val line_spacing_values_string = line_spacing_values.map { it.toString() }
val line_spacing_entries = line_spacing_values.map { "%.2f".format(Locale.getDefault(), it) }

val letter_spacing_values = arrayOf(0f, 0.01f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f)
val letter_spacing_values_string = letter_spacing_values.map { it.toString() }
val letter_spacing_entries = letter_spacing_values.map { "%.2f".format(Locale.getDefault(), it) }

@Composable
fun getStringArrayFromResourceArray(res: Array<Int>): List<String> {
    return res.map {
        stringResource(id = it)
    }
}

class SettingsViewModel(
    context: Context
) : ViewModel() {
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var interfaceTheme = mutableStateOf(interfaceThemeDefault)
    var brightness = mutableStateOf(brightnessDefault)
    var orientation = mutableStateOf(orientationDefault)
    var showSystemFonts = mutableStateOf(true)
    var showNotoFonts = mutableStateOf(true)

    var selectedColorTheme = mutableIntStateOf(PREF_COLOR_SELECTED_THEME_DEFAULT)

    var colors = defaultColors.mapIndexed { index, themeColors ->
        index to mutableStateOf(themeColors)
    }.toMap()

    var selectedColors = mutableStateOf(colors[PREF_COLOR_SELECTED_THEME_DEFAULT]!!.value)

    var fonts = mutableStateOf(defaultsFonts)

    var pageViewSettings = mutableStateOf(PageViewSettings())

    val tapOneAction = mutableStateOf(mapOf<ScreenZone, Actions>())
    val tapDoubleAction = mutableStateOf(mapOf<ScreenZone, Actions>())

    init {
        interfaceTheme.value = prefs.getString(PREFS_interface_theme, interfaceThemeDefault) ?: interfaceThemeDefault
        brightness.value = prefs.getString(PREFS_interface_brightness, brightnessDefault) ?: brightnessDefault
        orientation.value = prefs.getString(PREFS_orientation, orientationDefault) ?: orientationDefault

        selectedColorTheme.intValue = prefs.getInt(
            PREF_KEY_COLOR_SELECTED_THEME_INDEX,
            PREF_COLOR_SELECTED_THEME_DEFAULT
        )

        for (i in 0..4) {
            colors[i]!!.value = readTheme(i)
        }

        selectedColors.value = colors[selectedColorTheme.intValue]!!.value

        readFonts()

        readTabZones()

        val fontSize = prefs.getFloat(PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)
        val fontSizeInfoArea = prefs.getFloat(PREF_KEY_BOOK_TEXT_SIZE_INFO_AREA, defaultTextSizeInfoArea)

        val lineSpacingMultiplierString = prefs.getString(PREF_KEY_BOOK_LINE_SPACING, null)
        val lineSpacingMultiplier =
            lineSpacingMultiplierString?.toFloat() ?: defaultLineSpacingMultiplier

        val letterSpacingString = prefs.getString(PREF_KEY_BOOK_LETTER_SPACING, null)
        val letterSpacing = letterSpacingString?.toFloat() ?: defaultLetterSpacing

        pageViewSettings.value = pageViewSettings.value.copy(
            textSize = fontSize,
            textSizeInfoArea = fontSizeInfoArea,
            lineSpacingMultiplier = lineSpacingMultiplier,
            letterSpacing = letterSpacing
        )
    }

    private fun readTabZones() {
        try {
            tapOneAction.value = hashMapOf(
                ScreenZone.TopLeft to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_ONE_TOP_LEFT,
                    defaultTapZoneOneTopLeft.name
                ) ?: defaultTapZoneOneTopLeft.name),
                ScreenZone.TopCenter to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_ONE_TOP_CENTER,
                    defaultTapZoneOneTopCenter.name
                ) ?: defaultTapZoneOneTopCenter.name),
                ScreenZone.TopRight to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_ONE_TOP_RIGHT,
                    defaultTapZoneOneTopRight.name
                ) ?: defaultTapZoneOneTopRight.name),
                ScreenZone.MiddleLeft to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_ONE_MIDDLE_LEFT,
                    defaultTapZoneOneMiddleLeft.name
                ) ?: defaultTapZoneOneMiddleLeft.name),
                ScreenZone.MiddleCenter to defaultTapZoneOneMiddleCenter, // always default main menu
                ScreenZone.MiddleRight to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_ONE_MIDDLE_RIGHT,
                    defaultTapZoneOneMiddleRight.name
                ) ?: defaultTapZoneOneMiddleRight.name),
                ScreenZone.BottomLeft to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_ONE_BOTTOM_LEFT,
                    defaultTapZoneOneBottomLeft.name
                ) ?: defaultTapZoneOneBottomLeft.name),
                ScreenZone.BottomCenter to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_ONE_BOTTOM_CENTER,
                    defaultTapZoneOneBottomCenter.name
                ) ?: defaultTapZoneOneBottomCenter.name),
                ScreenZone.BottomRight to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_ONE_BOTTOM_RIGHT,
                    defaultTapZoneOneBottomRight.name
                ) ?: defaultTapZoneOneBottomRight.name),
            )

        tapDoubleAction.value =
            hashMapOf(
                ScreenZone.TopLeft to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_TOP_LEFT,
                    defaultTapZoneDoubleTopLeft.name
                ) ?: defaultTapZoneDoubleTopLeft.name),
                ScreenZone.TopCenter to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_TOP_CENTER,
                    defaultTapZoneDoubleTopCenter.name
                ) ?: defaultTapZoneDoubleTopCenter.name),
                ScreenZone.TopRight to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_TOP_RIGHT,
                    defaultTapZoneDoubleTopRight.name
                ) ?: defaultTapZoneDoubleTopRight.name),
                ScreenZone.MiddleLeft to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_MIDDLE_LEFT,
                    defaultTapZoneDoubleMiddleLeft.name
                ) ?: defaultTapZoneDoubleMiddleLeft.name),
                ScreenZone.MiddleCenter to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_MIDDLE_CENTER,
                    defaultTapZoneDoubleMiddleCenter.name
                ) ?: defaultTapZoneDoubleMiddleCenter.name),
                ScreenZone.MiddleRight to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_MIDDLE_RIGHT,
                    defaultTapZoneDoubleMiddleRight.name
                ) ?: defaultTapZoneDoubleMiddleRight.name),
                ScreenZone.BottomLeft to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_BOTTOM_LEFT,
                    defaultTapZoneDoubleBottomLeft.name
                ) ?: defaultTapZoneDoubleBottomLeft.name),
                ScreenZone.BottomCenter to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_BOTTOM_CENTER,
                    defaultTapZoneDoubleBottomCenter.name
                ) ?: defaultTapZoneDoubleBottomCenter.name),
                ScreenZone.BottomRight to Actions.valueOf(prefs.getString(
                    PREF_KEY_TAP_DOUBLE_BOTTOM_RIGHT,
                    defaultTapZoneDoubleBottomRight.name
                ) ?: defaultTapZoneDoubleBottomRight.name)
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readFonts() {
        showSystemFonts.value = prefs.getBoolean(PREF_KEY_USE_SYSTEM_FONTS, false)
        showNotoFonts.value = prefs.getBoolean(PREF_KEY_SHOW_NOTO_FONTS, false)

        val pathNormal = prefs.getString(PREF_KEY_BOOK_FONT_PATH_NORMAL, null)
        val nameNormal = prefs.getString(PREF_KEY_BOOK_FONT_NAME_NORMAL, defaultsFonts[TextType.Normal]?.name) ?: TypefaceRecord.DEFAULT.name

        val pathBold = prefs.getString(PREF_KEY_BOOK_FONT_PATH_BOLD, null)
        val nameBold = prefs.getString(PREF_KEY_BOOK_FONT_NAME_BOLD, defaultsFonts[TextType.Bold]?.name) ?: TypefaceRecord.DEFAULT.name

        val pathItalic = prefs.getString(PREF_KEY_BOOK_FONT_PATH_ITALIC, null)
        val nameItalic = prefs.getString(PREF_KEY_BOOK_FONT_NAME_ITALIC, defaultsFonts[TextType.Italic]?.name) ?: TypefaceRecord.DEFAULT.name

        val pathBoldItalic = prefs.getString(PREF_KEY_BOOK_FONT_PATH_BOLDITALIC, null)
        val nameBoldItalic = prefs.getString(PREF_KEY_BOOK_FONT_NAME_BOLDITALIC, defaultsFonts[TextType.Bold]?.name) ?: TypefaceRecord.DEFAULT.name

        val pathMono = prefs.getString(PREF_KEY_BOOK_FONT_PATH_MONOSPACE, null)
        val nameMono = prefs.getString(PREF_KEY_BOOK_FONT_NAME_MONOSPACE, defaultsFonts[TextType.Monospace]?.name) ?: MONO

        val pathInfoArea = prefs.getString(PREF_KEY_BOOK_FONT_PATH_INFO_AREA, null)
        val nameInfoArea = prefs.getString(PREF_KEY_BOOK_FONT_NAME_INFO_AREA, defaultsFonts[TextType.InfoArea]?.name) ?: MONO

        fonts.value = mapOf(
            TextType.Normal to TypefaceRecord(nameNormal, pathNormal),
            TextType.Bold to TypefaceRecord(nameBold, pathBold),
            TextType.Italic to TypefaceRecord(nameItalic, pathItalic),
            TextType.BoldItalic to TypefaceRecord(nameBoldItalic, pathBoldItalic),
            TextType.Monospace to TypefaceRecord(nameMono, pathMono),
            TextType.InfoArea to TypefaceRecord(nameInfoArea, pathInfoArea),
        )
    }

    private fun readTheme(colorThemeIndex: Int): ThemeColors {
        var co = prefs.getInt(PREF_KEY_COLOR_BACK + colorThemeIndex, 0)
        val colorBack =
            if (co != 0) Color(co)
            else defaultColors[colorThemeIndex].colorBackground

        co = prefs.getInt(PREF_KEY_COLOR_TEXT + colorThemeIndex, 0)
        val colorText =
            if (co != 0) Color(co)
            else defaultColors[colorThemeIndex].colorsText

        co = prefs.getInt(PREF_KEY_COLOR_LINKTEXT + colorThemeIndex, 0)
        val colorLinkText =
            if (co != 0) Color(co)
            else defaultColors[colorThemeIndex].colorsLink

        co = prefs.getInt(PREF_KEY_COLOR_INFOTEXT + colorThemeIndex, 0)
        val colorInfoText =
            if (co != 0) Color(co)
            else defaultColors[colorThemeIndex].colorsInfo

        co = prefs.getInt(PREF_KEY_COLOR_BOOKMARKS + colorThemeIndex, 0)
        val colorBookmark =
            if (co != 0) Color(co)
            else defaultColors[colorThemeIndex].colorBookmark

        val showBackgroundImage =
            prefs.getBoolean(PREF_KEY_SHOW_BACKGROUND_IMAGE + colorThemeIndex, false)
        val backgroundImageUri =
            prefs.getString(PREF_KEY_BACKGROUND_IMAGE_URI + colorThemeIndex, null)
        val backgroundImageTiledRepeat =
            prefs.getBoolean(PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT + colorThemeIndex, false)
        val stetchBackgroundImage =
            prefs.getBoolean(PREF_KEY_BACKGROUND_IMAGE_STRETCH + colorThemeIndex, true)

        return ThemeColors(
            colorBack,
            colorText,
            colorLinkText,
            colorInfoText,
            colorBookmark,
            showBackgroundImage,
            backgroundImageTiledRepeat,
            stetchBackgroundImage,
            backgroundImageUri,
            marginTop =  prefs.getInt(PREF_KEY_MARGIN_TOP, PREF_DEFAULT_MARGIN),
            marginBottom =  prefs.getInt(PREF_KEY_MARGIN_BOTTOM, PREF_DEFAULT_MARGIN_BOTTOM),
            marginBottomInfoArea =  prefs.getInt(PREF_KEY_MARGIN_BOTTOM_INFOAREA, PREF_DEFAULT_MARGIN_BOTTOM),
            marginLeft =  prefs.getInt(PREF_KEY_MARGIN_LEFT, PREF_DEFAULT_MARGIN),
            marginRight =  prefs.getInt(PREF_KEY_MARGIN_RIGHT, PREF_DEFAULT_MARGIN),
        )
    }

    fun changeinterfaceTheme(value: String) {
        interfaceTheme.value = value
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(PREFS_interface_theme, value)
        prefEditor.apply()
    }

    fun changebrightness(value: String) {
        brightness.value = value
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(PREFS_interface_brightness, value)
        prefEditor.apply()
    }

    fun changeorientation(value: String) {
        orientation.value = value
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(PREFS_orientation, value)
        prefEditor.apply()
    }
    fun changeselectedColorTheme(value: Int) {
        selectedColorTheme.intValue = value
        selectedColors.value = colors[selectedColorTheme.intValue]!!.value
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_SELECTED_THEME_INDEX, value)
        prefEditor.apply()
    }

    fun setDefaultColorBackground(themeIndex: Int) {
        changeColorBackground(themeIndex, defaultColors[themeIndex].colorBackground)
    }

    private fun changeColors(themeIndex: Int, newColors: ThemeColors) {
        colors[themeIndex]!!.value = newColors
        if (themeIndex == selectedColorTheme.intValue) selectedColors.value = newColors
    }

    fun changeColorBackground(themeIndex: Int, value: Color) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(colorBackground = value))

        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_BACK+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun setDefaultColorText(themeIndex: Int) {
        changeColorText(themeIndex, defaultColors[themeIndex].colorsText)
    }
    fun changeColorText(themeIndex: Int, value: Color) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(colorsText = value))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_TEXT+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun setDefaultColorLink(themeIndex: Int) {
        changeColorLink(themeIndex, defaultColors[themeIndex].colorsLink)
    }
    fun changeColorLink(themeIndex: Int, value: Color) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(colorsLink = value))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_LINKTEXT+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun setDefaultColorInfo(themeIndex: Int) {
        changeColorInfo(themeIndex, defaultColors[themeIndex].colorsInfo)
    }
    fun changeColorInfo(themeIndex: Int, value: Color) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(colorsInfo = value))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_INFOTEXT+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun setDefaultColorBookmark(themeIndex: Int) {
        changeColorBookmark(themeIndex, defaultColors[themeIndex].colorBookmark)
    }
    fun changeColorBookmark(themeIndex: Int, value: Color) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(colorBookmark = value))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_BOOKMARKS+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun changeShowBackgroundImage(themeIndex: Int, value: Boolean) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(showBackgroundImage = value))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putBoolean(PREF_KEY_SHOW_BACKGROUND_IMAGE+themeIndex, value)
        prefEditor.apply()
    }
    fun changeTileBackgroundImage(themeIndex: Int, value: Boolean) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(backgroundImageTiledRepeat = value))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putBoolean(PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT+themeIndex, value)
        prefEditor.apply()
    }
    fun changeStretchBackgroundImage(themeIndex: Int, value: Boolean) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(stetchBackgroundImage = value))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putBoolean(PREF_KEY_BACKGROUND_IMAGE_STRETCH+themeIndex, value)
        prefEditor.apply()
    }
    fun changeBackgroundImage(themeIndex: Int, value: Uri?) {
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(
            backgroundImageUri = value?.toString(),
            showBackgroundImage = value != null
            ))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        if (value != null)
            prefEditor.putString(PREF_KEY_BACKGROUND_IMAGE_URI+themeIndex, value.toString())
        else
            prefEditor.remove(PREF_KEY_BACKGROUND_IMAGE_URI+themeIndex)
        prefEditor.putBoolean(PREF_KEY_SHOW_BACKGROUND_IMAGE+themeIndex, value != null)
        prefEditor.apply()
    }
    fun changeMarginTop(themeIndex: Int, value: String) {
        val margin = value.toIntOrNull() ?: 0
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(marginTop = margin))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_TOP+themeIndex, margin)
        prefEditor.apply()
    }
    fun changeMarginBottom(themeIndex: Int, value: String) {
        val margin = value.toIntOrNull() ?: 0
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(marginBottom = margin))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_BOTTOM+themeIndex, margin)
        prefEditor.apply()
    }
    fun changeMarginBottomInfoArea(themeIndex: Int, value: String) {
        val margin = value.toIntOrNull() ?: 0
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(marginBottomInfoArea = margin))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_BOTTOM_INFOAREA+themeIndex, margin)
        prefEditor.apply()
    }
    fun changeMarginLeft(themeIndex: Int, value: String) {
        val margin = value.toIntOrNull() ?: 0
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(marginLeft = margin))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_LEFT+themeIndex, margin)
        prefEditor.apply()
    }
    fun changeMarginRight(themeIndex: Int, value: String) {
        val margin = value.toIntOrNull() ?: 0
        changeColors(themeIndex, colors[themeIndex]!!.value.copy(marginRight = margin))
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_RIGHT+themeIndex, margin)
        prefEditor.apply()
    }

    fun changeFont(type: TextType, typefaceRecord: TypefaceRecord) {
        fonts.value = fonts.value.map { entry -> entry.key to if (type == entry.key )
            typefaceRecord  else entry.value }.toMap()

        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(getFontNamePref(type), typefaceRecord.name)
        prefEditor.putString(getFontPathPref(type), typefaceRecord.file?.absolutePath)
        prefEditor.apply()
    }

    fun changeOneClick(zone: ScreenZone, action: String) {
        tapOneAction.value = tapOneAction.value.map {
            entry -> entry.key to if (zone == entry.key) Actions.valueOf(action) else entry.value
        }.toMap()

        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(getTapOneKey(zone), action)
        prefEditor.apply()
    }

    fun changeDoubleClick(zone: ScreenZone, action: String) {
        tapDoubleAction.value = tapDoubleAction.value.map {
                entry -> entry.key to if (zone == entry.key) Actions.valueOf(action) else entry.value
        }.toMap()

        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(getTapDoubleKey(zone), action)
        prefEditor.apply()
    }

    fun changeShowSystemFonts(value: Boolean) {
        showSystemFonts.value = value
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putBoolean(PREF_KEY_USE_SYSTEM_FONTS, value)
        prefEditor.apply()
    }
    fun changeShowNotoFonts(value: Boolean) {
        showNotoFonts.value = value
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putBoolean(PREF_KEY_SHOW_NOTO_FONTS, value)
        prefEditor.apply()
    }

    fun changeFontSize(size: Float) {
        pageViewSettings.value = pageViewSettings.value.copy(textSize = size)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putFloat(PREF_KEY_BOOK_TEXT_SIZE, size)
        prefEditor.apply()
    }

    fun changeFontSizeInfoArea(size: Float) {
        pageViewSettings.value = pageViewSettings.value.copy(textSizeInfoArea = size)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putFloat(PREF_KEY_BOOK_TEXT_SIZE_INFO_AREA, size)
        prefEditor.apply()
    }

    fun changeLineSpacingMultiplier(value: Float) {
        pageViewSettings.value = pageViewSettings.value.copy(lineSpacingMultiplier = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(PREF_KEY_BOOK_LINE_SPACING, value.toString())
        prefEditor.apply()
    }

    fun changeLetterSpacing(value: Float) {
        pageViewSettings.value = pageViewSettings.value.copy(letterSpacing = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(PREF_KEY_BOOK_LETTER_SPACING, value.toString())
        prefEditor.apply()
    }

    fun onFinishQuickMenuDialog(
        textSize: Float,
        lineSpacingMultiplier: Float,
        letterSpacing: Float,
        colorThemeIndex: Int) {

        pageViewSettings.value = pageViewSettings.value.copy(
            textSize = textSize,
            lineSpacingMultiplier = lineSpacingMultiplier,
            letterSpacing = letterSpacing
        )
        selectedColorTheme.intValue = colorThemeIndex
        selectedColors.value = colors[colorThemeIndex]!!.value

        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putFloat(PREF_KEY_BOOK_TEXT_SIZE, textSize)
        prefEditor.putString(PREF_KEY_BOOK_LINE_SPACING, lineSpacingMultiplier.toString())
        prefEditor.putString(PREF_KEY_BOOK_LETTER_SPACING, letterSpacing.toString())
        prefEditor.putInt(PREF_KEY_COLOR_SELECTED_THEME_INDEX, colorThemeIndex)
        prefEditor.apply()
    }

    fun isDarkMode(context: Context): Boolean {
//        println("${interfaceTheme.value} ${context.resources?.configuration?.uiMode?.and(
//            Configuration.UI_MODE_NIGHT_MASK)}")
        return when (interfaceTheme.value) {
            "Light" -> false
            "Dark" -> true
            else -> when (context.resources?.configuration?.uiMode?.and(
                Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                else -> false
            }
        }
    }
}

private fun getTapOneKey(zone: ScreenZone): String {
    return when (zone) {
        ScreenZone.TopLeft -> PREF_KEY_TAP_ONE_TOP_LEFT
        ScreenZone.TopCenter -> PREF_KEY_TAP_ONE_TOP_CENTER
        ScreenZone.TopRight -> PREF_KEY_TAP_ONE_TOP_RIGHT
        ScreenZone.MiddleLeft -> PREF_KEY_TAP_ONE_MIDDLE_LEFT
        ScreenZone.MiddleCenter -> PREF_KEY_TAP_ONE_MIDDLE_CENTER
        ScreenZone.MiddleRight -> PREF_KEY_TAP_ONE_MIDDLE_RIGHT
        ScreenZone.BottomLeft -> PREF_KEY_TAP_ONE_BOTTOM_LEFT
        ScreenZone.BottomCenter -> PREF_KEY_TAP_ONE_BOTTOM_CENTER
        ScreenZone.BottomRight -> PREF_KEY_TAP_ONE_BOTTOM_RIGHT
    }
}

private fun getTapDoubleKey(zone: ScreenZone): String {
    return when (zone) {
        ScreenZone.TopLeft -> PREF_KEY_TAP_DOUBLE_TOP_LEFT
        ScreenZone.TopCenter -> PREF_KEY_TAP_DOUBLE_TOP_CENTER
        ScreenZone.TopRight -> PREF_KEY_TAP_DOUBLE_TOP_RIGHT
        ScreenZone.MiddleLeft -> PREF_KEY_TAP_DOUBLE_MIDDLE_LEFT
        ScreenZone.MiddleCenter -> PREF_KEY_TAP_DOUBLE_MIDDLE_CENTER
        ScreenZone.MiddleRight -> PREF_KEY_TAP_DOUBLE_MIDDLE_RIGHT
        ScreenZone.BottomLeft -> PREF_KEY_TAP_DOUBLE_BOTTOM_LEFT
        ScreenZone.BottomCenter -> PREF_KEY_TAP_DOUBLE_BOTTOM_CENTER
        ScreenZone.BottomRight -> PREF_KEY_TAP_DOUBLE_BOTTOM_RIGHT
    }
}

private fun getFontPathPref(fonttype: TextType): String {
    return when (fonttype) {
        TextType.Monospace -> {
            PREF_KEY_BOOK_FONT_PATH_MONOSPACE
        }
        TextType.Bold -> {
            PREF_KEY_BOOK_FONT_PATH_BOLD
        }
        TextType.Italic -> {
            PREF_KEY_BOOK_FONT_PATH_ITALIC
        }
        TextType.BoldItalic -> {
            PREF_KEY_BOOK_FONT_PATH_BOLDITALIC
        }
        TextType.InfoArea -> {
            PREF_KEY_BOOK_FONT_PATH_INFO_AREA
        }
        else -> {
            PREF_KEY_BOOK_FONT_PATH_NORMAL
        }
    }
}

private fun getFontNamePref(fonttype: TextType): String {
    return when (fonttype) {
        TextType.Monospace -> {
            PREF_KEY_BOOK_FONT_NAME_MONOSPACE
        }
        TextType.Bold -> {
            PREF_KEY_BOOK_FONT_NAME_BOLD
        }
        TextType.Italic -> {
            PREF_KEY_BOOK_FONT_NAME_ITALIC
        }
        TextType.BoldItalic -> {
            PREF_KEY_BOOK_FONT_NAME_BOLDITALIC
        }
        TextType.InfoArea -> {
            PREF_KEY_BOOK_FONT_NAME_INFO_AREA
        }
        else -> {
            PREF_KEY_BOOK_FONT_NAME_NORMAL
        }
    }
}
