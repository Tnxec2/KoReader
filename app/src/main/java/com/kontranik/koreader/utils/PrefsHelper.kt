package com.kontranik.koreader.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.kontranik.koreader.R
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import kotlin.math.max
import kotlin.math.min


class PrefsHelper(val context: Context) {

    private var layoutpars: WindowManager.LayoutParams? = null

    /*
     *  Textview Parameters
     */

    var defaultTextSize: Float = 0f
    var textSize: Float = 0f
    val fontDefault: TypefaceRecord = TypefaceRecord.DEFAULT
    var font: TypefaceRecord = fontDefault

    var defaultLineSpacing: Float = 1.0f
    var lineSpacing: Float = defaultLineSpacing
    var defaultLetterSpacing: Float = 0.1f
    var letterSpacing: Float = defaultLetterSpacing

    init {
        defaultTextSize = context.resources.getDimension(R.dimen.text_size)

        val typedValue = TypedValue()
        context.resources.getValue(R.dimen.line_spacing, typedValue, true)
        defaultLineSpacing = typedValue.float

        textSize = defaultTextSize
        lineSpacing = defaultLineSpacing
    }

    /*
     * Book Parameters
     */
    var bookPath: String? = null

    /*
     * App Parameters
     */
    var theme: String? = PREF_THEME_DEFAULT



    /*
     *   Brightness
     */
    var screenBrightness: String? = PREF_BRIGHTNESS_DEFAULT
    var screenBrightnessLevel: Float = 0F
    var systemScreenBrightnessLevel: Float = 0.5f

    /*
     *  Color
     */
    val colorLightBackDefault: String = context.resources.getString(R.string.color_light_backgroud_default)
    var colorLightBack: String? = colorLightBackDefault
    val colorLightTextDefault: String = context.resources.getString(R.string.color_light_foregroud_default)
    var colorLightText: String? = colorLightTextDefault
    val colorLightLinkTextDefault: String = context.resources.getString(R.string.color_light_linktext_default)
    var colorLightLinkText: String? = colorLightLinkTextDefault

    val colorDarkBackDefault: String = context.resources.getString(R.string.color_dark_backgroud_default)
    var colorDarkBack: String? = colorDarkBackDefault
    val colorDarkTextDefault: String = context.resources.getString(R.string.color_dark_foregroud_default)
    var colorDarkText: String? = colorDarkTextDefault
    val colorDarkLinkTextDefault: String = context.resources.getString(R.string.color_dark_linktext_default)
    var colorDarkLinkText: String? = colorDarkLinkTextDefault

    /*
     *  Tap Zonen
     */
    var tapZoneOneTopLeft: String? = "PagePrev"
    var tapZoneOneTopCenter: String? = "None"
    var tapZoneOneTopRight: String? = "PageNext"
    var tapZoneOneMiddleLeft: String? = "None"
    var tapZoneOneMiddleCenter: String? = "MainMenu"
    var tapZoneOneMiddleRight: String? = "None"
    var tapZoneOneBottomLeft: String? = "PagePrev"
    var tapZoneOneBottomCenter: String? = "None"
    var tapZoneOneBottomRight: String? = "PageNext"
    
    val tapZoneDoubleTopLeft: String = "None"
    val tapZoneDoubleTopCenter: String = "None"
    val tapZoneDoubleTopRight: String = "None"
    val tapZoneDoubleMiddleLeft: String = "None"
    val tapZoneDoubleMiddleCenter: String = "QuickMenu"
    val tapZoneDoubleMiddleRight: String = "None"
    val tapZoneDoubleBottomLeft: String = "PagePrev"
    val tapZoneDoubleBottomCenter: String = "None"
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

    var tapOneAction: HashMap<ScreenZone, String?> = hashMapOf()
    var tapDoubleAction: HashMap<ScreenZone, String?> = hashMapOf()
    var tapLongAction: HashMap<ScreenZone, String?> = hashMapOf()

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

    fun setTheme( ) {
        when ( theme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
        }
    }

    fun setScreenBrightness(activity: Activity, level: Float) {
        layoutpars = activity.window.attributes
        systemScreenBrightnessLevel = layoutpars!!.screenBrightness

        if ( screenBrightness == PREF_BRIGHTNESS_MANUAL ) {
            layoutpars!!.screenBrightness = level
            activity.window.attributes = layoutpars
        }
    }

    companion object {

        const val textSizeMax: Float = 50F
        const val textSizeMin: Float = 6F

        /*
         * Prefs
         */
        const val PREF_BOOK_PATH = "BookPath"
        const val PREF_KEY_BOOK_TEXT_SIZE = "TextSize"
        const val PREF_KEY_BOOK_LINE_SPACING = "LineSpacing"
        const val PREF_KEY_BOOK_LETTER_SPACING = "LetterSpacing"
        const val PREF_KEY_BOOK_FONT_NAME = "FontName"
        const val PREF_KEY_BOOK_FONT_PATH = "FontPath"
        const val PREF_SCREEN_BRIGHTNESS = "ScreenBrightness"

        const val PREF_KEY_THEME = "theme"
        const val PREF_KEY_BRIGHTNESS = "brightness"
        const val PREF_KEY_ORIENTATION = "orientation"
        const val PREF_KEY_USE_SYSTEM_FONTS = "showSystemFonts"
        const val PREF_KEY_SHOW_NOTO_FONTS = "showNotoFonts"

        const val PREF_ORIENTATION_DEFAULT = "PortraitSensor"
        const val PREF_BRIGHTNESS_DEFAULT = "Manual"
        const val PREF_BRIGHTNESS_MANUAL = "Manual"
        const val PREF_THEME_DEFAULT = "Manual"

        const val PREF_KEY_COLOR_LIGHT_BACK = "colorBackLightTheme"
        const val PREF_KEY_COLOR_LIGHT_TEXT = "colorTextLightTheme"
        const val PREF_KEY_COLOR_LIGHT_LINKTEXT = "colorLinkLightTheme"
        const val PREF_KEY_COLOR_DARK_BACK = "colorBackDarkTheme"
        const val PREF_KEY_COLOR_DARK_TEXT = "colorTextDarkTheme"
        const val PREF_KEY_COLOR_DARK_LINKTEXT = "colorLinkDarkTheme"

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

    }
}