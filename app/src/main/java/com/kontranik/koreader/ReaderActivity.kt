package com.kontranik.koreader

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Point
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.kontranik.koreader.database.BookStatusDatabaseAdapter
import com.kontranik.koreader.database.BookStatusService
import com.kontranik.koreader.database.BookmarkService
import com.kontranik.koreader.database.BookmarksDatabaseAdapter
import com.kontranik.koreader.model.*
import com.kontranik.koreader.reader.*
import com.kontranik.koreader.utils.*
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class ReaderActivity :
        AppCompatActivity(),
        QuickMenuFragment.QuickMenuDialogListener,
        PermissionsHelper.PermissionsHelperListener,
        BookmarkListFragment.BookmarkListDialogListener,
        GotoMenuFragment.GotoMenuDialogListener
{

    private val prefsHelper = PrefsHelper()

    private var book: Book? = null

    private var pageView: TextView? = null

    private var textViewInfoCenter: TextView? = null
    private var textViewInfoLeft: TextView? = null
    private var textViewInfoRight: TextView? = null

    private var width: Int = 100
    private var fullwidth: Int = 100
    private var height: Int = 100
    private var fullheight: Int = 100

    private var settings: SharedPreferences? = null
    private var prefEditor: SharedPreferences.Editor? = null

    private var bookStatusService: BookStatusService? = null

    private var backButtonPressedTime = Date().time


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadSettings()
        loadPrefs()

        setContentView(R.layout.activity_reader_main)

        bookStatusService = BookStatusService(BookStatusDatabaseAdapter(this))

        pageView = findViewById(R.id.textView_pageview)
        TextViewInitiator.initiateTextView(pageView!!, "")
        pageView!!.textSize = prefsHelper.textSize
        pageView!!.typeface = prefsHelper.font.getTypeface()
        pageView!!.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            updateSizeInfo()
        }


        textViewInfoCenter = findViewById(R.id.tv_infotext_center)
        textViewInfoLeft = findViewById(R.id.tv_infotext_left)
        textViewInfoRight = findViewById(R.id.tv_infotext_right)

        setOnClickListener()

        // bei jedem start - bereinigen
        Thread {
            Log.d(TAG, "cleanup...")
            bookStatusService!!.cleanup()
            Log.d(TAG, "ready...")
        }.start()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //Here you can get the size of pageView!
        Log.d(TAG, "onWindowFocusChanged")
        val ph = PermissionsHelper(this)
        ph.checkPermissionsExternalStorage(pageView!!)
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
        Log.d(TAG, "loadBook: ${prefsHelper.bookPath}")
        book =  Book(applicationContext, prefsHelper.bookPath!!, pageView!!)
        if ( book != null ) {
            bookStatusService!!.updateLastOpenTime(prefsHelper.bookPath!!)
        } else {
            Toast.makeText(this, "Can't load book ${prefsHelper.bookPath}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == REQUEST_ACCESS_MAIN_MENU ){
            if(resultCode==RESULT_OK){
                if (data != null && data.hasExtra(PREF_TYPE)) {
                    when (data.getIntExtra(PREF_TYPE, 0)) {
                        PREF_TYPE_OPEN_BOOK -> openBookFromIntent(data)
                        PREF_TYPE_SETTINGS -> loadSettings()
                    }
                }
            } else{
                Log.e(TAG, "onActivityResult: access error")
            }
        } else if ( requestCode == REQUEST_ACCESS_QUICK_MENU) {
            if(resultCode==RESULT_OK){
                if (data != null ) {
                    if ( data.hasExtra(PrefsHelper.PREF_BOOK_TEXT_SIZE) ) {
                        prefsHelper.textSize = data.getFloatExtra(PrefsHelper.PREF_BOOK_TEXT_SIZE, prefsHelper.defaultTextSize)
                        savePrefs()
                        pageView!!.textSize = prefsHelper.textSize
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
        if ( book == null) return
        val startPosition = bookStatusService!!.getPosition(prefsHelper.bookPath!!) ?: BookPosition()
        book!!.curPage = Page(null, startPosition, BookPosition())
    }

    private fun savePositionForBook() {
        if ( prefsHelper.bookPath != null && book != null && book!!.curPage != null) {
            bookStatusService!!.savePosition(prefsHelper.bookPath!!, book!!.curPage!!.startBookPosition)
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
        prefsHelper.defaultTextSize = resources.getDimension(R.dimen.text_size)
        prefsHelper.textSize = prefsHelper.defaultTextSize

        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)

        if ( settings!!.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            prefsHelper.bookPath = settings!!.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        if ( settings!!.contains(PrefsHelper.PREF_BOOK_TEXT_SIZE) ) {
            prefsHelper.textSize = settings!!.getFloat(PrefsHelper.PREF_BOOK_TEXT_SIZE, prefsHelper.defaultTextSize)
        }

        if ( settings!!.contains(PrefsHelper.PREF_SCREEN_BRIGHTNESS) ) {
            prefsHelper.screenBrightnessLevel = settings!!.getFloat(PrefsHelper.PREF_SCREEN_BRIGHTNESS, prefsHelper.systemScreenBrightnessLevel)
            prefsHelper.setScreenBrightness(this, prefsHelper.screenBrightnessLevel)
        }

        if ( settings!!.contains(PrefsHelper.PREF_BOOK_FONT_PATH) ) {
            val fontpath = settings!!.getString(PrefsHelper.PREF_BOOK_FONT_PATH, null)
            if ( fontpath != null) {
                val fontFile = File(fontpath)
                if ( fontFile.isFile && fontFile.canRead() ) {
                    prefsHelper.font = TypefaceRecord(name = fontFile.name, file = fontFile)
                }
            }
        } else if ( settings!!.contains(PrefsHelper.PREF_BOOK_FONT_NAME)) {
            prefsHelper.font = TypefaceRecord(
                    name = settings!!.getString(PrefsHelper.PREF_BOOK_FONT_NAME, TypefaceRecord.SANSSERIF)!!)
        }
    }

    private fun savePrefs() {
        prefEditor = settings!!.edit()
        prefEditor!!.putString(PrefsHelper.PREF_BOOK_PATH, prefsHelper.bookPath)
        prefEditor!!.putFloat(PrefsHelper.PREF_BOOK_TEXT_SIZE, prefsHelper.textSize)
        prefEditor!!.putFloat(PrefsHelper.PREF_SCREEN_BRIGHTNESS, prefsHelper.screenBrightnessLevel)
        if ( prefsHelper.font.file != null) prefEditor!!.putString(PrefsHelper.PREF_BOOK_FONT_PATH, prefsHelper.font.file!!.absolutePath)
        else prefEditor!!.putString(PrefsHelper.PREF_BOOK_FONT_NAME, prefsHelper.font.name)
        prefEditor!!.apply()
    }

    private fun loadSettings() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        prefsHelper.theme = prefs.getString(PrefsHelper.PREF_KEY_THEME, "Auto")
        prefsHelper.screenOrientation = prefs.getString(PrefsHelper.PREF_KEY_ORIENTATION, "PortraitSensor")
        prefsHelper.screenBrightness = prefs.getString(PrefsHelper.PREF_KEY_BRIGHTNESS, "Manual")

        prefsHelper.setOrientation(this)
        prefsHelper.setTheme()

        try {
            if (Build.VERSION.SDK_INT >= 21) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // set status text dark
                        }
                    }
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {

        textViewInfoCenter!!.setOnTouchListener(object : OnSwipeTouchListener(this@ReaderActivity) {
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
                    val zone = ScreenZone.zone(point, width, height)
                    textViewInfoRight!!.text = resources.getString(R.string.click_in_zone, zone)
                    when (zone) {
                        ScreenZone.BottomRight -> {
                            doPageNext()
                        }
                        ScreenZone.BottomLeft -> {
                            doPagePrev()
                        }
                        ScreenZone.MiddleCenter -> {
                            openMainMenu()
                        }
                        else -> {
                        }
                    }
                }
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                textViewInfoRight!!.text = getString(R.string.swipe_left)
                doPageNext()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                textViewInfoRight!!.text = getString(R.string.swipe_right)
                doPagePrev()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                textViewInfoRight!!.text = getString(R.string.swipe_up)
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                textViewInfoRight!!.text = getString(R.string.swipe_down)
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
                val zone = ScreenZone.zone(point, width, height)
                when (zone) {
                    ScreenZone.MiddleCenter -> openQuickMenu()
                    else -> {
                    }
                }
                textViewInfoRight!!.text = resources.getString(R.string.doubleclick_in_zone, zone)
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
                    val zone = ScreenZone.zone(point, width, height)
                    when (zone) {
                        ScreenZone.MiddleCenter -> {
                        }
                        else -> {
                        }
                    }
                    textViewInfoRight!!.text = resources.getString(R.string.longclick_in_zone, zone)
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
            var curSection = book!!.curPage!!.endBookPosition.section
            curSection++

            var curTextPage = 0
            for (i in 0 .. curSection-2) {
                curTextPage += book!!.getPageScheme()!!.scheme[i]!!.textPages
            }
            curTextPage += ( book!!.curPage!!.endBookPosition.offSet / BookPageScheme.CHAR_PER_PAGE )

            textViewInfoLeft!!.text =
                    resources.getString(R.string.page_info_text, curTextPage, book!!.getPageScheme()!!.textPages)

            textViewInfoCenter!!.text =
                    resources.getString(R.string.page_info_text, curSection, book!!.getPageScheme()!!.sectionCount)
        } else {
            textViewInfoCenter!!.text = getString(R.string.no_book) 
        }
    }

    private fun updateView(page: Page?) {
        if ( page != null ) {
            book!!.curPage = Page(page)
            pageView!!.text = page.content
        } else {
            pageView!!.text = "no page content"
        }

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
                FloatTextViewFragment.newInstance(html)
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
        val quickMenuFragment: QuickMenuFragment = QuickMenuFragment.newInstance(prefsHelper.textSize, prefsHelper.font)
        quickMenuFragment.show(supportFragmentManager, "fragment_quick_menu")
    }

    private fun openGotoMenu() {
        if ( book == null || book!!.curPage == null) return
        val gotoMenuFragment: GotoMenuFragment =
                GotoMenuFragment
                        .newInstance(book!!.curPage!!.endBookPosition.section, book!!.getPageScheme()!!.sectionCount)
        gotoMenuFragment.show(supportFragmentManager, "fragment_goto_menu")
    }

    override fun onFinishQuickMenuDialog(textSize: Float, font: TypefaceRecord?) {
        Log.d(TAG, "onFinishQuickMenuDialog. TextSize: $textSize")
        if ( textSize != pageView!!.textSize
                || ( font != null && font.getTypeface() != pageView!!.typeface) ) {
            prefsHelper.textSize = textSize
            pageView!!.textSize = textSize
            if ( font != null) {
                prefsHelper.font = font
                pageView!!.typeface = font.getTypeface()
            }
            //book!!.loadPage(pageView!!)
            //updateView()
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

    override fun onCancelQuickMenu() {
        if ( pageView!!.textSize != prefsHelper.textSize ) {
            pageView!!.textSize = prefsHelper.textSize
            Log.d(TAG, "onCancelQuickMenu: getCurPage")
            updateView(book!!.getCur(recalc = true))
        }
        // ... other resets
    }

    override fun onAddBookmark(): Boolean {
        val bookmarkService = BookmarkService(BookmarksDatabaseAdapter(this))
        val bookmark = Bookmark(
                path = prefsHelper.bookPath!!,
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
        if ( prefsHelper.bookPath == null ) return
        val bookmarkListFragment: BookmarkListFragment = BookmarkListFragment.newInstance(prefsHelper.bookPath!!)
        bookmarkListFragment.show(supportFragmentManager, "fragment_bookmark_list")
    }

    override fun onAccessGrantedReadExternalStorage() {
        Log.d(TAG, "onAccessGrantedReadExternalStorage")
        if (prefsHelper.bookPath != null) {
            if ( book == null || book!!.fileLocation != prefsHelper.bookPath) {
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

    override fun onFinishGotoMenuDialog(section: Int) {
        book!!.curPage = Page(null, BookPosition(section = section), BookPosition())
        Log.d(TAG, "onFinishGotoMenuDialog: getCurPage")
        updateView(book!!.getCur(recalc = true))
        savePositionForBook()
    }

    private fun startProgress() {
        pageView!!.text = "loading Book..."
    }

    companion object {
        private const val TAG = "ReaderActivity"
        private const val PREFS_FILE = "MainActivitySettings"
        const val REQUEST_ACCESS_MAIN_MENU = 1
        const val REQUEST_ACCESS_QUICK_MENU = 2
        const val PREF_TYPE = "ReturnType"
        const val PREF_TYPE_OPEN_BOOK = 121
        const val PREF_TYPE_SETTINGS = 122
    }

}