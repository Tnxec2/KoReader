package com.kontranik.koreader.reader

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.runtimepermission.PermissionResult
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.android.material.snackbar.Snackbar
import com.kontranik.koreader.R
import com.kontranik.koreader.database.BookStatusDatabaseAdapter
import com.kontranik.koreader.database.BookStatusService
import com.kontranik.koreader.model.*
import com.kontranik.koreader.reader.LoadPageAsync.AsyncResponse

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class ReaderActivity : AppCompatActivity() {

    private var book: Book? = null

    var curPage: Page = Page(null, BookPosition())
    var nextPage: Page? = null
    var prevPage: Page? = null

    var pageView: TextView? = null
    var viewTreeObserver: ViewTreeObserver? = null

    var textViewInfoCenter: TextView? = null
    var textViewInfoLeft: TextView? = null
    var textViewInfoRight: TextView? = null

    var pageViewHolderOuter: RelativeLayout? = null

    var pageSplitter: PageSplitterOne? = null
    var width: Int? = null
    var height: Int? = null

    private var settings: SharedPreferences? = null
    private var prefEditor: SharedPreferences.Editor? = null

    private var selectedPath: String? = null
    private var bookPath: String? = null

    private val REQUEST_ACCESS_TYPE = 1

    private var asyncLoadPageNext: AsyncTask<LoadPageParams, Unit, Page?>? = null
    private var asyncLoadPagePrev: AsyncTask<LoadPageParams, Unit, Page?>? = null

    private var bookStatusService: BookStatusService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_main)

        bookStatusService = BookStatusService(BookStatusDatabaseAdapter(this))

        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
        loadPrefs()

        pageView = findViewById(R.id.textView_pageview)
        TextViewInitiator.initiateTextView(pageView!!)
        setOnClickListener()

        textViewInfoCenter = findViewById(R.id.tv_infotext_center)
        textViewInfoLeft = findViewById(R.id.tv_infotext_left)
        textViewInfoRight = findViewById(R.id.tv_infotext_right)

        pageViewHolderOuter = findViewById(R.id.pageViewHolderOuter)

        checkPermissions()

        // bei jedem start - bereinigen
        bookStatusService!!.cleanup()
    }

    private fun loadBook() {
        Log.d(TAG, "loadBook: " + bookPath)
        book = Book(applicationContext, bookPath)
    }

    private fun openFileChooser() {
        val intent = Intent(this, FileChooseActivity::class.java)
        intent.putExtra(INTENT_PATH, selectedPath)
        startActivityForResult(intent, REQUEST_ACCESS_TYPE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode==REQUEST_ACCESS_TYPE ){
            if(resultCode==RESULT_OK){
                if (data != null) {
                    selectedPath = data.getStringExtra(PREF_LAST_PATH)
                    bookPath = data.getStringExtra(PREF_BOOK_PATH)
                    savePrefs()
                    loadPositionForBook()
                    loadBook()
                    loadPage()
                };
            } else{
                Log.e(TAG, "onActivityResult: access error")
            }
        } else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private fun loadPositionForBook() {
        asyncLoadPageNext?.cancel(true)
        asyncLoadPagePrev?.cancel(true)

        val startPosition = bookStatusService!!.getPosition(bookPath!!) ?: BookPosition()

        curPage = Page(null, startPosition, BookPosition())
        nextPage = null
        prevPage = null
    }

    private fun savePositionForBook() {
        if ( bookPath != null) {
            bookStatusService!!.savePosition(bookPath!!, curPage.startBookPosition)
        }
    }

    override fun onPause() {
        super.onPause()
        savePositionForBook()
    }

    private fun loadPrefs() {
        if ( settings!!.contains(PREF_LAST_PATH) ) {
            selectedPath = settings!!.getString(PREF_LAST_PATH, null)
        }
        if ( settings!!.contains(PREF_BOOK_PATH) ) {
            bookPath = settings!!.getString(PREF_BOOK_PATH, null)
        }
    }

    private fun savePrefs() {
        prefEditor = settings!!.edit()
        prefEditor!!.putString(PREF_LAST_PATH, selectedPath)
        prefEditor!!.putString(PREF_BOOK_PATH, bookPath)
        prefEditor!!.apply()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        pageView!!.setOnTouchListener(object : OnSwipeTouchListener(this@ReaderActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                textViewInfoRight!!.setText(getString(R.string.swipe_left))
                doNext()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                textViewInfoRight!!.setText(getString(R.string.swipe_right))
                doPrev()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                textViewInfoRight!!.setText(getString(R.string.swipe_up))
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                textViewInfoRight!!.setText(getString(R.string.swipe_down))
            }

            override fun onClick(point: Point) {
                super.onClick(point)
                val zone = ScreenZone.zone(point, width!!, height!!)
                textViewInfoRight!!.text = resources.getString(R.string.click_in_zone, zone)
                when (zone) {
                    ScreenZone.BottomRight -> doNext()
                    ScreenZone.BottomLeft -> doPrev()
                    else -> {
                    }
                }
            }

            override fun onDoubleClick(point: Point) {
                super.onDoubleClick(point)
                val zone = ScreenZone.zone(point, width!!, height!!)
                when (zone) {
                   // ScreenZone.MiddleCenter -> setFullScreen(!isFullScreen())
                }
                textViewInfoRight!!.setText(resources.getString(R.string.doubleclick_in_zone, zone))
            }

            override fun onLongClick(point: Point) {
                super.onLongClick(point)
                val zone = ScreenZone.zone(point, width!!, height!!)
                textViewInfoRight!!.setText(resources.getString(R.string.longclick_in_zone, zone))
                openFileChooser()
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            doNext()
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            doPrev()
        }
        return true
    }

/*    override fun onWindowFocusChanged(hasFocus: Boolean) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus)
        updateSizeInfo()
    }*/

    private fun updateSizeInfo() {
        viewTreeObserver = pageView!!.getViewTreeObserver()
        viewTreeObserver!!.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                pageView!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                width = pageView!!.measuredWidth - pageView!!.paddingLeft - pageView!!.paddingRight
                height = pageView!!.measuredHeight - pageView!!.paddingTop - pageView!!.paddingBottom

                pageSplitter = PageSplitterOne(
                        width!!, height!!, pageView!!.paint,
                        pageView!!.lineSpacingMultiplier, pageView!!.lineSpacingExtra, applicationContext)
                loadPage()
            }
        })
    }

    private fun doNext() {
        if ( book == null) return
        if ( nextPage != null) {
            prevPage = curPage
            curPage = Page(nextPage!!)
            updateView()
            savePositionForBook()
            loadNextPage()
        }
    }

    private fun doPrev() {
        if ( book == null) return
        if ( prevPage != null ) {
            nextPage = curPage
            curPage = Page(prevPage!!)
            updateView()
            savePositionForBook()
            loadPrevPage()
        }
    }

    private fun loadPage() {
        if ( book == null) return
        loadCurPage()
        updateView()
        loadNextPage()
        loadPrevPage()
    }

    private fun loadCurPage() {
        curPage = book!!.loadPage(Page(null, curPage!!.startBookPosition), pageSplitter!!)
    }

    private fun loadNextPage() {
       // nextPage = book!!.loadPage(Page(null, Cursor(curPage!!.endCursor))!!, pageSplitter!!)

        asyncLoadPageNext = LoadPageAsync(object : AsyncResponse {
            override fun processFinish(output: Page?) {
                Log.d(TAG, "next page finish")
                nextPage = output
            }
        }).execute(
                LoadPageParams(false, book, Page(null, BookPosition(curPage!!.endBookPosition)), pageSplitter))
    }

    private fun loadPrevPage(){
         //prevPage = book!!.loadPageRevers(Page(null, Cursor(), Cursor(curPage!!.startCursor)), pageSplitter!!)

        asyncLoadPagePrev = LoadPageAsync(object : AsyncResponse {
            override fun processFinish(output: Page?) {
                Log.d(TAG, "prev page finish")
                prevPage = output
            }
        }).execute(
                LoadPageParams(true, book, Page(null, BookPosition(), BookPosition(curPage!!.startBookPosition)), pageSplitter))
    }

    private fun updateInfo() {
        textViewInfoCenter!!.text =
                resources.getString(R.string.page_info_text, curPage?.startBookPosition?.page, book!!.countPages)
    }

    private fun updateView() {
        pageView!!.text = curPage.content
        updateInfo()
    }


    fun isFullScreen(): Boolean {
        return window.attributes.flags and
                WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
    }

    fun setFullScreen(full: Boolean) {
        if (full == isFullScreen()) {
            return
        }
        val window: Window = window
        if (full) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if (full) {
            actionBar?.hide()
            pageViewHolderOuter!!.setPadding(
                    resources.getDimension(R.dimen.padding_textview_left_fullscreen).toInt(),
                    resources.getDimension(R.dimen.padding_textview_top_fullscreen).toInt(),
                    resources.getDimension(R.dimen.padding_textview_right_fullscreen).toInt(),
                    resources.getDimension(R.dimen.padding_textview_bottom_fullscreen).toInt())
        } else {
            actionBar?.show()
            pageViewHolderOuter!!.setPadding(
                    resources.getDimension(R.dimen.padding_textview_left).toInt(),
                    resources.getDimension(R.dimen.padding_textview_top).toInt(),
                    resources.getDimension(R.dimen.padding_textview_right).toInt(),
                    resources.getDimension(R.dimen.padding_textview_bottom).toInt())
        }
        updateSizeInfo()
    }

    private fun checkPermissions() {
        RuntimePermission.askPermission(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .onAccepted { result: PermissionResult? ->
                    //all permissions already granted or just granted

                    // test
                    //openReader( "/storage/emulated/0/Books/test.epub");
                    //openReader("/mnt/sdcard/Download/test.epub")
                    //
                    if ( bookPath != null) { loadBook(); loadPositionForBook() ; updateSizeInfo() }
                    else openFileChooser()
                }
                .onDenied { result: PermissionResult ->
                    Snackbar.make(pageView!!, getString(R.string.permissions_needed), Snackbar.LENGTH_SHORT).show()
                    //permission denied, but you can ask again, eg:
                    AlertDialog.Builder(this)
                            .setMessage(this.getString(R.string.give_permission_storage))
                            .setPositiveButton(this.getString(R.string.okay_string)) { dialog: DialogInterface?, which: Int -> result.askAgain() } // ask again
                            .setNegativeButton(this.getString(R.string.no_string)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                            .show()
                }
                .onForeverDenied { result: PermissionResult ->
                    Snackbar.make(pageView!!, getString(R.string.permissions_needed), Snackbar.LENGTH_SHORT)
                            .setAction(this.getString(R.string.go_to_settings)) { view: View? -> result.goToSettings() }.show()
                }
                .ask()
    }

    companion object {
        private const val TAG = "ReaderActivity"
        private const val PREFS_FILE = "MainActivitySettings"
        const val INTENT_PATH = "IntentPath"
        const val PREF_LAST_PATH = "LastPath"
        const val PREF_BOOK_PATH = "BookPath"
    }
}