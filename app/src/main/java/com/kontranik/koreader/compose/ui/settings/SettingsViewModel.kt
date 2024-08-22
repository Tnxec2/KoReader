package com.kontranik.koreader.compose.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.kontranik.koreader.compose.theme.defaultLetterSpacing
import com.kontranik.koreader.compose.theme.defaultLineSpacingMultiplier
import com.kontranik.koreader.compose.theme.defaultTextSize
import com.kontranik.koreader.model.ScreenZone

import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord.Companion.MONO
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord.Companion.SANSSERIF
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord.Companion.SERIF
import java.util.EnumMap

const val PREFS_FILE = "Sort"
const val PREFS_interface_theme = "interface_theme"
const val PREFS_interface_brightness = "brightness"
const val PREFS_orientation = "orientation"

const val PREF_KEY_USE_SYSTEM_FONTS = "showSystemFonts"
const val PREF_KEY_SHOW_NOTO_FONTS = "showNotoFonts"

const val PREF_BOOK_PATH = "BookPath"
const val PREF_KEY_BOOK_TEXT_SIZE = "TextSize"
const val PREF_KEY_BOOK_LINE_SPACING = "LineSpacing"
const val PREF_KEY_BOOK_LETTER_SPACING = "LetterSpacing"

const val PREF_KEY_COLOR_BACK = "colorBackTheme"
const val PREF_KEY_COLOR_TEXT = "colorTextTheme"
const val PREF_KEY_COLOR_INFOTEXT = "colorInfoTheme"
const val PREF_KEY_COLOR_LINKTEXT = "colorLinkTheme"
const val PREF_KEY_MARGIN_TOP = "marginTopTheme"
const val PREF_KEY_MARGIN_BOTTOM = "marginBottomTheme"
const val PREF_KEY_MARGIN_LEFT = "marginLeftTheme1"
const val PREF_KEY_MARGIN_RIGHT = "marginRightTheme"
const val PREF_KEY_COLOR_SELECTED_THEME_INDEX = "selected_theme_index"
const val PREF_KEY_BACKGROUND_IMAGE_URI = "backgroundImageTheme"
const val PREF_KEY_SHOW_BACKGROUND_IMAGE = "backgroundImageEnableTheme"
const val PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT = "backgroundImageTileTheme"

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

const val PREF_KEY_TAP_LONG_TOP_LEFT = "tapZoneLongClickTopLeft"
const val PREF_KEY_TAP_LONG_TOP_CENTER = "tapZoneLongClickTopCenter"
const val PREF_KEY_TAP_LONG_TOP_RIGHT = "tapZoneLongClickTopRight"
const val PREF_KEY_TAP_LONG_MIDDLE_LEFT = "tapZoneLongClickMiddleLeft"
const val PREF_KEY_TAP_LONG_MIDDLE_CENTER = "tapZoneLongClickMiddleCenter"
const val PREF_KEY_TAP_LONG_MIDDLE_RIGHT = "tapZoneLongClickMiddleRight"
const val PREF_KEY_TAP_LONG_BOTTOM_LEFT = "tapZoneLongClickBottomLeft"
const val PREF_KEY_TAP_LONG_BOTTOM_CENTER = "tapZoneLongClickBottomCenter"
const val PREF_KEY_TAP_LONG_BOTTOM_RIGHT = "tapZoneOneClickBottomRight"

const val PREF_COLOR_SELECTED_THEME_DEFAULT = 0
const val PREF_DEFAULT_MARGIN = 0
const val interfaceThemeDefault = "Auto"
const val brightnessDefault = "System"
const val orientationDefault = "Sensor"



data class ThemeColors(
    val colorBackground: Color,
    val colorsText: Color,
    val colorsLink: Color,
    val colorsInfo: Color,
    val showBackgroundImage: Boolean,
    val backgroundImageTiledRepeat: Boolean,
    val backgroundImageUri: String?,
    val marginTop: String = PREF_DEFAULT_MARGIN.toString(),
    val marginBottom: String = PREF_DEFAULT_MARGIN.toString(),
    val marginLeft: String = PREF_DEFAULT_MARGIN.toString(),
    val marginRight: String = PREF_DEFAULT_MARGIN.toString(),
)


val defaultColors = arrayOf(
    ThemeColors(
        Color(0xFFFBF0D9),
        Color(0xFF5F4B32),
        Color(0xFF2196F3),
        Color(0xFFFBF0D9),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        backgroundImageUri = null,
    ),
    ThemeColors(
        Color(0xFF000000),
        Color(0xFFFBF0D9),
        Color(0xFF2196F3),
        Color(0xFF000000),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        backgroundImageUri = null,
    ),
    ThemeColors(
        Color(0xff3e1104),
        Color(0xFFe57614),
        Color(0xFF005CA6),
        Color(0xff3e1104),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        backgroundImageUri = null,
    ),
    ThemeColors(
        Color(0xff000000),
        Color(0xFF008705),
        Color(0xFF00599F),
        Color(0xff000000),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        backgroundImageUri = null,
    ),
    ThemeColors(
        Color(0xff000000),
        Color(0xFF8C8A83),
        Color(0xFF3D7198),
        Color(0xff000000),
        showBackgroundImage = false,
        backgroundImageTiledRepeat = false,
        backgroundImageUri = null,
    ),
)

val defaultsFonts = mapOf(
    TextType.Normal to TypefaceRecord(name = SANSSERIF),
    TextType.Bold to TypefaceRecord(name = SANSSERIF),
    TextType.Italic to TypefaceRecord(name = SERIF),
    TextType.BoldItalic to TypefaceRecord(name = SERIF),
    TextType.Monospace to TypefaceRecord(name = MONO),
)

const val defaultTapZoneOneTopLeft: String = "PagePrev"
const val defaultTapZoneOneTopCenter: String = "None"
const val defaultTapZoneOneTopRight: String = "PageNext"
const val defaultTapZoneOneMiddleLeft: String = "PagePrev"
const val defaultTapZoneOneMiddleCenter: String = "MainMenu"
const val defaultTapZoneOneMiddleRight: String = "PageNext"
const val defaultTapZoneOneBottomLeft: String = "PagePrev"
const val defaultTapZoneOneBottomCenter: String = "GoTo"
const val defaultTapZoneOneBottomRight: String = "PageNext"

const val defaultTapZoneDoubleTopLeft: String = "PagePrev"
const val defaultTapZoneDoubleTopCenter: String = "None"
const val defaultTapZoneDoubleTopRight: String = "PageNext"
const val defaultTapZoneDoubleMiddleLeft: String = "PagePrev"
const val defaultTapZoneDoubleMiddleCenter: String = "QuickMenu"
const val defaultTapZoneDoubleMiddleRight: String = "PageNext"
const val defaultTapZoneDoubleBottomLeft: String = "PagePrev"
const val defaultTapZoneDoubleBottomCenter: String = "GoTo"
const val defaultTapZoneDoubleBottomRight: String = "PageNext"

const val defaultTapZoneLongTopLeft: String = "None"
const val defaultTapZoneLongTopCenter: String = "None"
const val defaultTapZoneLongTopRight: String = "None"
const val defaultTapZoneLongMiddleLeft: String = "None"
const val defaultTapZoneLongMiddleCenter: String = "None"
const val defaultTapZoneLongMiddleRight: String = "None"
const val defaultTapZoneLongBottomLeft: String = "None"
const val defaultTapZoneLongBottomCenter: String = "None"
const val defaultTapZoneLongBottomRight: String = "None"


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

    var fonts = mutableStateOf(defaultsFonts)

    var fontSize = mutableFloatStateOf(defaultTextSize)
    var lineSpacingMultiplier = mutableFloatStateOf(defaultLineSpacingMultiplier)
    var letterSpacing = mutableFloatStateOf(defaultLetterSpacing)

    val tapOneAction: MutableState<EnumMap<ScreenZone, String?>> = mutableStateOf(EnumMap(ScreenZone::class.java))
    val tapDoubleAction: MutableState<EnumMap<ScreenZone, String?>> = mutableStateOf(EnumMap(ScreenZone::class.java))
    val tapLongAction: MutableState<EnumMap<ScreenZone, String?>> = mutableStateOf(EnumMap(ScreenZone::class.java))

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

        readFonts()

        fontSize.floatValue = prefs.getFloat(PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)

        val lineSpacingMultiplierString = prefs.getString(PREF_KEY_BOOK_LINE_SPACING, null)
        lineSpacingMultiplier.floatValue =
            lineSpacingMultiplierString?.toFloat() ?: defaultLineSpacingMultiplier

        val letterSpacingString = prefs.getString(PREF_KEY_BOOK_LETTER_SPACING, null)
        letterSpacing.floatValue = letterSpacingString?.toFloat() ?: defaultLetterSpacing
    }

    private fun readTabZones() {
        tapDoubleAction.value = EnumMap(
            hashMapOf(
                ScreenZone.TopLeft to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_TOP_LEFT,
                    defaultTapZoneDoubleTopLeft
                ),
                ScreenZone.TopCenter to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_TOP_CENTER,
                    defaultTapZoneDoubleTopCenter
                ),
                ScreenZone.TopRight to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_TOP_RIGHT,
                    defaultTapZoneDoubleTopRight
                ),
                ScreenZone.MiddleLeft to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_MIDDLE_LEFT,
                    defaultTapZoneDoubleMiddleLeft
                ),
                ScreenZone.MiddleCenter to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_MIDDLE_CENTER,
                    defaultTapZoneDoubleMiddleCenter
                ),
                ScreenZone.MiddleRight to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_MIDDLE_RIGHT,
                    defaultTapZoneDoubleMiddleRight
                ),
                ScreenZone.BottomLeft to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_BOTTOM_LEFT,
                    defaultTapZoneDoubleBottomLeft
                ),
                ScreenZone.BottomCenter to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_BOTTOM_CENTER,
                    defaultTapZoneDoubleBottomCenter
                ),
                ScreenZone.BottomRight to prefs.getString(
                    PREF_KEY_TAP_DOUBLE_BOTTOM_RIGHT,
                    defaultTapZoneDoubleBottomRight
                ),
            )
        )

        tapOneAction.value = EnumMap(
            hashMapOf(
                ScreenZone.TopLeft to prefs.getString(
                    PREF_KEY_TAP_ONE_TOP_LEFT,
                    defaultTapZoneOneTopLeft
                ),
                ScreenZone.TopCenter to prefs.getString(
                    PREF_KEY_TAP_ONE_TOP_CENTER,
                    defaultTapZoneOneTopCenter
                ),
                ScreenZone.TopRight to prefs.getString(
                    PREF_KEY_TAP_ONE_TOP_RIGHT,
                    defaultTapZoneOneTopRight
                ),
                ScreenZone.MiddleLeft to prefs.getString(
                    PREF_KEY_TAP_ONE_MIDDLE_LEFT,
                    defaultTapZoneOneMiddleLeft
                ),
                ScreenZone.MiddleCenter to defaultTapZoneOneMiddleCenter, // always default main menu
                ScreenZone.MiddleRight to prefs.getString(
                    PREF_KEY_TAP_ONE_MIDDLE_RIGHT,
                    defaultTapZoneOneMiddleRight
                ),
                ScreenZone.BottomLeft to prefs.getString(
                    PREF_KEY_TAP_ONE_BOTTOM_LEFT,
                    defaultTapZoneOneBottomLeft
                ),
                ScreenZone.BottomCenter to prefs.getString(
                    PREF_KEY_TAP_ONE_BOTTOM_CENTER,
                    defaultTapZoneOneBottomCenter
                ),
                ScreenZone.BottomRight to prefs.getString(
                    PREF_KEY_TAP_ONE_BOTTOM_RIGHT,
                    defaultTapZoneOneBottomRight
                ),
            )
        )
        tapLongAction.value = EnumMap(
            hashMapOf(
                ScreenZone.TopLeft to prefs.getString(
                    PREF_KEY_TAP_LONG_TOP_LEFT,
                    defaultTapZoneLongTopLeft
                ),
                ScreenZone.TopCenter to prefs.getString(
                    PREF_KEY_TAP_LONG_TOP_CENTER,
                    defaultTapZoneLongTopCenter
                ),
                ScreenZone.TopRight to prefs.getString(
                    PREF_KEY_TAP_LONG_TOP_RIGHT,
                    defaultTapZoneLongTopRight
                ),
                ScreenZone.MiddleLeft to prefs.getString(
                    PREF_KEY_TAP_LONG_MIDDLE_LEFT,
                    defaultTapZoneLongMiddleLeft
                ),
                ScreenZone.MiddleCenter to prefs.getString(
                    PREF_KEY_TAP_LONG_MIDDLE_CENTER,
                    defaultTapZoneLongMiddleCenter
                ),
                ScreenZone.MiddleRight to prefs.getString(
                    PREF_KEY_TAP_LONG_MIDDLE_RIGHT,
                    defaultTapZoneLongMiddleRight
                ),
                ScreenZone.BottomLeft to prefs.getString(
                    PREF_KEY_TAP_LONG_BOTTOM_LEFT,
                    defaultTapZoneLongBottomLeft
                ),
                ScreenZone.BottomCenter to prefs.getString(
                    PREF_KEY_TAP_LONG_BOTTOM_CENTER,
                    defaultTapZoneLongBottomCenter
                ),
                ScreenZone.BottomRight to prefs.getString(
                    PREF_KEY_TAP_LONG_BOTTOM_RIGHT,
                    defaultTapZoneLongBottomRight
                ),
            )
        )
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
        val nameMono = prefs.getString(PREF_KEY_BOOK_FONT_NAME_MONOSPACE, defaultsFonts[TextType.Monospace]?.name) ?: TypefaceRecord.MONO

        fonts.value = mapOf(
            TextType.Normal to TypefaceRecord(nameNormal, pathNormal),
            TextType.Bold to TypefaceRecord(nameBold, pathBold),
            TextType.Italic to TypefaceRecord(nameItalic, pathItalic),
            TextType.BoldItalic to TypefaceRecord(nameBoldItalic, pathBoldItalic),
            TextType.Monospace to TypefaceRecord(nameMono, pathMono),
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

        val showBackgroundImage =
            prefs.getBoolean(PREF_KEY_SHOW_BACKGROUND_IMAGE + colorThemeIndex, false)
        val backgroundImageUri =
            prefs.getString(PREF_KEY_BACKGROUND_IMAGE_URI + colorThemeIndex, null)
        val backgroundImageTiledRepeat =
            prefs.getBoolean(PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT + colorThemeIndex, false)

        return ThemeColors(
            colorBack,
            colorText,
            colorLinkText,
            colorInfoText,
            showBackgroundImage,
            backgroundImageTiledRepeat,
            backgroundImageUri,
            marginTop =  prefs.getInt(PREF_KEY_MARGIN_TOP, PREF_DEFAULT_MARGIN).toString(),
            marginBottom =  prefs.getInt(PREF_KEY_MARGIN_BOTTOM, PREF_DEFAULT_MARGIN).toString(),
            marginLeft =  prefs.getInt(PREF_KEY_MARGIN_LEFT, PREF_DEFAULT_MARGIN).toString(),
            marginRight =  prefs.getInt(PREF_KEY_MARGIN_RIGHT, PREF_DEFAULT_MARGIN).toString(),
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
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_SELECTED_THEME_INDEX, value)
        prefEditor.apply()
    }

    fun setDefaultColorBackground(themeIndex: Int) {
        changeColorBackground(themeIndex, defaultColors[themeIndex].colorBackground)
    }
    fun changeColorBackground(themeIndex: Int, value: Color) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(colorBackground = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_BACK+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun setDefaultColorText(themeIndex: Int) {
        changeColorText(themeIndex, defaultColors[themeIndex].colorsText)
    }
    fun changeColorText(themeIndex: Int, value: Color) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(colorsText = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_TEXT+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun setDefaultColorLink(themeIndex: Int) {
        changeColorLink(themeIndex, defaultColors[themeIndex].colorsLink)
    }
    fun changeColorLink(themeIndex: Int, value: Color) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(colorsLink = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_LINKTEXT+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun setDefaultColorInfo(themeIndex: Int) {
        changeColorInfo(themeIndex, defaultColors[themeIndex].colorsInfo)
    }
    fun changeColorInfo(themeIndex: Int, value: Color) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(colorsInfo = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_COLOR_INFOTEXT+themeIndex, value.toArgb())
        prefEditor.apply()
    }
    fun changeShowBackgroundImage(themeIndex: Int, value: Boolean) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(showBackgroundImage = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putBoolean(PREF_KEY_SHOW_BACKGROUND_IMAGE+themeIndex, value)
        prefEditor.apply()
    }
    fun changeTileBackgroundImage(themeIndex: Int, value: Boolean) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(backgroundImageTiledRepeat = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putBoolean(PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT+themeIndex, value)
        prefEditor.apply()
    }
    fun changeBackgroundImage(themeIndex: Int, value: String) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(backgroundImageUri = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(PREF_KEY_BACKGROUND_IMAGE_URI+themeIndex, value)
        prefEditor.apply()
    }
    fun changeMarginTop(themeIndex: Int, value: String) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(marginTop = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_TOP+themeIndex, value.toInt())
        prefEditor.apply()
    }
    fun changeMarginBottom(themeIndex: Int, value: String) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(marginBottom = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_BOTTOM+themeIndex, value.toInt())
        prefEditor.apply()
    }
    fun changeMarginLeft(themeIndex: Int, value: String) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(marginLeft = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_LEFT+themeIndex, value.toInt())
        prefEditor.apply()
    }
    fun changeMarginRight(themeIndex: Int, value: String) {
        colors[themeIndex]!!.value = colors[themeIndex]!!.value.copy(marginRight = value)
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putInt(PREF_KEY_MARGIN_RIGHT+themeIndex, value.toInt())
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
        tapOneAction.value = EnumMap(tapOneAction.value.map {
            entry -> entry.key to if (zone == entry.key) action else entry.value
        }.toMap())

        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(getTapOneKey(zone), action)
        prefEditor.apply()
    }

    fun changeDoubleClick(zone: ScreenZone, action: String) {
        tapDoubleAction.value = EnumMap(tapDoubleAction.value.map {
                entry -> entry.key to if (zone == entry.key) action else entry.value
        }.toMap())

        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(getTapDoubleKey(zone), action)
        prefEditor.apply()
    }
    fun changeLongClick(zone: ScreenZone, action: String) {
        tapLongAction.value = EnumMap(tapLongAction.value.map {
                entry -> entry.key to if (zone == entry.key) action else entry.value
        }.toMap())

        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(getTapLongKey(zone), action)
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
        fontSize.floatValue = size
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putFloat(PREF_KEY_BOOK_TEXT_SIZE, size)
        prefEditor.apply()
    }

    fun changeLineSpacingMultiplier(value: Float) {
        lineSpacingMultiplier.floatValue = value
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(PREF_KEY_BOOK_LINE_SPACING, value.toString())
        prefEditor.apply()
    }

    fun changeLetterSpacing(value: Float) {
        letterSpacing.floatValue = value
        val prefEditor: SharedPreferences.Editor = prefs.edit()
        prefEditor.putString(PREF_KEY_BOOK_LETTER_SPACING, value.toString())
        prefEditor.apply()
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

private fun getTapLongKey(zone: ScreenZone): String {
    return when (zone) {
        ScreenZone.TopLeft -> PREF_KEY_TAP_LONG_TOP_LEFT
        ScreenZone.TopCenter -> PREF_KEY_TAP_LONG_TOP_CENTER
        ScreenZone.TopRight -> PREF_KEY_TAP_LONG_TOP_RIGHT
        ScreenZone.MiddleLeft -> PREF_KEY_TAP_LONG_MIDDLE_LEFT
        ScreenZone.MiddleCenter -> PREF_KEY_TAP_LONG_MIDDLE_CENTER
        ScreenZone.MiddleRight -> PREF_KEY_TAP_LONG_MIDDLE_RIGHT
        ScreenZone.BottomLeft -> PREF_KEY_TAP_LONG_BOTTOM_LEFT
        ScreenZone.BottomCenter -> PREF_KEY_TAP_LONG_BOTTOM_CENTER
        ScreenZone.BottomRight -> PREF_KEY_TAP_LONG_BOTTOM_RIGHT
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
        else -> {
            PREF_KEY_BOOK_FONT_NAME_NORMAL
        }
    }
}
