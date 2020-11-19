package com.kontranik.koreader.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import kotlin.math.max
import kotlin.math.min


class PrefsHelper() {

    private var layoutpars: WindowManager.LayoutParams? = null

    /*
     * Book Parameters
     */
    var bookPath: String? = null

    /*
     * App Parameters
     */
    var theme: String? = PREF_THEME_DEFAULT

    /*
     *  Textview Parameters
     */

    var textSize: Float = 0f
    val fontDefault: TypefaceRecord = TypefaceRecord.DEFAULT
    var font: TypefaceRecord = fontDefault
    var defaultTextSize: Float = 0f

    /*
     *   Brightness
     */
    var screenBrightness: String? = PREF_BRIGHTNESS_DEFAULT
    var screenBrightnessLevel: Float = 0F
    var systemScreenBrightnessLevel: Float = 0.5f

    private val screenBrightnessLevelMin: Float = 0.01F
    private val screenBrightnessLevelMax: Float = 1F
    private val screenBrightnessLevelStep: Float = 0.002F

    var screenOrientation: String? = PREF_ORIENTATION_DEFAULT

    fun increaseScreenBrghtness(activity: Activity, point: Point, width: Int) {
        if ( screenBrightness == PREF_BRIGHTNESS_MANUAL ) {
            if ( point.x < 32 || point.x > width-32) {
                screenBrightnessLevel += screenBrightnessLevelStep
                screenBrightnessLevel = min(screenBrightnessLevel, screenBrightnessLevelMax)
                setScreenBrightness(activity,  screenBrightnessLevel)
            }
        }
    }

    fun decreaseScreenBrghtness(activity: Activity, point: Point, width: Int) {
        if ( screenBrightness == PREF_BRIGHTNESS_MANUAL ) {
            if ( point.x < 32 || point.x > width-32) {
                screenBrightnessLevel -= screenBrightnessLevelStep
                screenBrightnessLevel = max(screenBrightnessLevel, screenBrightnessLevelMin)
                setScreenBrightness(activity,  screenBrightnessLevel)
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
        /*
         * Prefs
         */
        const val PREF_BOOK_PATH = "BookPath"
        const val PREF_BOOK_TEXT_SIZE = "TextSize"
        const val PREF_BOOK_FONT_NAME = "FontName"
        const val PREF_BOOK_FONT_PATH = "FontPath"
        const val PREF_SCREEN_BRIGHTNESS = "ScreenBrightness"

        const val PREF_KEY_THEME = "theme"
        const val PREF_KEY_BRIGHTNESS = "brightness"
        const val PREF_KEY_ORIENTATION = "orientation"
        const val PREF_KEY_USE_SYSTEM_FONTS = "useSystemFonts"
        const val PREF_KEY_SHOW_NOTO_FONTS = "showNotoFonts"

        const val PREF_ORIENTATION_DEFAULT = "PortraitSensor"
        const val PREF_BRIGHTNESS_DEFAULT = "Manual"
        const val PREF_BRIGHTNESS_MANUAL = "Manual"
        const val PREF_THEME_DEFAULT = "Manual"

    }
}