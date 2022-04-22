package com.kontranik.koreader

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.graphics.Point
import android.os.BatteryManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.kontranik.koreader.model.*
import com.kontranik.koreader.utils.PrefsHelper
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReaderActivityViewModel(val app: Application) : AndroidViewModel(app)  {
    var prefsHelper: PrefsHelper = PrefsHelper(app.applicationContext)

    var book: MutableLiveData<Book> = MutableLiveData()

    var pageViewContent: MutableLiveData<CharSequence?> = MutableLiveData()
    var pageViewSettings: MutableLiveData<PageViewSettings> = MutableLiveData(
        PageViewSettings(
            textSize = prefsHelper.textSize,
            lineSpacing = prefsHelper.lineSpacing,
            letterSpacing = prefsHelper.letterSpacing,
            typeFace = prefsHelper.font.getTypeface(),
            marginTop = prefsHelper.marginTop,
            marginBottom = prefsHelper.marginBottom,
            marginLeft = prefsHelper.marginLeft,
            marginRight = prefsHelper.marginRight
        )
    )
    var pageViewColorSettings: MutableLiveData<PageViewColorSettings> = MutableLiveData(
        PageViewColorSettings(
            showBackgroundImage = prefsHelper.showBackgroundImage,
            backgroundImageUri = prefsHelper.backgroundImageUri,
            backgroundImageTiledRepeat = prefsHelper.backgroundImageTiledRepeat,
            colorText = prefsHelper.colorText,
            colorBack = prefsHelper.colorBack,
            colorLink = prefsHelper.colorLinkText,
            colorInfoText = prefsHelper.colorInfoText,
        )
    )
    var infoTextLeft: MutableLiveData<CharSequence?> = MutableLiveData()
    var infoTextRight: MutableLiveData<CharSequence?> = MutableLiveData()
    var infoTextSystemstatus: MutableLiveData<CharSequence?> = MutableLiveData()

    var note: MutableLiveData<String> = MutableLiveData()

    private var width: Int = 100
    private var fullwidth: Int = 100
    private var height: Int = 100
    private var fullheight: Int = 100

    fun loadBook(textViewPageView: TextView) {
        book.value = Book(app, prefsHelper.bookPath!!, textViewPageView)
    }

    fun pageNext(): Boolean {
        if (book.value == null ) return false
        updateView(book.value?.getNext())
        return true
    }

    fun pagePrev(): Boolean {
        if (book.value == null) return false
        updateView(book.value!!.getPrev())
        return true
    }

    fun loadNote(url: String){
        note.value = book.value?.getNote(url)
    }

    fun getBookmarkForCurrentPosition(text: String): Bookmark {
        return Bookmark(
            path = prefsHelper.bookPath!!,
            text = text,
            position_section = book.value!!.curPage!!.startBookPosition.section,
            position_offset = book.value!!.curPage!!.startBookPosition.offSet,
        )
    }

    fun isBookmarkOnCurrentPage(bookmark: Bookmark): Boolean {
        if ( book.value?.curPage == null ) return false
        return (
                bookmark.position_section >= book.value!!.curPage!!.startBookPosition.section
                &&
                bookmark.position_section <= book.value!!.curPage!!.endBookPosition.section
                &&
                bookmark.position_offset >= book.value!!.curPage!!.startBookPosition.offSet
                &&
                bookmark.position_offset < book.value!!.curPage!!.endBookPosition.offSet
        )
    }

    fun goToBookmark(bookmark: Bookmark) {
        goToPage(Page(null, BookPosition(bookmark), BookPosition()))
    }

    fun goToSection(section: Int) {
        goToPage(
            Page(null, BookPosition(section = section), BookPosition())
        )
    }

    fun goToPage(page: Int) {
        goToPage(Page(
            null,
            book.value!!.ebookHelper!!.pageScheme.getBookPositionForPage(page),
            BookPosition()
        ))
    }

    fun goToPage(page: Page) {
        book.value?.curPage = page
        recalcCurrentPage()
    }

    fun reloadCurrentPage() {
        if (book.value != null)
            updateView(book.value?.getCur(recalc = false))
    }

    fun recalcCurrentPage() {
        if (book.value != null)
            updateView(book.value?.getCur(recalc = true))
    }

    private fun updateView(page: Page?) {
        if (page != null) {
            book.value?.curPage = Page(page)
            pageViewContent.value = page.content
        } else {
            pageViewContent.value = app.getString(R.string.no_page_content)
        }
        updateInfo()
    }

    private fun updateInfo() {
        if (book.value != null && book.value!!.curPage != null) {
            val curSection = book.value!!.getCurSection()

            val curTextPage = book.value!!.getCurTextPage()

            infoTextLeft.value =
                app.getString(
                    R.string.page_info_text_left,
                    curTextPage,
                    book.value!!.getPageScheme()!!.countTextPages,
                    if (book.value!!.getPageScheme()!!.countTextPages == 0) 0
                    else curTextPage * 100 / book.value!!.getPageScheme()!!.countTextPages
                )

            infoTextRight.value =
                app.getString(
                    R.string.page_info_text_right,
                    curSection,
                    book.value!!.getPageScheme()!!.sectionCount
                )
        } else {
            infoTextRight.value = app.getString(R.string.no_book)
        }
        updateSystemStatus()
    }

    fun updateSystemStatus() {
        val simpleDateFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val bm = app.getSystemService(AppCompatActivity.BATTERY_SERVICE) as BatteryManager
        val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val strTime: String = simpleDateFormatTime.format(Date().time)
        infoTextSystemstatus.value =
            app.resources.getString(R.string.page_info_text_time, strTime, batLevel)
    }

    fun goToPositionByBookStatus(bookStatus: BookStatus?) {
        if (book.value != null) {
            val startPosition: BookPosition = if (bookStatus == null) {
                BookPosition()
            } else {
                BookPosition(bookStatus.position_section, bookStatus.position_offset)
            }
            book.value!!.curPage = Page(null, startPosition, BookPosition())
            updateView(book.value!!.getCur(recalc = true))
        }
    }

    fun increaseScreenBrghtness(activity: Activity, point: Point) {
        prefsHelper.increaseScreenBrghtness(activity, point, fullwidth)
    }

    fun decreaseScreenBrghtness(activity: Activity, point: Point) {
        prefsHelper.decreaseScreenBrghtness(activity, point, fullwidth)
    }

    fun updateSizeInfo(fw: Int, fh: Int, w: Int, h: Int ) {
        if (w != width || h != height) {
            fullwidth = fw
            fullheight = fh
            width = w
            height = h
            recalcCurrentPage()
        }
    }

    fun getZone(point: Point): ScreenZone {
        return ScreenZone.zone(point, width, height)
    }

    fun finishQuickMenuSettings(
        textSize: Float,
        lineSpacing: Float,
        letterSpacing: Float,
        colorTheme: String
    ) {
        if (textSize != prefsHelper.textSize
            || lineSpacing != prefsHelper.lineSpacing
            || letterSpacing != prefsHelper.letterSpacing
            || colorTheme != prefsHelper.colorTheme
        ) {
            prefsHelper.textSize = textSize
            prefsHelper.lineSpacing = lineSpacing
            prefsHelper.letterSpacing = letterSpacing
            prefsHelper.colorTheme = colorTheme

            pageViewSettings.value = pageViewSettings.value?.also { it ->
                it.textSize = prefsHelper.textSize
                it.lineSpacing = prefsHelper.lineSpacing
                it.letterSpacing = prefsHelper.letterSpacing
            }

            loadColorThemeSettings()
        }
    }

    fun loadSettings(activity: Activity) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        prefsHelper.interfaceTheme = prefs.getString(PrefsHelper.PREF_KEY_THEME, "Auto")
        prefsHelper.screenOrientation =
            prefs.getString(PrefsHelper.PREF_KEY_ORIENTATION, "PortraitSensor")
        prefsHelper.screenBrightness = prefs.getString(PrefsHelper.PREF_KEY_BRIGHTNESS, "Manual")

        prefsHelper.textSize =
            prefs.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, prefsHelper.defaultTextSize)

        val lineSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, null)
        if (lineSpacingString != null) prefsHelper.lineSpacing = lineSpacingString.toFloat()
        else prefsHelper.lineSpacing = prefsHelper.defaultLineSpacing

        val letterSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, null)
        if (letterSpacingString != null) prefsHelper.letterSpacing = letterSpacingString.toFloat()
        else prefsHelper.letterSpacing = prefsHelper.defaultLetterSpacing

        if (prefs.contains(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL)) {
            val fontpath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL, null)
            if (fontpath != null) {
                val fontFile = File(fontpath)
                if (fontFile.isFile && fontFile.canRead()) {
                    prefsHelper.font = TypefaceRecord(name = fontFile.name, file = fontFile)
                }
            }
        } else if (prefs.contains(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL)) {
            prefsHelper.font = TypefaceRecord(
                name = prefs.getString(
                    PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL,
                    TypefaceRecord.SANSSERIF
                )!!
            )
        }
        var sMargin =
            prefs.getString(PrefsHelper.PREF_KEY_MERGE_TOP + prefsHelper.colorTheme, null)
        try {
            prefsHelper.marginTop =
                if (sMargin != null) Integer.parseInt(sMargin) else prefsHelper.marginDefault
        } catch (e: Exception) {
            prefsHelper.marginTop = prefsHelper.marginDefault
        }
        sMargin =
            prefs.getString(PrefsHelper.PREF_KEY_MERGE_BOTTOM + prefsHelper.colorTheme, null)
        try {
            prefsHelper.marginBottom =
                if (sMargin != null) Integer.parseInt(sMargin) else prefsHelper.marginDefault
        } catch (e: Exception) {
            prefsHelper.marginBottom = prefsHelper.marginDefault
        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_LEFT + prefsHelper.colorTheme, null)
        try {
            prefsHelper.marginLeft =
                if (sMargin != null) Integer.parseInt(sMargin) else prefsHelper.marginDefault
        } catch (e: Exception) {
            prefsHelper.marginLeft = prefsHelper.marginDefault
        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_RIGHT + prefsHelper.colorTheme, null)
        try {
            prefsHelper.marginRight =
                if (sMargin != null) Integer.parseInt(sMargin) else prefsHelper.marginDefault
        } catch (e: Exception) {
            prefsHelper.marginRight = prefsHelper.marginDefault
        }

        pageViewSettings.value = pageViewSettings.value?.also { it ->
            it.textSize = prefsHelper.textSize
            it.lineSpacing = prefsHelper.lineSpacing
            it.letterSpacing = prefsHelper.letterSpacing
            it.typeFace = prefsHelper.font.getTypeface()
            it.marginTop = prefsHelper.marginTop
            it.marginBottom = prefsHelper.marginBottom
            it.marginLeft = prefsHelper.marginLeft
            it.marginRight = prefsHelper.marginRight
        }

        prefsHelper.colorTheme = prefs.getString(
            PrefsHelper.PREF_KEY_COLOR_SELECTED_THEME,
            PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT
        )
            ?: PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT
        loadColorThemeSettings()

        prefsHelper.tapDoubleAction = hashMapOf(
            ScreenZone.TopLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_TOP_LEFT,
                prefsHelper.tapZoneDoubleTopLeft
            ),
            ScreenZone.TopCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_TOP_CENTER,
                prefsHelper.tapZoneDoubleTopCenter
            ),
            ScreenZone.TopRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_TOP_RIGHT,
                prefsHelper.tapZoneDoubleTopRight
            ),
            ScreenZone.MiddleLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_MIDDLE_LEFT,
                prefsHelper.tapZoneDoubleMiddleLeft
            ),
            ScreenZone.MiddleCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_MIDDLE_CENTER,
                prefsHelper.tapZoneDoubleMiddleCenter
            ),
            ScreenZone.MiddleRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_MIDDLE_RIGHT,
                prefsHelper.tapZoneDoubleMiddleRight
            ),
            ScreenZone.BottomLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_BOTTOM_LEFT,
                prefsHelper.tapZoneDoubleBottomLeft
            ),
            ScreenZone.BottomCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_BOTTOM_CENTER,
                prefsHelper.tapZoneDoubleBottomCenter
            ),
            ScreenZone.BottomRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_DOUBLE_BOTTOM_RIGHT,
                prefsHelper.tapZoneDoubleBottomRight
            ),
        )

        prefsHelper.tapOneAction = hashMapOf(
            ScreenZone.TopLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_TOP_LEFT,
                prefsHelper.tapZoneOneTopLeft
            ),
            ScreenZone.TopCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_TOP_CENTER,
                prefsHelper.tapZoneOneTopCenter
            ),
            ScreenZone.TopRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_TOP_RIGHT,
                prefsHelper.tapZoneOneTopRight
            ),
            ScreenZone.MiddleLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_MIDDLE_LEFT,
                prefsHelper.tapZoneOneMiddleLeft
            ),
            ScreenZone.MiddleCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_MIDDLE_CENTER,
                prefsHelper.tapZoneOneMiddleCenter
            ),
            ScreenZone.MiddleRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_MIDDLE_RIGHT,
                prefsHelper.tapZoneOneMiddleRight
            ),
            ScreenZone.BottomLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_BOTTOM_LEFT,
                prefsHelper.tapZoneOneBottomLeft
            ),
            ScreenZone.BottomCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_BOTTOM_CENTER,
                prefsHelper.tapZoneOneBottomCenter
            ),
            ScreenZone.BottomRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_ONE_BOTTOM_RIGHT,
                prefsHelper.tapZoneOneBottomRight
            ),
        )
        prefsHelper.tapLongAction = hashMapOf(
            ScreenZone.TopLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_TOP_LEFT,
                prefsHelper.tapZoneLongTopLeft
            ),
            ScreenZone.TopCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_TOP_CENTER,
                prefsHelper.tapZoneLongTopCenter
            ),
            ScreenZone.TopRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_TOP_RIGHT,
                prefsHelper.tapZoneLongTopRight
            ),
            ScreenZone.MiddleLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_MIDDLE_LEFT,
                prefsHelper.tapZoneLongMiddleLeft
            ),
            ScreenZone.MiddleCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_MIDDLE_CENTER,
                prefsHelper.tapZoneLongMiddleCenter
            ),
            ScreenZone.MiddleRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_MIDDLE_RIGHT,
                prefsHelper.tapZoneLongMiddleRight
            ),
            ScreenZone.BottomLeft to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_BOTTOM_LEFT,
                prefsHelper.tapZoneLongBottomLeft
            ),
            ScreenZone.BottomCenter to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_BOTTOM_CENTER,
                prefsHelper.tapZoneLongBottomCenter
            ),
            ScreenZone.BottomRight to prefs.getString(
                PrefsHelper.PREF_KEY_TAP_LONG_BOTTOM_RIGHT,
                prefsHelper.tapZoneLongBottomRight
            ),
        )

        prefsHelper.setOrientation(activity)
        prefsHelper.setThemeDefault()
    }

    private fun loadColorThemeSettings() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_BACK + prefsHelper.colorTheme, 0)
        prefsHelper.colorBack =
            if (co != 0) "#" + Integer.toHexString(co)
            else app.resources.getString(PrefsHelper.colorBackgroundDefaultArray[prefsHelper.colorTheme.toInt()-1])

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + prefsHelper.colorTheme, 0)
        prefsHelper.colorText =
            if (co != 0) "#" + Integer.toHexString(co)
            else app.resources.getString(PrefsHelper.colorForegroundDefaultArray[prefsHelper.colorTheme.toInt()-1])

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_LINKTEXT + prefsHelper.colorTheme, 0)
        prefsHelper.colorLinkText =
            if (co != 0) "#" + Integer.toHexString(co)
            else app.resources.getString(PrefsHelper.colorLinkDefaultArray[prefsHelper.colorTheme.toInt()-1])

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_INFOTEXT + prefsHelper.colorTheme, 0)
        prefsHelper.colorInfoText =
            if (co != 0) "#" + Integer.toHexString(co)
            else app.resources.getString(PrefsHelper.colorInfotextDefaultArray[prefsHelper.colorTheme.toInt()-1])

        prefsHelper.showBackgroundImage = prefs.getBoolean(
            PrefsHelper.PREF_KEY_SHOW_BACKGROUND_IMAGE + prefsHelper.colorTheme,
            false
        )
        prefsHelper.backgroundImageUri = prefs.getString(
            PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_URI + prefsHelper.colorTheme,
            null
        )
        prefsHelper.backgroundImageTiledRepeat = prefs.getBoolean(
            PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT + prefsHelper.colorTheme,
            false
        )

        pageViewColorSettings.value = PageViewColorSettings(
            showBackgroundImage = prefsHelper.showBackgroundImage,
            backgroundImageUri = prefsHelper.backgroundImageUri,
            backgroundImageTiledRepeat = prefsHelper.backgroundImageTiledRepeat,
            colorText = prefsHelper.colorText,
            colorBack = prefsHelper.colorBack,
            colorLink = prefsHelper.colorLinkText,
            colorInfoText = prefsHelper.colorInfoText,
        )
    }

    fun loadPrefs(activity: Activity) {
        val settings = app.getSharedPreferences(
            ReaderActivity.PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )

        if (settings.contains(PrefsHelper.PREF_BOOK_PATH)) {
            prefsHelper.bookPath = settings!!.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        if (settings.contains(PrefsHelper.PREF_SCREEN_BRIGHTNESS)) {
            prefsHelper.screenBrightnessLevel = settings!!.getFloat(
                PrefsHelper.PREF_SCREEN_BRIGHTNESS,
                prefsHelper.systemScreenBrightnessLevel
            )
            prefsHelper.setScreenBrightness(activity, prefsHelper.screenBrightnessLevel)
        }
    }

    fun savePrefs() {
        val settings = app.getSharedPreferences(
            ReaderActivity.PREFS_FILE,
            AppCompatActivity.MODE_PRIVATE
        )
        val prefEditor = settings.edit()
        prefEditor.putString(PrefsHelper.PREF_BOOK_PATH, prefsHelper.bookPath)
        prefEditor.putFloat(PrefsHelper.PREF_SCREEN_BRIGHTNESS, prefsHelper.screenBrightnessLevel)
        prefEditor.apply()
    }

    fun resetQuickSettings() {
        pageViewSettings.value = pageViewSettings.value?.also {
            it.textSize = prefsHelper.textSize
            it.lineSpacing = prefsHelper.lineSpacing
            it.letterSpacing = prefsHelper.letterSpacing
        }

        pageViewColorSettings.value = PageViewColorSettings(
            showBackgroundImage = prefsHelper.showBackgroundImage,
            backgroundImageUri = prefsHelper.backgroundImageUri,
            backgroundImageTiledRepeat = prefsHelper.backgroundImageTiledRepeat,
            colorText = prefsHelper.colorText,
            colorBack = prefsHelper.colorBack,
            colorLink = prefsHelper.colorLinkText,
            colorInfoText = prefsHelper.colorInfoText,
        )
    }


}