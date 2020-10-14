package com.kontranik.koreader.pagesplitter

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.kontranik.koreader.R
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.Cursor
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.test.PageSplitterOne

@RequiresApi(api = Build.VERSION_CODES.Q)
class PageSplitterActivity : FragmentActivity() {

    private var pagesView: ViewPager? = null
    private var book: Book? = null

    var curPage: Page? = null
    var nextPage: Page? = null
    var prevPage: Page? = null
    var pages: MutableList<Page> = mutableListOf()
    var oldPage: Int = 0

    var textPageAdapter: TextPagerAdapter? = null
    var lastPageReached = false
    var firstPageReached = false
    var testView: TextView? = null
    var textViewInfo: TextView? = null

    var pageSplitter: PageSplitterOne? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagesplitter_main)
        pagesView = findViewById<View>(R.id.pages) as ViewPager

        testView = findViewById<View>(R.id.textView_test) as TextView
        TextViewInitiator.initiateTextView(testView!!)

        textViewInfo = findViewById(R.id.tv_infotext)

        // to get ViewPager width and height we have to wait global layout
        val vto = pagesView!!.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                pagesView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                pagesView!!.setCurrentItem(0, false)
                pagesView!!.addOnPageChangeListener(object : OnPageChangeListener {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                    override fun onPageSelected(position: Int) {
                        Log.d(TAG, "onPageSelected: $position")
                        loadPages(position)
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        Log.d(TAG, "state: " + state + ", currentItem: " + pagesView!!.currentItem + ", last: " + (pagesView!!.adapter!!.count - 1))
                        if (ViewPager.SCROLL_STATE_IDLE == state) {
                            if (pagesView!!.currentItem == pagesView!!.adapter!!.count - 1) {
                                //loadNextBookPage();
                            } else if (pagesView!!.currentItem == 0) {
                                if (firstPageReached) {
                                    //prevBookPage();
                                } else {
                                    firstPageReached = true
                                }
                            }
                        }
                    }
                })

                val arguments = intent.extras
                if (arguments == null || !arguments.containsKey(INTENT_PATH)) {
                    finish()
                }
                book = Book(arguments!!.getString(INTENT_PATH))
                if ( book?.fileLocation != null) {
                    Log.d(TAG, book?.fileLocation)

                    val width = pagesView!!.measuredWidth - (pagesView!!.paddingLeft + pagesView!!.paddingRight)
                    val height = pagesView!!.measuredHeight - (pagesView!!.paddingTop + pagesView!!.paddingBottom)


                    pageSplitter = PageSplitterOne( width, height, testView!!.paint, testView!!.lineSpacingMultiplier, testView!!.lineSpacingExtra)

                    curPage = book!!.loadPage(Page(null, Cursor(1, 0, 0, 0)), pageSplitter!!)
                    nextPage = loadNextPage()

                    updateViewpager()

                    pagesView!!.setCurrentItem(0, false)
                }
            }
        })
    }

    private fun loadPages(newPage: Int) {
        if ( newPage > oldPage) doNext()
        else if ( newPage < oldPage ) doPrev()
        oldPage = newPage
    }

    private fun doNext() {
        if ( nextPage != null) {
            prevPage = curPage
            curPage = nextPage
            nextPage = loadNextPage()
            updateViewpager()
        }
    }

    private fun doPrev() {
        if ( prevPage != null ) {
            nextPage = curPage
            curPage = prevPage
            prevPage = loadPrevPage()
            updateViewpager()
        }
    }

    private fun loadNextPage(): Page? {
        return book!!.loadPage(Page(null, Cursor(curPage!!.endCursor)), pageSplitter!!)
    }

    private fun loadPrevPage(): Page? {
        return book!!.loadPageRevers(Page(null, Cursor(curPage!!.endCursor)), pageSplitter!!)
    }

    fun updateInfo() {
        textViewInfo!!.setText("${curPage?.startCursor?.bookPage} / ${book!!.countPages}")
    }

    fun updateViewpager() {
        pages = mutableListOf( curPage!!)
        if ( prevPage != null ) pages.add(0, prevPage!!)
        if ( nextPage != null ) pages.add(nextPage!!)

        textPageAdapter = TextPagerAdapter(
                supportFragmentManager,
                pages) // old items with new items

        pagesView!!.adapter = textPageAdapter

        if ( prevPage == null) pagesView!!.setCurrentItem(0, false)
        else pagesView!!.setCurrentItem(1, false)
        //textPageAdapter.refreshAdapter();
        updateInfo()
    }

    companion object {
        private const val TAG = "PageSplitterActivity"
        const val INTENT_PATH = "BookPath"
        val PATH = Environment.getExternalStorageDirectory().absolutePath + "/Books/"
    }
}