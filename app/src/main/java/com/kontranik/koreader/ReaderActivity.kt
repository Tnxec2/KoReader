package com.kontranik.koreader

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.kontranik.koreader.database.BookStatusDatabaseAdapter
import com.kontranik.koreader.database.BookStatusService
import com.kontranik.koreader.database.BookmarkService
import com.kontranik.koreader.database.BookmarksDatabaseAdapter
import com.kontranik.koreader.model.*
import com.kontranik.koreader.reader.*
import com.kontranik.koreader.utils.*
import com.kontranik.koreader.utils.OnSwipeTouchListener
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class ReaderActivity :
        AppCompatActivity(),
        QuickMenuFragment.QuickMenuDialogListener,
        BookmarkListFragment.BookmarkListDialogListener,
        GotoMenuFragment.GotoMenuDialogListener
{

    private var prefsHelper: PrefsHelper? = null

    private var book: Book? = null

    private var textViewHolder: ConstraintLayout? = null
    private var pageView: TextView? = null

    private var textViewInfoLeft: TextView? = null
    private var textViewInfoRight: TextView? = null
    private var linearLayoutInfobereich: LinearLayout? = null

    private var width: Int = 100
    private var fullwidth: Int = 100
    private var height: Int = 100
    private var fullheight: Int = 100

    private var bookStatusService: BookStatusService? = null

    private var backButtonPressedTime = Date().time


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefsHelper = PrefsHelper(this)
        
        setContentView(R.layout.activity_reader_main)

        bookStatusService = BookStatusService(BookStatusDatabaseAdapter(this))
        textViewHolder = findViewById(R.id.textViewHolder)
        pageView = findViewById(R.id.textView_pageview)


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

        TextViewInitiator.initiateTextView(pageView!!, "")

        loadSettings()
        loadPrefs()

        pageView!!.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Log.d(TAG, "pageView layout changed")
            updateSizeInfo()
        }

        textViewInfoLeft = findViewById(R.id.tv_infotext_left)
        textViewInfoRight = findViewById(R.id.tv_infotext_right)
        linearLayoutInfobereich = findViewById(R.id.ll_infobereich)

        setColors()

        setOnClickListener()

        AsyncTask.execute(Runnable {
            bookStatusService!!.cleanup(applicationContext)
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //Here you can get the size of pageView!
        Log.d(TAG, "onWindowFocusChanged")
        //val ph = PermissionsHelper(this)
        //ph.checkPermissionsExternalStorage(pageView!!)
        setColors()
        if (prefsHelper!!.bookPath != null) {
            if ( book == null || book!!.fileLocation != prefsHelper!!.bookPath) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("loading")
                progressDialog.show()

                runOnUiThread {
                    try {
                        loadBook()
                        loadPositionForBook()
                        updateView(book!!.getCur(recalc = true))
                    } catch (e: Exception) {
                        Log.e("tag", e.message)
                    }
                    // dismiss the progress dialog
                    progressDialog.dismiss()
                }

            }
        } else  {
            Toast.makeText(applicationContext, "Open a book", Toast.LENGTH_LONG).show()
            openMainMenu()
        }
    }

    override fun onBackPressed() {
        if (Date().time - backButtonPressedTime < 2000) {
            finish()
        } else {
            backButtonPressedTime = Date().time
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBook() {
        Log.d(TAG, "loadBook: ${prefsHelper!!.bookPath}")

        if ( ! FileHelper.contentFileExist(applicationContext, prefsHelper!!.bookPath) ) {
            Toast.makeText(this, "Can't load book ${prefsHelper!!.bookPath}", Toast.LENGTH_LONG).show()
            openMainMenu()
        }
        book =  Book(applicationContext, prefsHelper!!.bookPath!!, pageView!!)
        if ( book != null ) {
            bookStatusService!!.updateLastOpenTime(book!!)
        } else {
            Toast.makeText(this, "Can't load book ${prefsHelper!!.bookPath}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == REQUEST_ACCESS_MAIN_MENU ){
            if(resultCode==RESULT_OK){
                if (data != null && data.hasExtra(PREF_TYPE)) {
                    when (data.getIntExtra(PREF_TYPE, 0)) {
                        PREF_TYPE_OPEN_BOOK -> openBookFromIntent(data)
                        PREF_TYPE_SETTINGS -> {
                            loadSettings()
                        }
                    }
                }
            } else{
                Log.e(TAG, "onActivityResult: access error")
            }
        } else if ( requestCode == REQUEST_ACCESS_QUICK_MENU) {
            if(resultCode==RESULT_OK){
                if (data != null ) {
                    var changed = false
                    if ( data.hasExtra(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE) ) {
                        prefsHelper!!.textSize = data.getFloatExtra(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, prefsHelper!!.defaultTextSize)
                        pageView!!.textSize = prefsHelper!!.textSize
                        changed = true
                    }
                    if ( data.hasExtra(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING) ) {
                        prefsHelper!!.lineSpacing = data.getFloatExtra(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, prefsHelper!!.defaultLineSpacing)
                        pageView!!.setLineSpacing(pageView!!.lineSpacingMultiplier, prefsHelper!!.lineSpacing)
                        changed = true
                    }
                    if ( changed ) {
                        //book!!.loadPage(pageView!!)
                        //updateView( )
                        Log.d(TAG, "onActivityResult: getCurPage")
                        updateView(book!!.getCur(recalc = true))
                    }
                }
            } else{
                Log.e(TAG, "onActivityResult: access error")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openBookFromIntent(data: Intent) {
        Log.d(TAG, "openBookFromIntent")
        if ( data.hasExtra(PrefsHelper.PREF_BOOK_PATH) ) {
            prefsHelper!!.bookPath = data.getStringExtra(PrefsHelper.PREF_BOOK_PATH)
            savePrefs()
            startProgress()
            loadBook()
            loadPositionForBook()
            Log.d(TAG, "openBookFromIntent: getCurPage")
            updateView(book!!.getCur(recalc = true))
        }
    }

    private fun loadPositionForBook() {
        if ( book == null) return
        val startPosition = bookStatusService!!.getPosition(prefsHelper!!.bookPath!!) ?: BookPosition()
        book!!.curPage = Page(null, startPosition, BookPosition())
    }

    private fun savePositionForBook() {
        if ( prefsHelper!!.bookPath != null && book != null && book!!.curPage != null) {
            bookStatusService!!.savePosition(book!!)
        }
    }

    override fun onPause() {
        super.onPause()
        // savePositionForBook()
    }

    override fun onStop() {
        super.onStop()
        savePrefs()
    }

    private fun loadPrefs() {
        val settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)

        if ( settings.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            prefsHelper!!.bookPath = settings!!.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        if ( settings.contains(PrefsHelper.PREF_SCREEN_BRIGHTNESS) ) {
            prefsHelper!!.screenBrightnessLevel = settings!!.getFloat(PrefsHelper.PREF_SCREEN_BRIGHTNESS, prefsHelper!!.systemScreenBrightnessLevel)
            prefsHelper!!.setScreenBrightness(this, prefsHelper!!.screenBrightnessLevel)
        }
    }

    private fun savePrefs() {
        val settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
        val prefEditor = settings.edit()
        prefEditor.putString(PrefsHelper.PREF_BOOK_PATH, prefsHelper!!.bookPath)
        prefEditor.putFloat(PrefsHelper.PREF_SCREEN_BRIGHTNESS, prefsHelper!!.screenBrightnessLevel)
        prefEditor.apply()
    }

    private fun loadSettings() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        prefsHelper!!.theme = prefs.getString(PrefsHelper.PREF_KEY_THEME, "Auto")
        prefsHelper!!.screenOrientation = prefs.getString(PrefsHelper.PREF_KEY_ORIENTATION, "PortraitSensor")
        prefsHelper!!.screenBrightness = prefs.getString(PrefsHelper.PREF_KEY_BRIGHTNESS, "Manual")

        prefsHelper!!.textSize = prefs.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, prefsHelper!!.defaultTextSize)

        val lineSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, null )
        if ( lineSpacingString != null) prefsHelper!!.lineSpacing = lineSpacingString.toFloat()
        else prefsHelper!!.lineSpacing = prefsHelper!!.defaultLineSpacing

        val letterSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, null )
        if ( letterSpacingString != null) prefsHelper!!.letterSpacing = letterSpacingString.toFloat()
        else prefsHelper!!.letterSpacing = prefsHelper!!.defaultLetterSpacing

        if ( prefs.contains(PrefsHelper.PREF_KEY_BOOK_FONT_PATH) ) {
            val fontpath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH, null)
            if ( fontpath != null) {
                val fontFile = File(fontpath)
                if ( fontFile.isFile && fontFile.canRead() ) {
                    prefsHelper!!.font = TypefaceRecord(name = fontFile.name, file = fontFile)
                }
            }
        } else if ( prefs.contains(PrefsHelper.PREF_KEY_BOOK_FONT_NAME)) {
            prefsHelper!!.font = TypefaceRecord(
                    name = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME, TypefaceRecord.SANSSERIF)!!)
        }

        pageView!!.textSize = prefsHelper!!.textSize
        pageView!!.typeface = prefsHelper!!.font.getTypeface()
        pageView!!.setLineSpacing(pageView!!.lineSpacingMultiplier, prefsHelper!!.lineSpacing)
        pageView!!.letterSpacing = prefsHelper!!.letterSpacing

        var co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_LIGHT_BACK, 0)
        if ( co != 0 )
            prefsHelper!!.colorLightBack = "#" + Integer.toHexString(co)
        else
            prefsHelper!!.colorLightBack = prefsHelper!!.colorLightBackDefault
        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_LIGHT_TEXT, 0)
        if ( co != 0 )
            prefsHelper!!.colorLightText = "#" + Integer.toHexString(co)
        else
            prefsHelper!!.colorLightText = prefsHelper!!.colorLightTextDefault

        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_LIGHT_LINKTEXT, 0)
        if ( co != 0 )
            prefsHelper!!.colorLightLinkText = "#" + Integer.toHexString(co)
        else
            prefsHelper!!.colorLightLinkText = prefsHelper!!.colorLightLinkTextDefault


        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_DARK_BACK, 0)
        if ( co != 0 )
            prefsHelper!!.colorDarkBack = "#" + Integer.toHexString(co)
        else
            prefsHelper!!.colorDarkBack = prefsHelper!!.colorDarkBackDefault
        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_DARK_TEXT, 0)
        if ( co != 0 )
            prefsHelper!!.colorDarkText = "#" + Integer.toHexString(co)
        else
            prefsHelper!!.colorDarkText = prefsHelper!!.colorDarkTextDefault
        co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_DARK_LINKTEXT, 0)
        if ( co != 0 )
            prefsHelper!!.colorDarkLinkText = "#" + Integer.toHexString(co)
        else
            prefsHelper!!.colorDarkLinkText = prefsHelper!!.colorDarkLinkTextDefault

        prefsHelper!!.tapDoubleAction = hashMapOf(
                ScreenZone.TopLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_TOP_LEFT, prefsHelper!!.tapZoneDoubleTopLeft),
                ScreenZone.TopCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_TOP_CENTER, prefsHelper!!.tapZoneDoubleTopCenter),
                ScreenZone.TopRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_TOP_RIGHT, prefsHelper!!.tapZoneDoubleTopRight),
                ScreenZone.MiddleLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_MIDDLE_LEFT, prefsHelper!!.tapZoneDoubleMiddleLeft),
                ScreenZone.MiddleCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_MIDDLE_CENTER, prefsHelper!!.tapZoneDoubleMiddleCenter),
                ScreenZone.MiddleRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_MIDDLE_RIGHT, prefsHelper!!.tapZoneDoubleMiddleRight),
                ScreenZone.BottomLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_BOTTOM_LEFT, prefsHelper!!.tapZoneDoubleBottomLeft),
                ScreenZone.BottomCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_BOTTOM_CENTER, prefsHelper!!.tapZoneDoubleBottomCenter),
                ScreenZone.BottomRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_DOUBLE_BOTTOM_RIGHT, prefsHelper!!.tapZoneDoubleBottomRight),
        )
        
        prefsHelper!!.tapOneAction = hashMapOf(
                ScreenZone.TopLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_TOP_LEFT, prefsHelper!!.tapZoneOneTopLeft),
                ScreenZone.TopCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_TOP_CENTER, prefsHelper!!.tapZoneOneTopCenter),
                ScreenZone.TopRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_TOP_RIGHT, prefsHelper!!.tapZoneOneTopRight),
                ScreenZone.MiddleLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_MIDDLE_LEFT, prefsHelper!!.tapZoneOneMiddleLeft),
                ScreenZone.MiddleCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_MIDDLE_CENTER, prefsHelper!!.tapZoneOneMiddleCenter),
                ScreenZone.MiddleRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_MIDDLE_RIGHT, prefsHelper!!.tapZoneOneMiddleRight),
                ScreenZone.BottomLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_BOTTOM_LEFT, prefsHelper!!.tapZoneOneBottomLeft),
                ScreenZone.BottomCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_BOTTOM_CENTER, prefsHelper!!.tapZoneOneBottomCenter),
                ScreenZone.BottomRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_ONE_BOTTOM_RIGHT, prefsHelper!!.tapZoneOneBottomRight),
        )        
        prefsHelper!!.tapLongAction = hashMapOf(
                ScreenZone.TopLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_TOP_LEFT, prefsHelper!!.tapZoneLongTopLeft),
                ScreenZone.TopCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_TOP_CENTER, prefsHelper!!.tapZoneLongTopCenter),
                ScreenZone.TopRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_TOP_RIGHT, prefsHelper!!.tapZoneLongTopRight),
                ScreenZone.MiddleLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_MIDDLE_LEFT, prefsHelper!!.tapZoneLongMiddleLeft),
                ScreenZone.MiddleCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_MIDDLE_CENTER, prefsHelper!!.tapZoneLongMiddleCenter),
                ScreenZone.MiddleRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_MIDDLE_RIGHT, prefsHelper!!.tapZoneLongMiddleRight),
                ScreenZone.BottomLeft to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_BOTTOM_LEFT, prefsHelper!!.tapZoneLongBottomLeft),
                ScreenZone.BottomCenter to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_BOTTOM_CENTER, prefsHelper!!.tapZoneLongBottomCenter),
                ScreenZone.BottomRight to prefs.getString(PrefsHelper.PREF_KEY_TAP_LONG_BOTTOM_RIGHT, prefsHelper!!.tapZoneLongBottomRight),
        )


        prefsHelper!!.setOrientation(this)
        prefsHelper!!.setTheme()

    }

    private fun checkTap(point: Point, tap: Int) {

        val zone = ScreenZone.zone(point, width, height)
        // textViewInfoRight!!.text = resources.getString(R.string.click_in_zone, tap, zone)
        Log.d(TAG, "Tap $tap in Zone $zone")
        when (tap) {
            1 -> { // double tap
                doTapAction(prefsHelper!!.tapOneAction[zone])
            }
            2 -> { // double tap
                doTapAction(prefsHelper!!.tapDoubleAction[zone])
            }
            3 -> { // long tap
                doTapAction(prefsHelper!!.tapLongAction[zone])
            }
        }
    }

    private fun doTapAction(tapAction: String?) {
        Log.d(TAG, "doTapAction: $tapAction")
        if ( tapAction == null) return
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
            "None" -> {
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {

        linearLayoutInfobereich!!.setOnTouchListener(object : OnSwipeTouchListener(this@ReaderActivity) {
            override fun onClick(point: Point) {
                super.onClick(point)
                openGotoMenu()
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)
                openGotoMenu()
            }
        })

        pageView!!.setOnTouchListener(object : OnSwipeTouchListener(this@ReaderActivity) {
            override fun onClick(point: Point) {
                super.onClick(point)

                // Find the URL that was pressed
                val off = getClickedOffset(point)
                val spannable = pageView!!.text as Spannable
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

            override fun onSwipeUp() {
                super.onSwipeUp()
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
            }

            override fun onSlideUp(point: Point) {
                super.onSlideUp(point)
                prefsHelper!!.increaseScreenBrghtness(this@ReaderActivity, point, fullwidth)
            }

            override fun onSlideDown(point: Point) {
                super.onSlideDown(point)
                prefsHelper!!.decreaseScreenBrghtness(this@ReaderActivity, point, fullwidth)
            }

            override fun onDoubleClick(point: Point) {
                //super.onDoubleClick(point)
                checkTap(point, 2)
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)

                // Find the Image that was pressed
                val off = getClickedOffset(point)
                val spannable = pageView!!.text as Spannable
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
        val fw = pageView!!.measuredWidth
        val fh = pageView!!.measuredHeight
        val w = pageView!!.measuredWidth - pageView!!.paddingLeft - pageView!!.paddingRight
        val h = pageView!!.measuredHeight - pageView!!.paddingTop - pageView!!.paddingBottom

        // Toast.makeText(this, "$w x $h", Toast.LENGTH_SHORT).show()

        if ( w != width || h != height) {
            fullwidth = fw
            fullheight = fh
            width = w
            height = h
            if ( book != null) {
                Log.d(TAG, "updateSizeInfo: getCurPage")
                updateView(book!!.getCur(recalc = true))
            }
        }
    }

    private fun doPageNext() {
        if ( book == null) return
        if ( book!!.curPage!!.endBookPosition.section >= book!!.getPageScheme()!!.sectionCount-1 &&
                book!!.curPage!!.endBookPosition.offSet >=
                book!!.getPageScheme()!!.scheme[book!!.getPageScheme()!!.sectionCount - 1]!!.textSize) return
        updateView(book!!.getNext())
        savePositionForBook()
    }

    private fun doPagePrev() {
        if ( book == null) return
        if ( book!!.curPage!!.startBookPosition.section <= 0 &&
                book!!.curPage!!.startBookPosition.offSet <= 0) return
        updateView(book!!.getPrev())
        savePositionForBook()
    }

    private fun updateInfo() {
        if ( book != null && book!!.curPage != null) {
            val curSection = book!!.getCurSection()

            val curTextPage = book!!.getCurTextPage()

            textViewInfoLeft!!.text =
                    resources.getString(R.string.page_info_text, curTextPage, book!!.getPageScheme()!!.textPages)

            textViewInfoRight!!.text =
                    resources.getString(R.string.page_info_text, curSection, book!!.getPageScheme()!!.sectionCount)
        } else {
            textViewInfoRight!!.text = getString(R.string.no_book)
        }
    }

    private fun updateView(page: Page?) {
        if ( page != null ) {
            book!!.curPage = Page(page)
            pageView!!.text = page.content
        } else {
            pageView!!.text = "no page content"
        }

        pageView!!.fontFeatureSettings

        updateInfo()
    }

    private fun getClickedOffset(point: Point): Int {
        // check if Link or image clicked
        var x = point.x
        var y = point.y
        x -= pageView!!.totalPaddingLeft
        y -= pageView!!.totalPaddingTop
        x += pageView!!.scrollX
        y += pageView!!.scrollY
        // Locate the clicked span
        val layout = pageView!!.layout
        val line = layout.getLineForVertical(y)
        return layout.getOffsetForHorizontal(line, x.toFloat())
    }

    private fun showNote(html: String?) {
        if ( html == null) return
        val floatTextViewFragment: FloatTextViewFragment =
                FloatTextViewFragment.newInstance(html, prefsHelper!!.textSize, prefsHelper!!.font)
        floatTextViewFragment.show(supportFragmentManager, "fragment_floattextview")
    }

    private fun showImage(imageSpan: ImageSpan) {
        val b: ByteArray
        b = if ( imageSpan.source != null) {
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
        val quickMenuFragment: QuickMenuFragment = QuickMenuFragment()
        quickMenuFragment.show(supportFragmentManager, "fragment_quick_menu")
    }



    override fun onFinishQuickMenuDialog(textSize: Float, lineSpacing: Float, letterSpacing: Float, font: TypefaceRecord?) {
        Log.d(TAG, "onFinishQuickMenuDialog. TextSize: $textSize")
        if ( textSize != pageView!!.textSize
                || lineSpacing != pageView!!.lineSpacingMultiplier
                || letterSpacing != pageView!!.letterSpacing
                || ( font != null && font.getTypeface() != pageView!!.typeface)  ) {
            prefsHelper!!.textSize = textSize
            prefsHelper!!.lineSpacing = lineSpacing
            prefsHelper!!.letterSpacing = letterSpacing
            pageView!!.textSize = textSize
            pageView!!.setLineSpacing(pageView!!.lineSpacingExtra, lineSpacing)
            if ( font != null) {
                prefsHelper!!.font = font
                pageView!!.typeface = font.getTypeface()
            }
            Log.d(TAG, "onFinishQuickMenuDialog: getCurPage")
            updateView(book!!.getCur(recalc = true))
            savePrefs()
        }
    }

    override fun onChangeTextSize(textSize: Float) {
        pageView!!.textSize = textSize
        Log.d(TAG, "onChangeTextSize: getCurPage")
        updateView(book!!.getCur(recalc = true))
    }

    override fun onChangeLineSpacing(lineSpacing: Float) {
        pageView!!.setLineSpacing(pageView!!.lineSpacingExtra, lineSpacing)
        Log.d(TAG, "onChangeLineSpacing: getCurPage")
        updateView(book!!.getCur(recalc = true))
    }

    override fun onChangeLetterSpacing(letterSpacing: Float) {
        pageView!!.letterSpacing = letterSpacing
        Log.d(TAG, "onChangeLetterSpacing: getCurPage")
        updateView(book!!.getCur(recalc = true))
    }

    override fun onCancelQuickMenu() {
        if ( pageView!!.textSize != prefsHelper!!.textSize || pageView!!.lineSpacingMultiplier != prefsHelper!!.lineSpacing ) {
            pageView!!.textSize = prefsHelper!!.textSize
            pageView!!.setLineSpacing(pageView!!.lineSpacingExtra, prefsHelper!!.lineSpacing)
            pageView!!.letterSpacing = prefsHelper!!.letterSpacing
            Log.d(TAG, "onCancelQuickMenu: getCurPage")
            updateView(book!!.getCur(recalc = true))
        }
        // ... other resets
    }

    override fun onAddBookmark(): Boolean {
        val bookmarkService = BookmarkService(BookmarksDatabaseAdapter(this))
        val bookmark = Bookmark(
                path = prefsHelper!!.bookPath!!,
                text = pageView!!.text.toString().substring(0, 100),
                position_section = book!!.curPage!!.startBookPosition.section,
                position_offset = book!!.curPage!!.startBookPosition.offSet,
        )

        return if ( bookmarkService.addBookmark(bookmark) ) {
            Toast.makeText(this, "Bookmark saved", Toast.LENGTH_SHORT).show()
            true
        } else {
            false
        }
    }

    override fun onShowBookmarklist() {
        if ( prefsHelper!!.bookPath == null ) return
        val bookmarkListFragment: BookmarkListFragment = BookmarkListFragment.newInstance(prefsHelper!!.bookPath!!)
        bookmarkListFragment.show(supportFragmentManager, "fragment_bookmark_list")
    }

    /*
    override fun onAccessGrantedReadExternalStorage() {
        Log.d(TAG, "onAccessGrantedReadExternalStorage")
        if (prefsHelper!!.bookPath != null) {
            if ( book == null || book!!.fileLocation != prefsHelper!!.bookPath) {
                startProgress()
                loadBook()
                loadPositionForBook()
                updateView(book!!.getCur(recalc = true))
            }
        } else  {
            Toast.makeText(applicationContext, "Open a book", Toast.LENGTH_LONG).show()
            openMainMenu()
        }
    }
    */

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
        if ( book == null || book!!.curPage == null) return
        val gotoMenuFragment: GotoMenuFragment =
                GotoMenuFragment
                        .newInstance(
                                book!!.curPage!!.endBookPosition.section,
                                book!!.getCurTextPage(),
                                book!!.getPageScheme()!!.textPages,
                                book!!.getPageScheme()!!.sections.toTypedArray()
                        )
        gotoMenuFragment.show(supportFragmentManager, "fragment_goto_menu")
    }

    override fun onFinishGotoMenuDialogPage(page: Int) {
        book!!.curPage = Page(null, book!!.ebookHelper!!.pageScheme.getBookPositionForPage(page), BookPosition())
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
        pageView!!.text = "loading Book..."
    }

    private fun setColors() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_UNDEFINED,
            Configuration.UI_MODE_NIGHT_YES -> {
                textViewHolder!!.setBackgroundColor(Color.parseColor(prefsHelper!!.colorDarkBack))
                pageView!!.setTextColor(Color.parseColor(prefsHelper!!.colorDarkText))
                pageView!!.setLinkTextColor(Color.parseColor(prefsHelper!!.colorDarkLinkText))
                textViewInfoLeft!!.setTextColor(Color.parseColor(prefsHelper!!.colorDarkText))
                textViewInfoRight!!.setTextColor(Color.parseColor(prefsHelper!!.colorDarkText))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                textViewHolder!!.setBackgroundColor(Color.parseColor(prefsHelper!!.colorLightBack))
                pageView!!.setTextColor(Color.parseColor(prefsHelper!!.colorLightText))
                pageView!!.setLinkTextColor(Color.parseColor(prefsHelper!!.colorLightLinkText))
                textViewInfoLeft!!.setTextColor(Color.parseColor(prefsHelper!!.colorLightText))
                textViewInfoRight!!.setTextColor(Color.parseColor(prefsHelper!!.colorLightText))
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= 21) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                //window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

                var pIsDark = false
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        window.statusBarColor = Color.parseColor(prefsHelper!!.colorDarkBack)
                        pIsDark = true
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        window.statusBarColor = Color.parseColor(prefsHelper!!.colorLightBack)
                    }
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                        window.statusBarColor = Color.parseColor(prefsHelper!!.colorDarkBack)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val lFlags = window.decorView.systemUiVisibility;
                    window.decorView.systemUiVisibility =
                            if (pIsDark) (lFlags and (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR).inv() ) else
                                      (lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun isExternalStorageReadable(): Boolean {
        val state: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state ||
                Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    private fun checkPermissions(): Boolean {
        if (!isExternalStorageReadable() ) {
            Toast.makeText(this, "external storage not available", Toast.LENGTH_LONG).show()
            return false
        }
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), FontPickerFragment.READ_STORAGE_PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            FontPickerFragment.READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "read permissions granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "need permissions to read external storage", Toast.LENGTH_LONG).show()
                }
            }
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