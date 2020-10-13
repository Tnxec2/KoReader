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
import com.kontranik.koreader.model.MyStyle
import com.kontranik.koreader.model.Word
import com.kontranik.koreader.test.PageSplitter3
import org.jsoup.Jsoup

@RequiresApi(api = Build.VERSION_CODES.Q)
class PageSplitterActivity : FragmentActivity() {

    private var pagesView: ViewPager? = null
    private var book: Book? = null

    var pageSplitter: PageSplitter3? = null
    var textPageAdapter: TextPagerAdapter? = null
    var lastPageReached = false
    var firstPageReached = false
    var testView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagesplitter_main)
        pagesView = findViewById<View>(R.id.pages) as ViewPager

        testView = findViewById<View>(R.id.textView_test) as TextView
        TextViewInitiator.initiateTextView(testView!!)

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
                            Log.d(TAG, "lastpagereached: $lastPageReached")
                            Log.d(TAG, "firstPageReached: $lastPageReached")
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
                    book!!.currentPageNumber = 4
                    val pageHtml = book!!.getPage()
                    loadPage(pageHtml)
                    pagesView!!.setCurrentItem(0, false)
                }
            }
        })
    }

    fun updateViewpager() {
        textPageAdapter = TextPagerAdapter(
                supportFragmentManager,
                pageSplitter!!.getPages()) // old items with new items

        pagesView!!.adapter = textPageAdapter
        //pagesView.setCurrentItem(0, false);
        //textPageAdapter.refreshAdapter();
    }

    fun loadPage(page: String?) {
        val width = pagesView!!.measuredWidth - (pagesView!!.paddingLeft + pagesView!!.paddingRight)
        val height = pagesView!!.measuredHeight - (pagesView!!.paddingTop + pagesView!!.paddingBottom)
        pageSplitter = PageSplitter3(
                width, height, 1.0f, 0f
        )
        appendPage(page)
    }

    fun appendPage(page: String?) {
        val document = Jsoup.parse(page)
        val elements = document.body().select("*")
        for (element in elements) {
            val el = element.normalName()

            //Log.d(TAG, el);
            //Log.d(TAG, element.ownText());
            val myStyle = MyStyle.getFromString(el)
            if (myStyle == MyStyle.None) {
                continue
            } else if (myStyle == MyStyle.Other) {
                pageSplitter!!.append(Word("$el::", MyStyle.Title))
                pageSplitter!!.append(Word(element.ownText(), MyStyle.Paragraph))
            } else {
                pageSplitter!!.append(Word(element.ownText(), myStyle))
            }
            pageSplitter!!.newLine();
            //Log.d(TAG, "pageSplitter size: " + pageSplitter.getPages().size());
        }
        pageSplitter!!.split(testView!!.paint)
        updateViewpager()
    }

    fun loadNextBookPage() {
        if (book!!.currentPageNumber > book!!.countPages - 1) return
        val oldPage = book!!.getPage()
        loadPage(oldPage)
        val lastPage = pageSplitter!!.getPages().size - 1
        book!!.currentPageNumber++
        val bookPage = book!!.getPage()
        appendPage(bookPage)
        pagesView!!.setCurrentItem(lastPage, false) // set pager to last page
    }

    fun nextBookPage() {
        if (book!!.currentPageNumber > book!!.countPages - 1) return
        book!!.currentPageNumber++
        val bookPage = book!!.getPage()
        loadPage(bookPage)
        pagesView!!.setCurrentItem(0, true)
        lastPageReached = false
        firstPageReached = true
    }

    fun prevBookPage() {
        if (book!!.currentPageNumber == 1) return
        book!!.currentPageNumber--
        val bookPage = book!!.getPage()
        loadPage(bookPage)
        pagesView!!.setCurrentItem(pagesView!!.adapter!!.count - 1, true)
        firstPageReached = false
        lastPageReached = true
    }

    companion object {
        private const val TAG = "PageSplitterActivity"
        const val INTENT_PATH = "BookPath"
        val PATH = Environment.getExternalStorageDirectory().absolutePath + "/Books/"
    }
}