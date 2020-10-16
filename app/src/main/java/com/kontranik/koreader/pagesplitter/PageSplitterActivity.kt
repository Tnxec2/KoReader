package com.kontranik.koreader.pagesplitter

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.kontranik.koreader.R
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.Cursor
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.pagesplitter.LoadPageAsync.AsyncResponse
import com.kontranik.koreader.test.PageSplitterOne


@RequiresApi(api = Build.VERSION_CODES.Q)
class PageSplitterActivity : FragmentActivity() {

    private var book: Book? = null

    var curPage: Page? = null
    var nextPage: Page? = null
    var prevPage: Page? = null

    var pageView: TextView? = null
    var textViewInfoCenter: TextView? = null
    var textViewInfoLeft: TextView? = null
    var textViewInfoRight: TextView? = null

    var pageSplitter: PageSplitterOne? = null
    var width: Int? = null
    var height: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagesplitter_main)

        pageView = findViewById<View>(R.id.textView_pageview) as TextView
        TextViewInitiator.initiateTextView(pageView!!)

        textViewInfoCenter = findViewById(R.id.tv_infotext_center)
        textViewInfoLeft = findViewById(R.id.tv_infotext_left)
        textViewInfoRight = findViewById(R.id.tv_infotext_right)


        val arguments = intent.extras
        if (arguments == null || !arguments.containsKey(INTENT_PATH)) {
            finish()
        }
        book = Book(applicationContext, arguments!!.getString(INTENT_PATH))
        if ( book?.fileLocation != null) {
            Log.d(TAG, book?.fileLocation)


            val vto = pageView!!.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    pageView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    width = pageView!!.measuredWidth
                    height = pageView!!.measuredHeight

                    pageSplitter = PageSplitterOne(width!!, height!!, pageView!!.paint, pageView!!.lineSpacingMultiplier, pageView!!.lineSpacingExtra)

                    curPage = book!!.loadPage(Page(null, Cursor(0, 0, 0, 0)), pageSplitter!!)
                    loadNextPage()

                    updateView()
                    setOnClickListener()
                }
            })

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        pageView!!.setOnTouchListener(object : OnSwipeTouchListener(this@PageSplitterActivity) {
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
                    ScreenZone.MiddleCenter -> setFullScreen(!isFullScreen())
                }
            }
        })
    }


    private fun doNext() {
        if ( nextPage != null) {
            prevPage = curPage
            curPage = nextPage
            updateView()
            loadNextPage()
        }
    }

    private fun doPrev() {
        if ( prevPage != null ) {
            nextPage = curPage
            curPage = prevPage
            updateView()
            loadPrevPage()
        }
    }

    private fun loadNextPage() {
        //nextPage = book!!.loadPage(Page(null, Cursor(curPage!!.endCursor))!!, pageSplitter!!)

        LoadPageAsync(object : AsyncResponse {
            override fun processFinish(output: Page?) {
                Log.d(TAG, "next page finish")
                nextPage = output
            }
        }).execute(
                LoadPageParams(false, book, Page(null, Cursor(curPage!!.endCursor)), pageSplitter))
    }

    private fun loadPrevPage(){
        //prevPage = book!!.loadPageRevers(Page(null, Cursor(), Cursor(curPage!!.startCursor)), pageSplitter!!)
         LoadPageAsync(object : AsyncResponse {
             override fun processFinish(output: Page?) {
                 Log.d(TAG, "prev page finish")
                 prevPage = output
             }
         }).execute(
                 LoadPageParams(true, book, Page(null, Cursor(), Cursor(curPage!!.startCursor)), pageSplitter))
    }

    private fun updateInfo() {
        textViewInfoCenter!!.text =
                resources.getString(R.string.page_info_text, curPage?.startCursor?.bookPage, book!!.countPages)
    }

    private fun updateView() {
        pageView!!.text = curPage!!.content

        updateInfo()
    }

    companion object {
        private const val TAG = "PageSplitterActivity"
        const val INTENT_PATH = "BookPath"
        val PATH = Environment.getExternalStorageDirectory().absolutePath + "/Books/"
    }

    fun isFullScreen(): Boolean {
        return window.attributes.flags and
                WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
    }

    @SuppressLint("NewApi")
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
        if (Build.VERSION.SDK_INT >= 11) {
            if (full) {
                actionBar?.hide()
            } else {
                actionBar?.show()
            }
        }
    }
}