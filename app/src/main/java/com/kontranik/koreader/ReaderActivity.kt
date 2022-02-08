package com.kontranik.koreader

import android.annotation.SuppressLint
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.BookmarksViewModel
import com.kontranik.koreader.databinding.ActivityReaderMainBinding
import com.kontranik.koreader.model.*
import com.kontranik.koreader.reader.*
import com.kontranik.koreader.utils.*
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class ReaderActivity :
    AppCompatActivity(),
    QuickMenuFragment.QuickMenuDialogListener,
    BookmarkListFragment.BookmarkListDialogListener,
    GotoMenuFragment.GotoMenuDialogListener {

    private lateinit var binding: ActivityReaderMainBinding

    private lateinit var prefsHelper: PrefsHelper

    private var book: Book? = null

    private val simpleDateFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val mTimeInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent) {
            updateSystemStatus()
        }
    }

    private var width: Int = 100
    private var fullwidth: Int = 100
    private var height: Int = 100
    private var fullheight: Int = 100

    private lateinit var mBookStatusViewModel: BookStatusViewModel
    private lateinit var mBookmarksViewModel: BookmarksViewModel

    private var backButtonPressedTime = Date().time


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefsHelper = PrefsHelper(this)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = ActivityReaderMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mBookStatusViewModel = ViewModelProvider(this).get(BookStatusViewModel::class.java)
        mBookmarksViewModel = ViewModelProvider(this).get(BookmarksViewModel::class.java)

        mBookStatusViewModel.cleanup(this)

        // nachputzen falsche prefs
        // linespace und LetterSpace  pref muss string sein - float ist falsch
        val mySPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = mySPrefs.edit()
        try {
            if (mySPrefs.getFloat(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, -1f) != -1f) {
                editor.remove(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING)
                editor.apply()
            }
        } catch (e: ClassCastException) {
            // its ok
        }
        try {
            if (mySPrefs.getFloat(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, -1f) != -1f) {
                editor.remove(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING)
                editor.apply()
            }
        } catch (e: ClassCastException) {
            // its ok
        }

        TextViewInitiator.initiateTextView(binding.textViewPageview, "")

        loadSettings()
        loadPrefs()

        registerReceiver(mTimeInfoReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        setColorTheme()
        setOnClickListener()

        binding.textViewPageview.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Log.d(TAG, "pageView layout changed")
            updateSizeInfo()
        }

        mBookStatusViewModel.savedBookStatus.observe(this, androidx.lifecycle.Observer {
            if (book != null) {
                val startPosition: BookPosition = if (it == null) {
                    BookPosition()
                } else {
                    BookPosition(it.position_section, it.position_offset)
                }
                book!!.curPage = Page(null, startPosition, BookPosition())
                Log.d(TAG, "onWindowFocusChanged: getCurPage")
                updateView(book!!.getCur(recalc = true))
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //Here you can get the size of pageView!
        Log.d(TAG, "onWindowFocusChanged")
        //val ph = PermissionsHelper(this)
        //ph.checkPermissionsExternalStorage(binding.textViewPageview!!)
        setColorTheme()
        if (prefsHelper.bookPath != null) {
            runOnUiThread {
                try {
                    if (book == null || book!!.fileLocation != prefsHelper.bookPath) {
                        loadBook()
                        loadPositionForBook()
                    }
                } catch (e: Exception) {
                    Log.e("tag", e.stackTraceToString())
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.open_book),
                Toast.LENGTH_LONG
            ).show()
            openMainMenu()
        }
    }

    override fun onBackPressed() {
        if (Date().time - backButtonPressedTime < 2000) {
            finish()
        } else {
            backButtonPressedTime = Date().time
            Toast.makeText(
                this,
                resources.getString(R.string.press_again_to_exit),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadBook() {
        if (!FileHelper.contentFileExist(applicationContext, prefsHelper.bookPath)) {
            Toast.makeText(
                this,
                resources.getString(R.string.can_not_load_book, prefsHelper.bookPath),
                Toast.LENGTH_LONG
            ).show()
            openMainMenu()
        }
        Toast.makeText(this, resources.getString(R.string.loading_book), Toast.LENGTH_SHORT).show()
        book = Book(applicationContext, prefsHelper.bookPath!!, binding.textViewPageview)
        if (book != null) {
            mBookStatusViewModel.updateLastOpenTime(book!!)
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.can_not_load_book, prefsHelper.bookPath),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ACCESS_MAIN_MENU) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(PREF_TYPE)) {
                    when (data.getIntExtra(PREF_TYPE, 0)) {
                        PREF_TYPE_OPEN_BOOK -> openBookFromIntent(data)
                        PREF_TYPE_SETTINGS -> {
                            loadSettings()
                        }
                    }
                }
            } else {
                Log.e(TAG, "onActivityResult: access error")
            }
        } else if (requestCode == REQUEST_ACCESS_QUICK_MENU) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    var changed = false
                    if (data.hasExtra(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE)) {
                        prefsHelper.textSize = data.getFloatExtra(
                            PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE,
                            prefsHelper.defaultTextSize
                        )
                        binding.textViewPageview.textSize = prefsHelper.textSize
                        changed = true
                    }
                    if (data.hasExtra(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING)) {
                        prefsHelper.lineSpacing = data.getFloatExtra(
                            PrefsHelper.PREF_KEY_BOOK_LINE_SPACING,
                            prefsHelper.defaultLineSpacing
                        )
                        binding.textViewPageview.setLineSpacing(
                            binding.textViewPageview.lineSpacingMultiplier,
                            prefsHelper.lineSpacing
                        )
                        changed = true
                    }
                    if (changed) {
                        //book!!.loadPage(binding.textViewPageview!!)
                        //updateView( )
                        Log.d(TAG, "onActivityResult: getCurPage")
                        updateView(book!!.getCur(recalc = true))
                    }
                }
            } else {
                Log.e(TAG, "onActivityResult: access error")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openBookFromIntent(data: Intent) {
        Log.d(TAG, "openBookFromIntent")
        if (data.hasExtra(PrefsHelper.PREF_BOOK_PATH)) {
            prefsHelper.bookPath = data.getStringExtra(PrefsHelper.PREF_BOOK_PATH)
            savePrefs()
            startProgress()
            loadBook()
            loadPositionForBook()
            Log.d(TAG, "openBookFromIntent: getCurPage")
            updateView(book!!.getCur(recalc = true))
        }
    }

    private fun loadPositionForBook() {
        if (book == null) return
        if ( prefsHelper.bookPath != null) mBookStatusViewModel.loadBookStatus(prefsHelper.bookPath!!)
    }

    private fun savePositionForBook() {
        if (prefsHelper.bookPath != null && book != null && book!!.curPage != null) {
            mBookStatusViewModel.savePosition(book!!)
        }
    }

    override fun onStop() {
        super.onStop()
        savePrefs()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTimeInfoReceiver)
    }

    private fun loadPrefs() {
        val settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)

        if (settings.contains(PrefsHelper.PREF_BOOK_PATH)) {
            prefsHelper.bookPath = settings!!.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        if (settings.contains(PrefsHelper.PREF_SCREEN_BRIGHTNESS)) {
            prefsHelper.screenBrightnessLevel = settings!!.getFloat(
                PrefsHelper.PREF_SCREEN_BRIGHTNESS,
                prefsHelper.systemScreenBrightnessLevel
            )
            prefsHelper.setScreenBrightness(this, prefsHelper.screenBrightnessLevel)
        }
    }

    private fun savePrefs() {
        val settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
        val prefEditor = settings.edit()
        prefEditor.putString(PrefsHelper.PREF_BOOK_PATH, prefsHelper.bookPath)
        prefEditor.putFloat(PrefsHelper.PREF_SCREEN_BRIGHTNESS, prefsHelper.screenBrightnessLevel)
        prefEditor.apply()
    }

    private fun loadSettings() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
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

        binding.textViewPageview.textSize = prefsHelper.textSize
        binding.textViewPageview.typeface = prefsHelper.font.getTypeface()

        binding.textViewPageview.setLineSpacing(
            binding.textViewPageview.lineSpacingMultiplier,
            prefsHelper.lineSpacing
        )
        binding.textViewPageview.letterSpacing = prefsHelper.letterSpacing

        if (book != null) updateView(book!!.getCur(recalc = true))

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

        prefsHelper.setOrientation(this)
        prefsHelper.setThemeDefault()

    }

    private fun loadColorThemeSettings() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_BACK + prefsHelper.colorTheme, 0)
        prefsHelper.colorBack =
            if (co != 0) "#" + Integer.toHexString(co)
            else resources.getString(PrefsHelper.colorBackgroundDefaultArray[prefsHelper.colorTheme.toInt()-1])

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + prefsHelper.colorTheme, 0)
        prefsHelper.colorText =
            if (co != 0) "#" + Integer.toHexString(co)
            else resources.getString(PrefsHelper.colorForegroundDefaultArray[prefsHelper.colorTheme.toInt()-1])

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_LINKTEXT + prefsHelper.colorTheme, 0)
        prefsHelper.colorLinkText =
            if (co != 0) "#" + Integer.toHexString(co) else prefsHelper.colorLinkTextDefault

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_INFOTEXT + prefsHelper.colorTheme, 0)
        prefsHelper.colorInfoText =
            if (co != 0) "#" + Integer.toHexString(co) else prefsHelper.colorTextDefault

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
    }

    private fun checkTap(point: Point, tap: Int) {

        val zone = ScreenZone.zone(point, width, height)
        // textViewInfoRight!!.text = resources.getString(R.string.click_in_zone, tap, zone)
        Log.d(TAG, "Tap $tap in Zone $zone")
        when (tap) {
            1 -> { // double tap
                doTapAction(prefsHelper.tapOneAction[zone])
            }
            2 -> { // double tap
                doTapAction(prefsHelper.tapDoubleAction[zone])
            }
            3 -> { // long tap
                doTapAction(prefsHelper.tapLongAction[zone])
            }
        }
    }

    private fun doTapAction(tapAction: String?) {
        Log.d(TAG, "doTapAction: $tapAction")
        if (tapAction == null) return
        when (tapAction) {
            "PagePrev" -> {
                doPagePrev()
            }
            "PageNext" -> {
                doPageNext()
            }
            "QuickMenu" -> {
                openQuickMenu()
            }
            "MainMenu" -> {
                openMainMenu()
            }
            "GoTo" -> {
                openGotoMenu()
            }
            "Bookmarks" -> {
                onShowBookmarklist()
            }
            "None" -> {
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {

        binding.llInfobereich.setOnTouchListener(object :
            OnSwipeTouchListener(this@ReaderActivity) {
            override fun onClick(point: Point) {
                super.onClick(point)
                openGotoMenu()
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)
                openGotoMenu()
            }
        })

        binding.textViewPageview.setOnTouchListener(object :
            OnSwipeTouchListener(this@ReaderActivity) {
            override fun onClick(point: Point) {
                super.onClick(point)

                // Find the URL that was pressed
                val off = getClickedOffset(point)
                val spannable = binding.textViewPageview.text as Spannable
                val link = spannable.getSpans(off, off, URLSpan::class.java)
                if (link.isNotEmpty()) {
                    // link clicked
                    val url = link[0].url
                    //Toast.makeText(applicationContext, url, Toast.LENGTH_SHORT).show()
                    showNote(book!!.getNote(url))
                } else {
                    // not a link, normal click
                    checkTap(point, 1)
                }
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                doPageNext()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                doPagePrev()
            }

            override fun onSlideUp(point: Point) {
                super.onSlideUp(point)
                prefsHelper.increaseScreenBrghtness(this@ReaderActivity, point, fullwidth)
            }

            override fun onSlideDown(point: Point) {
                super.onSlideDown(point)
                prefsHelper.decreaseScreenBrghtness(this@ReaderActivity, point, fullwidth)
            }

            override fun onDoubleClick(point: Point) {
                //super.onDoubleClick(point)
                checkTap(point, 2)
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)

                // Find the Image that was pressed
                val off = getClickedOffset(point)
                val spannable = binding.textViewPageview.text as Spannable
                val image = spannable.getSpans(off, off, ImageSpan::class.java)
                if (image.isNotEmpty()) {
                    showImage(image[0])
                } else {
                    checkTap(point, 3)
                }
            }

        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                doPageNext()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                doPagePrev()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun updateSizeInfo() {
        val fw = binding.textViewPageview.measuredWidth
        val fh = binding.textViewPageview.measuredHeight
        val w =
            binding.textViewPageview.measuredWidth - binding.textViewPageview.paddingLeft - binding.textViewPageview.paddingRight
        val h =
            binding.textViewPageview.measuredHeight - binding.textViewPageview.paddingTop - binding.textViewPageview.paddingBottom

        // Toast.makeText(this, "$w x $h", Toast.LENGTH_SHORT).show()

        if (w != width || h != height) {
            fullwidth = fw
            fullheight = fh
            width = w
            height = h
            if (book != null) {
                Log.d(TAG, "updateSizeInfo: getCurPage")
                updateView(book!!.getCur(recalc = true))
            }
        }
    }

    private fun doPageNext() {
        if (book == null) return
        else {
            if (book!!.isLastSection() && book!!.isLastPage()) return
            updateView(book!!.getNext())
            savePositionForBook()
        }
    }


    private fun doPagePrev() {
        if (book == null) return
        if (book!!.isFirstSection() && book!!.isFirstPage()) return
        updateView(book!!.getPrev())
        savePositionForBook()
    }

    private fun updateInfo() {
        if (book != null && book!!.curPage != null) {
            val curSection = book!!.getCurSection()

            val curTextPage = book!!.getCurTextPage()

            binding.tvInfotextLeft.text =
                resources.getString(
                    R.string.page_info_text_left,
                    curTextPage,
                    book!!.getPageScheme()!!.countTextPages,
                    if (book!!.getPageScheme()!!.countTextPages == 0) 0 else curTextPage * 100 / book!!.getPageScheme()!!.countTextPages
                )

            binding.tvInfotextRight.text =
                resources.getString(
                    R.string.page_info_text_right,
                    curSection,
                    book!!.getPageScheme()!!.sectionCount
                )
        } else {
            binding.tvInfotextRight.text = getString(R.string.no_book)
        }
        updateSystemStatus()
    }

    private fun updateSystemStatus() {
        val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
        val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val strTime: String = simpleDateFormatTime.format(Date().time)
        binding.tvInfotextSystemstatus.text =
            resources.getString(R.string.page_info_text_time, strTime, batLevel)

    }

    private fun updateView(page: Page?) {
        if (page != null) {
            book!!.curPage = Page(page)
            binding.textViewPageview.text = page.content
        } else {
            binding.textViewPageview.text = resources.getString(R.string.no_page_content)
        }

        binding.textViewPageview.fontFeatureSettings

        updateInfo()
    }

    private fun getClickedOffset(point: Point): Int {
        // check if Link or image clicked
        var x = point.x
        var y = point.y
        x -= binding.textViewPageview.totalPaddingLeft
        y -= binding.textViewPageview.totalPaddingTop
        x += binding.textViewPageview.scrollX
        y += binding.textViewPageview.scrollY
        // Locate the clicked span
        val layout = binding.textViewPageview.layout
        val line = layout.getLineForVertical(y)
        return layout.getOffsetForHorizontal(line, x.toFloat())
    }

    private fun showNote(html: String?) {
        if (html == null) return
        val floatTextViewFragment: FloatTextViewFragment =
            FloatTextViewFragment.newInstance(html, prefsHelper.textSize, prefsHelper.font)
        floatTextViewFragment.show(supportFragmentManager, "fragment_floattextview")
    }

    private fun showImage(imageSpan: ImageSpan) {
        val b: ByteArray = if (imageSpan.source != null) {
            book!!.getImageByteArray(imageSpan.source!!) ?: return
        } else {
            val bitmap = ImageUtils.drawableToBitmap(imageSpan.drawable)
            val byteArrayStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayStream)
            byteArrayStream.toByteArray()
        }
        val imageViewerFragment: ImageViewerFragment =
            ImageViewerFragment.newInstance(b)
        imageViewerFragment.show(supportFragmentManager, "fragment_imageviewfragment")
    }

    private fun openMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivityForResult(intent, REQUEST_ACCESS_MAIN_MENU)
    }

    private fun openQuickMenu() {
        Log.d(TAG, "openQuickMenu")
        val quickMenuFragment = QuickMenuFragment()
        quickMenuFragment.show(supportFragmentManager, "fragment_quick_menu")
    }

    override fun onFinishQuickMenuDialog(
        textSize: Float,
        lineSpacing: Float,
        letterSpacing: Float,
        colorTheme: String
    ) {
        Log.d(TAG, "onFinishQuickMenuDialog. TextSize: $textSize, lineSpacing: $lineSpacing, letterSpacing: $letterSpacing, colorTheme: $colorTheme")

        if (textSize != prefsHelper.textSize
            || lineSpacing != prefsHelper.lineSpacing
            || letterSpacing != prefsHelper.letterSpacing
            || colorTheme != prefsHelper.colorTheme
        ) {
            prefsHelper.textSize = textSize
            prefsHelper.lineSpacing = lineSpacing
            prefsHelper.letterSpacing = letterSpacing
            prefsHelper.colorTheme = colorTheme
            binding.textViewPageview.textSize = textSize
            binding.textViewPageview.setLineSpacing(
                binding.textViewPageview.lineSpacingExtra,
                lineSpacing
            )

            loadColorThemeSettings()
            setColorTheme()
        }
    }

    override fun onChangeColorTheme(colorTheme: String, colorThemeIndex: Int) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_BACK + colorTheme, 0)
        val colorBack =
            if (co != 0) "#" + Integer.toHexString(co)
            else resources.getString(
                PrefsHelper.colorBackgroundDefaultArray[colorThemeIndex]
            )

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + colorTheme, 0)
        val colorText =
            if (co != 0) "#" + Integer.toHexString(co)
            else resources.getString(
                PrefsHelper.colorForegroundDefaultArray[colorThemeIndex]
            )

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_LINKTEXT + colorTheme, 0)
        val colorLinkText =
            if (co != 0) "#" + Integer.toHexString(co)
            else prefsHelper.colorLinkTextDefault

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_INFOTEXT + colorTheme, 0)
        val colorInfoText =
            if (co != 0) "#" + Integer.toHexString(co)
            else prefsHelper.colorTextDefault

        val showBackgroundImage =
            prefs.getBoolean(PrefsHelper.PREF_KEY_SHOW_BACKGROUND_IMAGE + colorTheme, false)
        val backgroundImageUri =
            prefs.getString(PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_URI + colorTheme, null)
        val backgroundImageTiledRepeat =
            prefs.getBoolean(PrefsHelper.PREF_KEY_BACKGROUND_IMAGE_TILED_REPEAT + colorTheme, false)

        if (showBackgroundImage
            && backgroundImageUri != null
            && backgroundImageUri != "") {
            binding.textViewHolder.setBackgroundColor(Color.TRANSPARENT)
            try {
                val uri = Uri.parse(backgroundImageUri)
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap = BitmapDrawable(resources, inputStream)
                if (backgroundImageTiledRepeat)
                    bitmap.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                binding.textViewHolder.background = bitmap
                // val backgroundDrawable = Drawable.createFromStream(inputStream, uri.toString())
                // textViewHolder!!.background = backgroundDrawable
            } catch (e: Exception) {
                binding.textViewHolder.background = null
                binding.textViewHolder.setBackgroundColor(Color.parseColor(colorBack))
            }
        } else {
            binding.textViewHolder.background = null
            binding.textViewHolder.setBackgroundColor(Color.parseColor(colorBack))
        }

        binding.textViewPageview.setTextColor(Color.parseColor(colorText))
        binding.textViewPageview.setLinkTextColor(Color.parseColor(colorLinkText))
        binding.tvInfotextLeft.setTextColor(Color.parseColor(colorInfoText))
        binding.tvInfotextRight
            .setTextColor(Color.parseColor(colorInfoText))

        var marginTop = prefsHelper.marginDefault
        var marginBottom = prefsHelper.marginDefault
        var marginLeft = prefsHelper.marginDefault
        var marginRight = prefsHelper.marginDefault

        var sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_TOP + colorTheme, null)
        try {
            marginTop =
                if (sMargin != null) Integer.parseInt(sMargin)
                else prefsHelper.marginDefault
        } catch (e: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_BOTTOM + colorTheme, null)
        try {
            marginBottom =
                if (sMargin != null) Integer.parseInt(sMargin)
                else prefsHelper.marginDefault
        } catch (e: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_LEFT + colorTheme, null)
        try {
            marginLeft =
                if (sMargin != null) Integer.parseInt(sMargin)
                else prefsHelper.marginDefault
        } catch (e: Exception) {

        }
        sMargin = prefs.getString(PrefsHelper.PREF_KEY_MERGE_RIGHT + colorTheme, null)
        try {
            marginRight =
                if (sMargin != null) Integer.parseInt(sMargin)
                else prefsHelper.marginDefault
        } catch (e: Exception) {

        }

        val density = this.resources.displayMetrics.density
        val marginTopPixel = (marginTop * density).toInt()
        val marginBottomPixel = (marginBottom * density).toInt()
        val marginLeftPixel = (marginLeft * density).toInt()
        val marginRightPixel = (marginRight * density).toInt()
        binding.textViewPageview.setPadding(
            marginLeftPixel,
            marginTopPixel,
            marginRightPixel,
            marginBottomPixel
        )

    }

    override fun onChangeTextSize(textSize: Float) {
        binding.textViewPageview.textSize = textSize
        updateView(book!!.getCur(recalc = false))
    }

    override fun onChangeLineSpacing(lineSpacing: Float) {
        if (binding.textViewPageview.lineSpacingMultiplier == lineSpacing) return
        binding.textViewPageview.setLineSpacing(
            binding.textViewPageview.lineSpacingExtra,
            lineSpacing
        )
        Log.d(TAG, "onChangeLineSpacing: getCurPage")
        updateView(book!!.getCur(recalc = false))
    }

    override fun onChangeLetterSpacing(letterSpacing: Float) {
        if (binding.textViewPageview.letterSpacing == letterSpacing) return
        binding.textViewPageview.letterSpacing = letterSpacing
        Log.d(TAG, "onChangeLetterSpacing: getCurPage")
        updateView(book!!.getCur(recalc = false))
    }

    override fun onCancelQuickMenu() {
        binding.textViewPageview.textSize = prefsHelper.textSize
        binding.textViewPageview.setLineSpacing(
            binding.textViewPageview.lineSpacingExtra,
            prefsHelper.lineSpacing
        )
        binding.textViewPageview.letterSpacing = prefsHelper.letterSpacing
        setColorTheme()
    }

    override fun onAddBookmark() {
        val bookmark = Bookmark(
            path = prefsHelper.bookPath!!,
            text = binding.textViewPageview.text.toString().substring(0, 100),
            position_section = book!!.curPage!!.startBookPosition.section,
            position_offset = book!!.curPage!!.startBookPosition.offSet,
        )
        mBookmarksViewModel.insert(bookmark)
    }

    override fun onShowBookmarklist() {
        if (prefsHelper.bookPath == null) return
        val bookmarkListFragment: BookmarkListFragment =
            BookmarkListFragment.newInstance(prefsHelper.bookPath!!)
        bookmarkListFragment.show(supportFragmentManager, "fragment_bookmark_list")
    }

    override fun onSelectBookmark(bookmark: Bookmark) {
        if (
            bookmark.position_section >= book!!.curPage!!.startBookPosition.section
            &&
            bookmark.position_section <= book!!.curPage!!.endBookPosition.section
            &&
            bookmark.position_offset >= book!!.curPage!!.startBookPosition.offSet
            &&
            bookmark.position_offset < book!!.curPage!!.endBookPosition.offSet
        ) return
        // go to selected bookmark
        book!!.curPage = Page(null, BookPosition(bookmark), BookPosition())
        Log.d(TAG, "onSelectBookmark: getCurPage")
        updateView(book!!.getCur(recalc = true))
        savePositionForBook()
    }

    private fun openGotoMenu() {
        if (book == null || book!!.curPage == null) return
        val gotoMenuFragment: GotoMenuFragment =
            GotoMenuFragment
                .newInstance(
                    book!!.curPage!!.endBookPosition.section,
                    book!!.getCurTextPage(),
                    book!!.getPageScheme()!!.countTextPages,
                    book!!.getPageScheme()!!.sections.toTypedArray()
                )
        gotoMenuFragment.show(supportFragmentManager, "fragment_goto_menu")
    }

    override fun onFinishGotoMenuDialogPage(page: Int) {
        book!!.curPage =
            Page(null, book!!.ebookHelper!!.pageScheme.getBookPositionForPage(page), BookPosition())
        Log.d(TAG, "onFinishGotoMenuDialog: getCurPage")
        updateView(book!!.getCur(recalc = true))
        savePositionForBook()
    }

    override fun onFinishGotoMenuDialogSection(section: Int) {
        book!!.curPage = Page(null, BookPosition(section = section), BookPosition())
        Log.d(TAG, "onFinishGotoMenuDialog: getCurPage")
        updateView(book!!.getCur(recalc = true))
        savePositionForBook()
    }

    private fun startProgress() {
        binding.textViewPageview.text = resources.getString(R.string.loading_book)
    }

    private fun setColorTheme() {
        if (prefsHelper.showBackgroundImage && prefsHelper.backgroundImageUri != null && !prefsHelper.backgroundImageUri.equals(
                ""
            )
        ) {
            binding.textViewHolder.setBackgroundColor(Color.TRANSPARENT)
            try {
                val uri = Uri.parse(prefsHelper.backgroundImageUri)
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap = BitmapDrawable(resources, inputStream)
                if (prefsHelper.backgroundImageTiledRepeat)
                    bitmap.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                binding.textViewHolder.background = bitmap
                // val backgroundDrawable = Drawable.createFromStream(inputStream, uri.toString())
                // binding.textViewHolder!!.background = backgroundDrawable
            } catch (e: Exception) {
                binding.textViewHolder.background = null
                binding.textViewHolder.setBackgroundColor(Color.parseColor(prefsHelper.colorBack))
            }
        } else {
            binding.textViewHolder.background = null
            binding.textViewHolder.setBackgroundColor(Color.parseColor(prefsHelper.colorBack))
        }

        binding.textViewPageview.setTextColor(Color.parseColor(prefsHelper.colorText))
        binding.textViewPageview.setLinkTextColor(Color.parseColor(prefsHelper.colorLinkText))
        binding.tvInfotextLeft.setTextColor(Color.parseColor(prefsHelper.colorInfoText))
        binding.tvInfotextRight.setTextColor(Color.parseColor(prefsHelper.colorInfoText))
        binding.tvInfotextSystemstatus.setTextColor(Color.parseColor(prefsHelper.colorInfoText))

        val density = this.resources.displayMetrics.density
        val marginTopPixel = (prefsHelper.marginTop * density).toInt()
        val marginBottomPixel = (prefsHelper.marginBottom * density).toInt()
        val marginLeftPixel = (prefsHelper.marginLeft * density).toInt()
        val marginRightPixel = (prefsHelper.marginRight * density).toInt()
        binding.textViewPageview.setPadding(
            marginLeftPixel,
            marginTopPixel,
            marginRightPixel,
            marginBottomPixel
        )

        try {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

            var pIsDark = false
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    //window.statusBarColor = Color.parseColor(prefsHelper!!.colorDarkBack)
                    pIsDark = true
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    //window.statusBarColor = Color.parseColor(prefsHelper!!.colorLightBack)
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    //window.statusBarColor = Color.parseColor(prefsHelper!!.colorDarkBack)
                    pIsDark = true
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val lFlags = window.decorView.systemUiVisibility
                window.decorView.systemUiVisibility =
                    if (pIsDark) (lFlags and (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR).inv()) else
                        (lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "ReaderActivity"
        internal const val PREFS_FILE = "MainActivitySettings"
        const val REQUEST_ACCESS_MAIN_MENU = 1
        const val REQUEST_ACCESS_QUICK_MENU = 2
        const val PREF_TYPE = "ReturnType"
        const val PREF_TYPE_OPEN_BOOK = 121
        const val PREF_TYPE_SETTINGS = 122
    }

}