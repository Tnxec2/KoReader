package com.kontranik.koreader.utils

import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.model.PageViewColorSettings
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min


class PrefsHelper() {


    companion object {

        /*
                 * Prefs
                 */
        const val PREF_BOOK_PATH = "BookPath"
        const val PREF_KEY_BOOK_TEXT_SIZE = "TextSize"
        const val PREF_KEY_BOOK_LINE_SPACING = "LineSpacing"
        const val PREF_KEY_BOOK_LETTER_SPACING = "LetterSpacing"

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
        const val PREF_SCREEN_BRIGHTNESS = "ScreenBrightness"

        const val PREF_KEY_THEME = "interface_theme"
        const val PREF_KEY_BRIGHTNESS = "brightness"
        const val PREF_KEY_ORIENTATION = "orientation"
        const val PREF_KEY_USE_SYSTEM_FONTS = "showSystemFonts"
        const val PREF_KEY_SHOW_NOTO_FONTS = "showNotoFonts"

        const val PREF_ORIENTATION_DEFAULT = "PortraitSensor"
        const val PREF_BRIGHTNESS_DEFAULT = "Manual"
        const val PREF_BRIGHTNESS_MANUAL = "Manual"
        const val PREF_THEME_DEFAULT = "Manual"

        const val PREF_COLOR_SELECTED_THEME_DEFAULT = "1"
        const val PREF_KEY_COLOR_SELECTED_THEME = "selected_theme"
        const val PREF_KEY_BACKGROUND_IMAGE_URI = "backgroundImageTheme"
        const val PREF_KEY_SHOW_BACKGROUND_IMAGE = "backgroundImageEnableTheme"
        const val PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT = "backgroundImageTileTheme"


        const val PREF_KEY_COLOR_BACK = "colorBackTheme"
        const val PREF_KEY_COLOR_TEXT = "colorTextTheme"
        const val PREF_KEY_COLOR_INFOTEXT = "colorInfoTheme"
        const val PREF_KEY_COLOR_LINKTEXT = "colorLinkTheme"
        const val PREF_KEY_MERGE_TOP = "marginTopTheme"
        const val PREF_KEY_MERGE_BOTTOM = "marginBottomTheme"
        const val PREF_KEY_MERGE_LEFT = "marginLeftTheme1"
        const val PREF_KEY_MERGE_RIGHT = "marginRightTheme"

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

        val colorBackgroundDefaultArray = arrayListOf(
            R.string.color_theme1_backgroud_default,
            R.string.color_theme2_backgroud_default,
            R.string.color_theme3_backgroud_default,
            R.string.color_theme4_backgroud_default,
            R.string.color_theme5_backgroud_default,
        )
        val colorForegroundDefaultArray = arrayListOf(
            R.string.color_theme1_foregroud_default,
            R.string.color_theme2_foregroud_default,
            R.string.color_theme3_foregroud_default,
            R.string.color_theme4_foregroud_default,
            R.string.color_theme5_foregroud_default,
        )
        val colorInfotextDefaultArray = arrayListOf(
            R.string.color_theme1_foregroud_default,
            R.string.color_theme2_foregroud_default,
            R.string.color_theme3_foregroud_default,
            R.string.color_theme4_foregroud_default,
            R.string.color_theme5_foregroud_default,
        )
        val colorLinkDefaultArray = arrayListOf(
            R.string.color_theme1_linktext_default,
            R.string.color_theme2_linktext_default,
            R.string.color_theme3_linktext_default,
            R.string.color_theme4_linktext_default,
            R.string.color_theme5_linktext_default,
        )

        /*
         *  Textview Parameters
         */

        var defaultTextSize: Float = KoReaderApplication.getContext().resources.getDimension(R.dimen.text_size)
        var textSize: Float = defaultTextSize
        private val fontDefault: TypefaceRecord = TypefaceRecord.DEFAULT
        var font: TypefaceRecord = fontDefault


        var defaultLineSpacingMultiplier: Float = getFloatTypedValue(R.dimen.line_spacing)
        var lineSpacingMultiplier: Float = defaultLineSpacingMultiplier
        var defaultLetterSpacing: Float = 0.1f
        var letterSpacing: Float = defaultLetterSpacing

        const val textSizeMax: Float = 50F
        const val textSizeMin: Float = 6F

        fun getFloatTypedValue(resource: Int): Float {
            val typedValue = TypedValue()
            KoReaderApplication.getContext().resources.getValue(resource, typedValue, true)
            return typedValue.float
        }


        /*
         * Book Parameters
         */
        var bookPath: String? = null

        /*
         * App Parameters
         */
        var interfaceTheme: String? = PREF_THEME_DEFAULT



        /*
         *   Brightness
         */
        var screenBrightness: String? = PREF_BRIGHTNESS_DEFAULT
        var screenBrightnessLevel: Float = 0F
        var systemScreenBrightnessLevel: Float = 0.5f


        /*
         *  Color
         */
        var colorTheme: String = PREF_COLOR_SELECTED_THEME_DEFAULT
        private val colorBackDefault: String = KoReaderApplication.getContext().resources.getString(R.string.color_theme1_backgroud_default)
        var colorBack: String = colorBackDefault
        val colorTextDefault: String = KoReaderApplication.getContext().resources.getString(R.string.color_theme1_foregroud_default)
        var colorText: String = colorTextDefault
        val colorLinkTextDefault: String = KoReaderApplication.getContext().resources.getString(R.string.color_theme1_linktext_default)
        var colorLinkText: String = colorLinkTextDefault
        var colorInfoText: String = colorTextDefault
        var showBackgroundImage: Boolean = false
        var backgroundImageUri: String? = null
        var backgroundImageTiledRepeat: Boolean = false
        val marginDefault: Int = Integer.parseInt(KoReaderApplication.getContext().resources.getString(R.string.default_margin_value))
        var marginTop: Int = marginDefault
        var marginBottom: Int = marginDefault
        var marginLeft: Int = marginDefault
        var marginRight: Int = marginDefault

        /*
         *  Tap Zonen
         */
        var tapZoneOneTopLeft: String? = "PagePrev"
        var tapZoneOneTopCenter: String? = "None"
        var tapZoneOneTopRight: String? = "PageNext"
        var tapZoneOneMiddleLeft: String? = "PagePrev"
        var tapZoneOneMiddleCenter: String? = "MainMenu"
        var tapZoneOneMiddleRight: String? = "PageNext"
        var tapZoneOneBottomLeft: String? = "PagePrev"
        var tapZoneOneBottomCenter: String? = "GoTo"
        var tapZoneOneBottomRight: String? = "PageNext"

        val tapZoneDoubleTopLeft: String = "PagePrev"
        val tapZoneDoubleTopCenter: String = "None"
        val tapZoneDoubleTopRight: String = "PageNext"
        val tapZoneDoubleMiddleLeft: String = "PagePrev"
        val tapZoneDoubleMiddleCenter: String = "QuickMenu"
        val tapZoneDoubleMiddleRight: String = "PageNext"
        val tapZoneDoubleBottomLeft: String = "PagePrev"
        val tapZoneDoubleBottomCenter: String = "GoTo"
        val tapZoneDoubleBottomRight: String = "PageNext"

        val tapZoneLongTopLeft: String = "None"
        val tapZoneLongTopCenter: String = "None"
        val tapZoneLongTopRight: String = "None"
        val tapZoneLongMiddleLeft: String = "None"
        val tapZoneLongMiddleCenter: String = "None"
        val tapZoneLongMiddleRight: String = "None"
        val tapZoneLongBottomLeft: String = "None"
        val tapZoneLongBottomCenter: String = "None"
        val tapZoneLongBottomRight: String = "None"

        var tapOneAction: EnumMap<ScreenZone, String?> = EnumMap(ScreenZone::class.java)
        var tapDoubleAction: EnumMap<ScreenZone, String?> = EnumMap(ScreenZone::class.java)
        var tapLongAction: EnumMap<ScreenZone, String?> = EnumMap(ScreenZone::class.java)

        private val screenBrightnessLevelMin: Float = 0.01F
        private val screenBrightnessLevelMax: Float = 1F
        private val screenBrightnessLevelStep: Float = 0.002F

        var screenOrientation: String? = PREF_ORIENTATION_DEFAULT

        fun increaseScreenBrghtness(activity: Activity, point: Point, width: Int) {
            if ( screenBrightness == PREF_BRIGHTNESS_MANUAL ) {
                if ( point.x < 32 || point.x > width-32) {
                    screenBrightnessLevel += screenBrightnessLevelStep
                    screenBrightnessLevel = min(screenBrightnessLevel, screenBrightnessLevelMax)
                    setScreenBrightness(activity, screenBrightnessLevel)
                }
            }
        }

        fun decreaseScreenBrghtness(activity: Activity, point: Point, width: Int) {
            if ( screenBrightness == PREF_BRIGHTNESS_MANUAL ) {
                if ( point.x < 32 || point.x > width-32) {
                    screenBrightnessLevel -= screenBrightnessLevelStep
                    screenBrightnessLevel = max(screenBrightnessLevel, screenBrightnessLevelMin)
                    setScreenBrightness(activity, screenBrightnessLevel)
                }
            }
        }

        fun setOrientation(activity: Activity) {
            if ( screenOrientation == null) return
            activity.requestedOrientation = when (screenOrientation) {
                "Sensor" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
                "Portrait" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                "PortraitSensor" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                "Landscape" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                "LandscapeSensor" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }

        fun setThemeDefault() {
            setThemeByName(interfaceTheme)
        }

        fun isDarkMode(): Boolean {
            return when ( interfaceTheme) {
                    "Light" -> false
                    "Dark" -> true
                    else -> when (KoReaderApplication.getContext().resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                                Configuration.UI_MODE_NIGHT_YES -> true
                                Configuration.UI_MODE_NIGHT_NO -> false
                                else -> false
                            }
            }
        }

        private fun setThemeByName(mTheme: String?) {
            when ( mTheme) {
                "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
            }
        }

        fun setScreenBrightness(activity: Activity, level: Float) {
            var layoutpars: WindowManager.LayoutParams? = null
            layoutpars = activity.window.attributes
            systemScreenBrightnessLevel = layoutpars!!.screenBrightness

            if ( screenBrightness == PREF_BRIGHTNESS_MANUAL ) {
                layoutpars!!.screenBrightness = level
                activity.window.attributes = layoutpars
            }
        }

        fun loadSettings(activity: Activity) {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(KoReaderApplication.getContext())
            loadInterfaceSettings(prefs)
            loadTextSettings(prefs)
            loadFontSettings(prefs)
            loadMarginSettings(prefs)
            loadTapSettings(prefs)
            setOrientation(activity)
            setThemeDefault()
        }

        private fun loadInterfaceSettings(prefs: SharedPreferences) {
            interfaceTheme = prefs.getString(PREF_KEY_THEME, "Auto")
            screenOrientation =
                prefs.getString(PREF_KEY_ORIENTATION, "PortraitSensor")
            screenBrightness = prefs.getString(PREF_KEY_BRIGHTNESS, "Manual")
            colorTheme = prefs.getString(
                PREF_KEY_COLOR_SELECTED_THEME,
                PREF_COLOR_SELECTED_THEME_DEFAULT
            )
                ?: PREF_COLOR_SELECTED_THEME_DEFAULT
        }

        private fun loadTapSettings(prefs: SharedPreferences) {
            tapDoubleAction = EnumMap(
                hashMapOf(
                    ScreenZone.TopLeft to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_TOP_LEFT,
                        tapZoneDoubleTopLeft
                    ),
                    ScreenZone.TopCenter to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_TOP_CENTER,
                        tapZoneDoubleTopCenter
                    ),
                    ScreenZone.TopRight to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_TOP_RIGHT,
                        tapZoneDoubleTopRight
                    ),
                    ScreenZone.MiddleLeft to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_MIDDLE_LEFT,
                        tapZoneDoubleMiddleLeft
                    ),
                    ScreenZone.MiddleCenter to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_MIDDLE_CENTER,
                        tapZoneDoubleMiddleCenter
                    ),
                    ScreenZone.MiddleRight to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_MIDDLE_RIGHT,
                        tapZoneDoubleMiddleRight
                    ),
                    ScreenZone.BottomLeft to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_BOTTOM_LEFT,
                        tapZoneDoubleBottomLeft
                    ),
                    ScreenZone.BottomCenter to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_BOTTOM_CENTER,
                        tapZoneDoubleBottomCenter
                    ),
                    ScreenZone.BottomRight to prefs.getString(
                        PREF_KEY_TAP_DOUBLE_BOTTOM_RIGHT,
                        tapZoneDoubleBottomRight
                    ),
                )
            )

            tapOneAction = EnumMap(
                hashMapOf(
                    ScreenZone.TopLeft to prefs.getString(
                        PREF_KEY_TAP_ONE_TOP_LEFT,
                        tapZoneOneTopLeft
                    ),
                    ScreenZone.TopCenter to prefs.getString(
                        PREF_KEY_TAP_ONE_TOP_CENTER,
                        tapZoneOneTopCenter
                    ),
                    ScreenZone.TopRight to prefs.getString(
                        PREF_KEY_TAP_ONE_TOP_RIGHT,
                        tapZoneOneTopRight
                    ),
                    ScreenZone.MiddleLeft to prefs.getString(
                        PREF_KEY_TAP_ONE_MIDDLE_LEFT,
                        tapZoneOneMiddleLeft
                    ),
                    //                ScreenZone.MiddleCenter to prefs.getString(
                    //                    PREF_KEY_TAP_ONE_MIDDLE_CENTER,
                    //                    tapZoneOneMiddleCenter
                    //                ),
                    ScreenZone.MiddleCenter to tapZoneOneMiddleCenter, // always default main menu
                    ScreenZone.MiddleRight to prefs.getString(
                        PREF_KEY_TAP_ONE_MIDDLE_RIGHT,
                        tapZoneOneMiddleRight
                    ),
                    ScreenZone.BottomLeft to prefs.getString(
                        PREF_KEY_TAP_ONE_BOTTOM_LEFT,
                        tapZoneOneBottomLeft
                    ),
                    ScreenZone.BottomCenter to prefs.getString(
                        PREF_KEY_TAP_ONE_BOTTOM_CENTER,
                        tapZoneOneBottomCenter
                    ),
                    ScreenZone.BottomRight to prefs.getString(
                        PREF_KEY_TAP_ONE_BOTTOM_RIGHT,
                        tapZoneOneBottomRight
                    ),
                )
            )
            tapLongAction = EnumMap(
                hashMapOf(
                    ScreenZone.TopLeft to prefs.getString(
                        PREF_KEY_TAP_LONG_TOP_LEFT,
                        tapZoneLongTopLeft
                    ),
                    ScreenZone.TopCenter to prefs.getString(
                        PREF_KEY_TAP_LONG_TOP_CENTER,
                        tapZoneLongTopCenter
                    ),
                    ScreenZone.TopRight to prefs.getString(
                        PREF_KEY_TAP_LONG_TOP_RIGHT,
                        tapZoneLongTopRight
                    ),
                    ScreenZone.MiddleLeft to prefs.getString(
                        PREF_KEY_TAP_LONG_MIDDLE_LEFT,
                        tapZoneLongMiddleLeft
                    ),
                    ScreenZone.MiddleCenter to prefs.getString(
                        PREF_KEY_TAP_LONG_MIDDLE_CENTER,
                        tapZoneLongMiddleCenter
                    ),
                    ScreenZone.MiddleRight to prefs.getString(
                        PREF_KEY_TAP_LONG_MIDDLE_RIGHT,
                        tapZoneLongMiddleRight
                    ),
                    ScreenZone.BottomLeft to prefs.getString(
                        PREF_KEY_TAP_LONG_BOTTOM_LEFT,
                        tapZoneLongBottomLeft
                    ),
                    ScreenZone.BottomCenter to prefs.getString(
                        PREF_KEY_TAP_LONG_BOTTOM_CENTER,
                        tapZoneLongBottomCenter
                    ),
                    ScreenZone.BottomRight to prefs.getString(
                        PREF_KEY_TAP_LONG_BOTTOM_RIGHT,
                        tapZoneLongBottomRight
                    ),
                )
            )
        }

        private fun loadMarginSettings(prefs: SharedPreferences) {
            var sMargin =
                prefs.getString(PREF_KEY_MERGE_TOP + colorTheme, null)
            marginTop = try {
                if (sMargin != null) Integer.parseInt(sMargin) else marginDefault
            } catch (e: Exception) {
                marginDefault
            }
            sMargin =
                prefs.getString(PREF_KEY_MERGE_BOTTOM + colorTheme, null)
            marginBottom = try {
                if (sMargin != null) Integer.parseInt(sMargin) else marginDefault
            } catch (e: Exception) {
                marginDefault
            }
            sMargin = prefs.getString(PREF_KEY_MERGE_LEFT + colorTheme, null)
            marginLeft = try {
                if (sMargin != null) Integer.parseInt(sMargin) else marginDefault
            } catch (e: Exception) {
                marginDefault
            }
            sMargin = prefs.getString(PREF_KEY_MERGE_RIGHT + colorTheme, null)
            marginRight = try {
                if (sMargin != null) Integer.parseInt(sMargin) else marginDefault
            } catch (e: Exception) {
                marginDefault
            }
        }

        private fun loadTextSettings(prefs: SharedPreferences) {
            textSize =
                prefs.getFloat(PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)

            val lineSpacingMultiplierString = prefs.getString(PREF_KEY_BOOK_LINE_SPACING, null)
            lineSpacingMultiplier =
                lineSpacingMultiplierString?.toFloat() ?: defaultLineSpacingMultiplier

            val letterSpacingString = prefs.getString(PREF_KEY_BOOK_LETTER_SPACING, null)
            letterSpacing = letterSpacingString?.toFloat() ?: defaultLetterSpacing
        }

        private fun loadFontSettings(prefs: SharedPreferences) {
            if (prefs.contains(PREF_KEY_BOOK_FONT_PATH_NORMAL)) {
                val fontpath = prefs.getString(PREF_KEY_BOOK_FONT_PATH_NORMAL, null)
                if (fontpath != null) {
                    val fontFile = File(fontpath)
                    if (fontFile.isFile && fontFile.canRead()) {
                        font = TypefaceRecord(name = fontFile.name, file = fontFile)
                    }
                }
                println("loaded fontpath: $fontpath")
            } else if (prefs.contains(PREF_KEY_BOOK_FONT_NAME_NORMAL)) {
                val fontname = prefs.getString(
                    PREF_KEY_BOOK_FONT_NAME_NORMAL,
                    TypefaceRecord.SANSSERIF
                )
                fontname?.let {
                    font = TypefaceRecord(name = it)
                }
                println("loaded fontname: ${font.name}")
            }
        }

        fun loadColorThemeSettings(): PageViewColorSettings {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(KoReaderApplication.getContext())

            var co = prefs.getInt(PREF_KEY_COLOR_BACK + colorTheme, 0)
            colorBack =
                if (co != 0) "#" + Integer.toHexString(co)
                else KoReaderApplication.getContext().resources.getString(colorBackgroundDefaultArray[colorTheme.toInt()-1])

            co = prefs.getInt(PREF_KEY_COLOR_TEXT + colorTheme, 0)
            colorText =
                if (co != 0) "#" + Integer.toHexString(co)
                else KoReaderApplication.getContext().resources.getString(colorForegroundDefaultArray[colorTheme.toInt()-1])

            co = prefs.getInt(PREF_KEY_COLOR_LINKTEXT + colorTheme, 0)
            colorLinkText =
                if (co != 0) "#" + Integer.toHexString(co)
                else KoReaderApplication.getContext().resources.getString(colorLinkDefaultArray[colorTheme.toInt()-1])

            co = prefs.getInt(PREF_KEY_COLOR_INFOTEXT + colorTheme, 0)
            colorInfoText =
                if (co != 0) "#" + Integer.toHexString(co)
                else KoReaderApplication.getContext().resources.getString(colorInfotextDefaultArray[colorTheme.toInt()-1])

            showBackgroundImage = prefs.getBoolean(
                PREF_KEY_SHOW_BACKGROUND_IMAGE + colorTheme,
                false
            )
            backgroundImageUri = prefs.getString(
                PREF_KEY_BACKGROUND_IMAGE_URI + colorTheme,
                null
            )
            backgroundImageTiledRepeat = prefs.getBoolean(
                PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT + colorTheme,
                false
            )
            return PageViewColorSettings(
                showBackgroundImage = showBackgroundImage,
                backgroundImageUri = backgroundImageUri,
                backgroundImageTiledRepeat = backgroundImageTiledRepeat,
                colorText = colorText,
                colorBack = colorBack,
                colorLink = colorLinkText,
                colorInfoText = colorInfoText,
            )
        }
    }
}