package com.kontranik.koreader.ui.components

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.text.Spannable
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.kontranik.koreader.model.PageViewSettings
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.utils.OnSwipeTouchListener
import com.kontranik.koreader.utils.PrefsHelper

class BookReaderTextview: AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet?) :
        this(context, attrs, R.attr.textViewStyle)

    constructor(context: Context) : super(context)


    private var listener: BookReaderTextviewListener? = null

    init {
        initUi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        setOnTouchListener(object :
            OnSwipeTouchListener(context) {
                override fun onClick(point: Point) {
                    super.onClick(point)

                    // Find the URL that was pressed
                    val off = getClickedOffset(point)
                    val spannable = text as Spannable
                    val link = spannable.getSpans(off, off, URLSpan::class.java)
                    if (link.isNotEmpty()) {
                        // link clicked
                        listener?.onClickLinkOnBookReaderTextview(link[0].url)
                    } else {
                        // not a link, normal click
                        checkTap(point, TapType.OneTap)
                    }
                }

                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    listener?.onSwipeLeftOnBookReaderTextview()
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    listener?.onSwipeRightOnBookReaderTextview()
                }

                override fun onSlideUp(point: Point) {
                    super.onSlideUp(point)
                    listener?.onSlideUpOnBookReaderTextView(point)
                }

                override fun onSlideDown(point: Point) {
                    super.onSlideDown(point)
                    listener?.onSlideDownOnBookReaderTextView(point)
                }

                override fun onDoubleClick(point: Point) {
                    //super.onDoubleClick(point)
                    checkTap(point, TapType.DoubleTap)
                }

                override fun onLongClick(point: Point) {
                    super.onLongClick(point)

                    // Find the Image that was pressed
                    val off = getClickedOffset(point)
                    val spannable = text as Spannable
                    val image = spannable.getSpans(off, off, ImageSpan::class.java)
                    if (image.isNotEmpty()) {
                        listener?.onClickImageOnBookReaderTextview(image[0])
                    } else {
                        checkTap(point, TapType.LongTap)
                    }
                }
            }
        )
    }

    private fun getClickedOffset(point: Point): Int {
        // check if Link or image clicked
        var x = point.x
        var y = point.y
        x -= totalPaddingLeft
        y -= totalPaddingTop
        x += scrollX
        y += scrollY
        // Locate the clicked span
        val layout = layout
        val line = layout.getLineForVertical(y)
        return layout.getOffsetForHorizontal(line, x.toFloat())
    }

    private fun checkTap(point: Point, tapType: OnSwipeTouchListener.TapType) {
        val zone = getZone(point)

        when (tapType) {
            OnSwipeTouchListener.TapType.OneTap -> { // double tap
                listener?.onTabActionOnBookReaderTextview(PrefsHelper.tapOneAction[zone])
            }
            OnSwipeTouchListener.TapType.DoubleTap -> { // double tap
                listener?.onTabActionOnBookReaderTextview(PrefsHelper.tapDoubleAction[zone])
            }
            OnSwipeTouchListener.TapType.LongTap -> { // long tap
                listener?.onTabActionOnBookReaderTextview(PrefsHelper.tapLongAction[zone])
            }
        }
    }

    private fun getZone(point: Point): ScreenZone {
        return ScreenZone.zone(point, measuredWidth - paddingLeft - paddingRight, measuredHeight - paddingTop - paddingBottom)
    }

    interface BookReaderTextviewListener {
        fun onClickLinkOnBookReaderTextview(url: String)
        fun onTabActionOnBookReaderTextview(tapAction: String?)
        fun onSwipeLeftOnBookReaderTextview()
        fun onSwipeRightOnBookReaderTextview()
        fun onSlideUpOnBookReaderTextView(point: Point)
        fun onSlideDownOnBookReaderTextView(point: Point)
        fun onClickImageOnBookReaderTextview(imageSpan: ImageSpan)
    }

    fun setListener(listener: BookReaderTextviewListener) {
        this.listener  = listener
    }

    fun changeSettings(pageViewSettings: PageViewSettings) {
        textSize = pageViewSettings.textSize
        letterSpacing = pageViewSettings.letterSpacing
        typeface = pageViewSettings.typeFace
        setLineSpacing(
            lineSpacingExtra,
            pageViewSettings.lineSpacingMultiplier
        )
        val density = this.resources.displayMetrics.density
        val marginTopPixel = (pageViewSettings.marginTop * density).toInt()
        val marginBottomPixel = (pageViewSettings.marginBottom * density).toInt()
        val marginLeftPixel = (pageViewSettings.marginLeft * density).toInt()
        val marginRightPixel = (pageViewSettings.marginRight * density).toInt()
        setPadding(
            marginLeftPixel,
            marginTopPixel,
            marginRightPixel,
            marginBottomPixel
        )
    }
}