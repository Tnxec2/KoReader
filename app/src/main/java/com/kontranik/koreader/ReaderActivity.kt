package com.kontranik.koreader

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kontranik.koreader.database.BookStatusDatabaseAdapter
import com.kontranik.koreader.database.BookStatusService
import com.kontranik.koreader.database.BookmarkService
import com.kontranik.koreader.database.BookmarksDatabaseAdapter
import com.kontranik.koreader.model.*
import com.kontranik.koreader.reader.*
import com.kontranik.koreader.reader.OnSwipeTouchListener
import com.kontranik.koreader.utils.PermissionsHelper
import java.util.*

class ReaderActivity :
        AppCompatActivity(),
        QuickMenuFragment.QuickMenuDialogListener,
        PermissionsHelper.PermissionsHelperListener,
        BookmarkListFragment.BookmarkListDialogListener
{

    private var book: Book? = null

    private var pageView: TextView? = null
    private var viewTreeObserver: ViewTreeObserver? = null

    private var textViewInfoCenter: TextView? = null
    private var textViewInfoLeft: TextView? = null
    private var textViewInfoRight: TextView? = null

    private var pageViewHolderOuter: RelativeLayout? = null

    var width: Int? = null
    var height: Int? = null
    private var textSize: Float = 0f
    private var defaultTextSize: Float = 0f

    private var settings: SharedPreferences? = null
    private var prefEditor: SharedPreferences.Editor? = null

    private var bookPath: String? = null

    private var bookStatusService: BookStatusService? = null

    private var backButtonPressedTime = Date().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reader_main)

        bookStatusService = BookStatusService(BookStatusDatabaseAdapter(this))

        defaultTextSize = resources.getDimension(R.dimen.text_size)
        textSize = defaultTextSize

        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
        loadPrefs()

        pageView = findViewById(R.id.textView_pageview)
        TextViewInitiator.initiateTextView(pageView!!, "")
        setOnClickListener()
        pageView!!.textSize = textSize

        val callback = CustomActionModeCallback(this)
        pageView!!.customSelectionActionModeCallback = callback

        textViewInfoCenter = findViewById(R.id.tv_infotext_center)
        textViewInfoLeft = findViewById(R.id.tv_infotext_left)
        textViewInfoRight = findViewById(R.id.tv_infotext_right)

        pageViewHolderOuter = findViewById(R.id.pageViewHolderOuter)

        val ph = PermissionsHelper(this)
        ph.checkPermissions(pageView!!)

        // bei jedem start - bereinigen
        bookStatusService!!.cleanup()
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
        if( requestCode== REQUEST_ACCESS_MAIN_MENU ){
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

    private fun loadPrefs() {
        if ( settings!!.contains(PREF_BOOK_PATH) ) {
            bookPath = settings!!.getString(PREF_BOOK_PATH, null)
        }
        if ( settings!!.contains(PREF_BOOK_TEXT_SIZE) ) {
            textSize = settings!!.getFloat(PREF_BOOK_TEXT_SIZE, defaultTextSize)
        }
    }

    private fun savePrefs() {
        prefEditor = settings!!.edit()
        prefEditor!!.putString(PREF_BOOK_PATH, bookPath)
        prefEditor!!.putFloat(PREF_BOOK_TEXT_SIZE, textSize)
        prefEditor!!.apply()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {

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

            override fun onClick(point: Point) {
                super.onClick(point)
                if (ScreenZone != null) {
                    val zone = ScreenZone.zone(point, width!!, height!!)
                    textViewInfoRight!!.text = resources.getString(R.string.click_in_zone, zone)
                    when (zone) {
                        ScreenZone.BottomRight -> doPageNext()
                        ScreenZone.BottomLeft -> doPagePrev()
                        else -> {
                        }
                    }
                }
            }

            override fun onDoubleClick(point: Point) {
                super.onDoubleClick(point)
                val zone = ScreenZone.zone(point, width!!, height!!)
                when (zone) {
                    ScreenZone.MiddleCenter -> {
                        openMainMenu()
                    }
                }
                textViewInfoRight!!.setText(resources.getString(R.string.doubleclick_in_zone, zone))
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)
                val zone = ScreenZone.zone(point, width!!, height!!)
                textViewInfoRight!!.text = resources.getString(R.string.longclick_in_zone, zone)
                openQuickMenu()
            }


            fun onLongClick() {}
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
            var curPage = book!!.curPage!!.startBookPosition.page
            curPage++
            textViewInfoCenter!!.text =
                    resources.getString(R.string.page_info_text, curPage, book!!.schema.pageCount)
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
        val quickMenuFragment: QuickMenuFragment = QuickMenuFragment.newInstance(textSize)
        quickMenuFragment.show(supportFragmentManager, "fragment_quick_menu")
    }

    override fun onFinishQuickMenuDialog(textSize: Float) {
        Log.d(TAG, "onFinishQuickMenuDialog. TextSize: $textSize")
        this.textSize = textSize
        pageView!!.textSize = textSize
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
                position_page = book!!.curPage!!.startBookPosition.page,
                position_element = book!!.curPage!!.startBookPosition.element,
                position_paragraph = book!!.curPage!!.startBookPosition.paragraph,
                position_symbol = book!!.curPage!!.startBookPosition.symbol
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

    override fun onAccessGranted() {
        // test
        //openReader( "/storage/emulated/0/Books/test.epub");
        //openReader("/mnt/sdcard/Download/test.epub")
        //
        if ( bookPath != null) { loadBook(); loadPositionForBook() ; updateSizeInfo() }
        else  {
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

    companion object {
        private const val TAG = "ReaderActivity"
        private const val PREFS_FILE = "MainActivitySettings"
        const val REQUEST_ACCESS_MAIN_MENU = 1
        const val REQUEST_ACCESS_QUICK_MENU = 2
        const val PREF_TYPE = "ReturnType"
        const val PREF_TYPE_OPEN_BOOK = 121
        const val PREF_BOOK_PATH = "BookPath"
        const val PREF_BOOK_TEXT_SIZE = "TextSize"
    }


}