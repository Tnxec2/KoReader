package com.kontranik.koreader

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kontranik.koreader.database.BookStatusDatabaseAdapter
import com.kontranik.koreader.database.BookStatusService
import com.kontranik.koreader.database.BookmarkService
import com.kontranik.koreader.database.BookmarksDatabaseAdapter
import com.kontranik.koreader.model.*
import com.kontranik.koreader.reader.*
import com.kontranik.koreader.utils.PermissionsHelper
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min

class ReaderActivity :
        AppCompatActivity(),
        QuickMenuFragment.QuickMenuDialogListener,
        PermissionsHelper.PermissionsHelperListener,
        BookmarkListFragment.BookmarkListDialogListener,
        GotoMenuFragment.GotoMenuDialogListener
{

    private var layoutpars: WindowManager.LayoutParams? = null
    private var systemScreenBrightnessLevel: Float = 0.5f
    private var screenBrightnessLevelMin: Float = 0.01F
    private var screenBrightnessLevelMax: Float = 1F
    private var screenBrightnessLevel: Float = 0F
    private var screenBrightnessLevelStep: Float = 0.01F

    private var screenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    private var book: Book? = null

    private var pageView: TextView? = null
    //private var viewTreeObserver: ViewTreeObserver? = null

    private var textViewInfoCenter: TextView? = null
    private var textViewInfoLeft: TextView? = null
    private var textViewInfoRight: TextView? = null

    private var pageViewHolderOuter: RelativeLayout? = null

    var width: Int = 100
    var height: Int = 100
    private var textSize: Float = 0f
    private val fontDefault: TypefaceRecord = TypefaceRecord.DEFAULT
    private var font: TypefaceRecord = fontDefault
    private var defaultTextSize: Float = 0f

    private var settings: SharedPreferences? = null
    private var prefEditor: SharedPreferences.Editor? = null

    private var bookPath: String? = null

    private var bookStatusService: BookStatusService? = null

    private var backButtonPressedTime = Date().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            if (Build.VERSION.SDK_INT >= 21) {
                val window: Window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

                val nightModeFlags: Int = this.getResources().getConfiguration().uiMode and
                        Configuration.UI_MODE_NIGHT_MASK
                when (nightModeFlags) {
                    Configuration.UI_MODE_NIGHT_YES -> {

                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                    }
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        layoutpars = window.attributes
        systemScreenBrightnessLevel = layoutpars!!.screenBrightness

        layoutpars!!.screenOrientation = screenOrientation
        window.attributes = layoutpars

        setContentView(R.layout.activity_reader_main)

        bookStatusService = BookStatusService(BookStatusDatabaseAdapter(this))

        pageView = findViewById(R.id.textView_pageview)

        defaultTextSize = resources.getDimension(R.dimen.text_size)
        textSize = defaultTextSize

        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
        loadPrefs()

        TextViewInitiator.initiateTextView(pageView!!, "")
        pageView!!.textSize = textSize
        pageView!!.typeface = font.getTypeface()

        textViewInfoCenter = findViewById(R.id.tv_infotext_center)
        textViewInfoLeft = findViewById(R.id.tv_infotext_left)
        textViewInfoRight = findViewById(R.id.tv_infotext_right)

        pageViewHolderOuter = findViewById(R.id.pageViewHolderOuter)

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
        Log.d(TAG, "loadBook: $bookPath")
        book =  Book(applicationContext, bookPath!!)
        if ( book != null ) {
            bookStatusService!!.updateLastOpenTime(bookPath!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == REQUEST_ACCESS_MAIN_MENU ){
            if(resultCode==RESULT_OK){
                if (data != null && data.hasExtra(PREF_TYPE)) {
                    when (data.getIntExtra(PREF_TYPE, 0)) {
                        PREF_TYPE_OPEN_BOOK -> openBookFromIntent(data)
                    }
                }
            } else{
                Log.e(TAG, "onActivityResult: access error")
            }
        } else if ( requestCode == REQUEST_ACCESS_QUICK_MENU) {
            if(resultCode==RESULT_OK){
                if (data != null ) {

                    if ( data.hasExtra(PREF_BOOK_TEXT_SIZE) ) {
                        textSize = data.getFloatExtra(PREF_BOOK_TEXT_SIZE, defaultTextSize)
                        savePrefs()
                        pageView!!.textSize = textSize
                        book!!.loadPage(pageView!!)
                        updateView()
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
        if ( data.hasExtra(PREF_BOOK_PATH) ) {
            bookPath = data.getStringExtra(PREF_BOOK_PATH)
            savePrefs()
            loadPositionForBook()
            loadBook()
            book!!.loadPage(pageView!!)
            updateView()
        }
    }

    private fun loadPositionForBook() {
        if ( book == null) return
        val startPosition = bookStatusService!!.getPosition(bookPath!!) ?: BookPosition()

        book!!.curPage = Page(null, startPosition, BookPosition())
        book!!.nextPage = null
        book!!.prevPage = null
    }

    private fun savePositionForBook() {
        if ( bookPath != null && book != null && book!!.curPage != null) {
            bookStatusService!!.savePosition(bookPath!!, book!!.curPage!!.startBookPosition)
        }
    }

    override fun onPause() {
        super.onPause()
        savePositionForBook()
    }

    override fun onStop() {
        super.onStop()
        savePrefs()
    }

    private fun loadPrefs() {
        if ( settings!!.contains(PREF_BOOK_PATH) ) {
            bookPath = settings!!.getString(PREF_BOOK_PATH, null)
        }
        if ( settings!!.contains(PREF_BOOK_TEXT_SIZE) ) {
            textSize = settings!!.getFloat(PREF_BOOK_TEXT_SIZE, defaultTextSize)
        }
        if ( settings!!.contains(PREF_SCREENBRIGHTNESS) ) {
            screenBrightnessLevel = settings!!.getFloat(PREF_SCREENBRIGHTNESS, systemScreenBrightnessLevel)
            setScreenBrightness(screenBrightnessLevel)
        }

        if ( settings!!.contains(PREF_BOOK_FONT_PATH) ) {
            val fontpath = settings!!.getString(PREF_BOOK_FONT_PATH, null)
            if ( fontpath != null) {
                val fontFile = File(fontpath)
                if ( fontFile.isFile && fontFile.canRead() ) {
                    font = TypefaceRecord(name = fontFile.name, file = fontFile)
                }
            }
        } else if ( settings!!.contains(PREF_BOOK_FONT_NAME)) {
            font = TypefaceRecord(
                    name = settings!!.getString(PREF_BOOK_FONT_NAME, TypefaceRecord.SANSSERIF)!!)
        }
    }

    private fun savePrefs() {
        prefEditor = settings!!.edit()
        prefEditor!!.putString(PREF_BOOK_PATH, bookPath)
        prefEditor!!.putFloat(PREF_BOOK_TEXT_SIZE, textSize)
        prefEditor!!.putFloat(PREF_SCREENBRIGHTNESS, screenBrightnessLevel)
        if ( font.file != null) prefEditor!!.putString(PREF_BOOK_FONT_PATH, font.file!!.absolutePath)
        else prefEditor!!.putString(PREF_BOOK_FONT_NAME, font.name)
        prefEditor!!.apply()
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
                //if ( point.y < 32 || point.y > width-32) {
                screenBrightnessLevel += screenBrightnessLevelStep
                screenBrightnessLevel = min(screenBrightnessLevel, screenBrightnessLevelMax)
                textViewInfoRight!!.text = (screenBrightnessLevel * 100).toString()
                setScreenBrightness(screenBrightnessLevel)
                //}
            }

            override fun onSlideDown(point: Point) {
                super.onSlideDown(point)
                //if ( point.y < 32 || point.y > width-32) {
                screenBrightnessLevel -= screenBrightnessLevelStep
                screenBrightnessLevel = max(screenBrightnessLevel, screenBrightnessLevelMin)
                textViewInfoRight!!.text = (screenBrightnessLevel * 100).toString()
                setScreenBrightness(screenBrightnessLevel)
                //}
            }

            override fun onClick(point: Point) {
                super.onClick(point)
                val zone = ScreenZone.zone(point, width, height)
                textViewInfoRight!!.text = resources.getString(R.string.click_in_zone, zone)
                when (zone) {
                    ScreenZone.BottomRight -> doPageNext()
                    ScreenZone.BottomLeft -> doPagePrev()
                    else -> {
                    }
                }
            }

            override fun onDoubleClick(point: Point) {
                super.onDoubleClick(point)
                val zone = ScreenZone.zone(point, width, height)
                when (zone) {
                    ScreenZone.MiddleCenter -> {
                        openQuickMenu()
                    }
                }
                textViewInfoRight!!.text = resources.getString(R.string.doubleclick_in_zone, zone)
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)
                val zone = ScreenZone.zone(point, width, height)
                when (zone) {
                    ScreenZone.MiddleCenter -> {
                        openMainMenu()
                    }
                }
                textViewInfoRight!!.text = resources.getString(R.string.longclick_in_zone, zone)
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
        width = pageView!!.measuredWidth - pageView!!.paddingLeft - pageView!!.paddingRight
        height = pageView!!.measuredHeight - pageView!!.paddingTop - pageView!!.paddingBottom

        book!!.loadPage(pageView!!)
        updateView()
        /*
        viewTreeObserver = pageView!!.viewTreeObserver
        viewTreeObserver!!.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                pageView!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                width = pageView!!.measuredWidth - pageView!!.paddingLeft - pageView!!.paddingRight
                height = pageView!!.measuredHeight - pageView!!.paddingTop - pageView!!.paddingBottom

                book!!.loadPage(pageView!!)
                updateView()
            }
        })
        */
    }

    private fun doPageNext() {
        if ( book == null) return
        if ( book!!.nextPage != null) {
            book!!.prevPage = book!!.curPage
            book!!.curPage = Page(book!!.nextPage!!)
            updateView()
            savePositionForBook()
            book!!.loadNextPage(pageView!!)
        }
    }

    private fun doPagePrev() {
        if ( book == null) return
        if ( book!!.prevPage != null ) {
            book!!.nextPage = book!!.curPage
            book!!.curPage = Page(book!!.prevPage!!)
            updateView()
            savePositionForBook()
            book!!.loadPrevPage(pageView!!)
        }
    }

    private fun updateInfo() {
        if ( book != null && book!!.curPage != null) {
            var curSection = book!!.curPage!!.endBookPosition.section
            curSection++

            var curPage = 0;
            for (i in 0 .. curSection-2) {
                curPage += book!!.scheme.scheme[i]!!.textPages
            }
            curPage += ( book!!.curPage!!.endBookPosition.offSet / BookScheme.CHAR_PER_PAGE )

            textViewInfoLeft!!.text =
                    resources.getString(R.string.page_info_text, curPage, book!!.scheme.textPages)

            textViewInfoCenter!!.text =
                    resources.getString(R.string.page_info_text, curSection, book!!.scheme.sectionCount)
        } else {
            textViewInfoCenter!!.text = "no Book"
        }
    }

    private fun updateView() {
        pageView!!.text = book!!.curPage?.content
        updateInfo()
    }


    private fun openMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivityForResult(intent, REQUEST_ACCESS_MAIN_MENU)
    }

    private fun openQuickMenu() {
        val quickMenuFragment: QuickMenuFragment = QuickMenuFragment.newInstance(textSize, font)
        quickMenuFragment.show(supportFragmentManager, "fragment_quick_menu")
    }

    private fun openGotoMenu() {
        if ( book == null || book!!.curPage == null) return
        val gotoMenuFragment: GotoMenuFragment =
                GotoMenuFragment
                        .newInstance(book!!.curPage!!.endBookPosition.section, book!!.scheme.sectionCount)
        gotoMenuFragment.show(supportFragmentManager, "fragment_goto_menu")
    }

    override fun onFinishQuickMenuDialog(textSize: Float, font: TypefaceRecord?) {
        Log.d(TAG, "onFinishQuickMenuDialog. TextSize: $textSize")
        this.textSize = textSize
        pageView!!.textSize = textSize
        if ( font != null) {
            this.font = font
            pageView!!.typeface = font.getTypeface()
        }
        book!!.loadPage(pageView!!)
        updateView()
        savePrefs()
    }

    override fun onChangeTextSize(textSize: Float) {
        pageView!!.textSize = textSize
        book!!.loadPage(pageView!!)
        updateView()
    }

    override fun onCancelQuickMenu() {
        pageView!!.textSize = textSize
        book!!.loadPage(pageView!!)
        updateView()
        // ... other resets
    }

    override fun onAddBookmark(): Boolean {
        val bookmarkService = BookmarkService(BookmarksDatabaseAdapter(this))
        val bookmark = Bookmark(
                path = bookPath!!,
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
        if ( bookPath == null ) return
        val bookmarkListFragment: BookmarkListFragment = BookmarkListFragment.newInstance(bookPath!!)
        bookmarkListFragment.show(supportFragmentManager, "fragment_bookmark_list")
    }

    override fun onAccessGrantedReadExternalStorage() {
        if (bookPath != null) {
            loadBook();
            loadPositionForBook();
            updateSizeInfo()
        } else  {
            Toast.makeText(applicationContext, "Open a book", Toast.LENGTH_LONG).show()
            openMainMenu()
        }
    }

    override fun onSelectBookmark(bookmark: Bookmark) {
        // go to selected bookmark
        book!!.curPage = Page(null, BookPosition(bookmark), BookPosition())
        book!!.nextPage = null
        book!!.prevPage = null
        book!!.loadPage(pageView!!)
        updateView()
    }

    override fun onFinishGotoMenuDialog(section: Int) {
        book!!.curPage = Page(null, BookPosition(section = section), BookPosition())
        book!!.nextPage = null
        book!!.prevPage = null
        book!!.loadPage(pageView!!)
        updateView()
    }

    fun setScreenBrightness(level: Float) {
        layoutpars!!.screenBrightness = level
        window.attributes = layoutpars
    }

    companion object {
        private const val TAG = "ReaderActivity"
        private const val PREFS_FILE = "MainActivitySettings"
        const val REQUEST_ACCESS_MAIN_MENU = 1
        const val REQUEST_ACCESS_QUICK_MENU = 2
        const val PREF_TYPE = "ReturnType"
        const val PREF_TYPE_OPEN_BOOK = 121
        const val PREF_BOOK_PATH = "BookPath"
        const val PREF_BOOK_TEXT_SIZE = "TextSize"
        const val PREF_BOOK_FONT_NAME = "FontName"
        const val PREF_BOOK_FONT_PATH = "FontPath"
        const val PREF_SCREENBRIGHTNESS = "screenbrightness"
    }

}