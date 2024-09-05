package com.kontranik.koreader.compose.ui.reader


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.text.SpannedString
import android.text.style.ImageSpan
import android.text.style.URLSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LifecycleOwner
import com.kontranik.koreader.model.PageViewSettings
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.utils.OnSwipeTouchListener

class BookReaderTextview(
    context: Context,
    val bookReaderViewModel: BookReaderViewModel):
    AppCompatTextView(context) {

    private var listener: BookReaderTextviewListener? = null

    init {
        initUi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {

        bookReaderViewModel.pageViewContent.observe(context as LifecycleOwner) {
            text = it
        }

        bookReaderViewModel.themeColors.observe(context as LifecycleOwner) {
            setTextColor(it.colorsText.toArgb())
            setLinkTextColor(it.colorsLink.toArgb())
        }
        bookReaderViewModel.pageViewSettings.observe(context as LifecycleOwner) {
            changeSettings(it)
        }

        setOnTouchListener(object :
            OnSwipeTouchListener(context) {
                override fun onClick(point: Point) {
                    super.onClick(point)
                    try {
                        // Find the URL that was pressed
                        val off = getClickedOffset(point)
                        val spannable = text as SpannedString
                        val link = spannable.getSpans(off, off, URLSpan::class.java)
                        if (link.isNotEmpty()) {
                            // link clicked
                            listener?.onClickLinkOnBookReaderTextview(link[0].url)
                        } else {
                            // not a link, normal click
                            checkTap(point, TapType.OneTap)
                        }
                    } catch (e: Exception) {
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
                    val spannable = text as SpannedString
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
                listener?.onTapOnBookReaderTextview(zone)
            }
            OnSwipeTouchListener.TapType.DoubleTap -> { // double tap
                listener?.onDoubleTapOnBookReaderTextview(zone)
            }
            OnSwipeTouchListener.TapType.LongTap -> { // long tap
                listener?.onLongTapOnBookReaderTextview(zone)
            }
        }
    }

    private fun getZone(point: Point): ScreenZone {
        return ScreenZone.zone(point, measuredWidth - paddingLeft - paddingRight, measuredHeight - paddingTop - paddingBottom)
    }

    interface BookReaderTextviewListener {
        fun onClickLinkOnBookReaderTextview(url: String)
        fun onTapOnBookReaderTextview(zone: ScreenZone)
        fun onDoubleTapOnBookReaderTextview(zone: ScreenZone)
        fun onLongTapOnBookReaderTextview(zone: ScreenZone)
        fun onSwipeLeftOnBookReaderTextview()
        fun onSwipeRightOnBookReaderTextview()
        fun onSlideUpOnBookReaderTextView(point: Point)
        fun onSlideDownOnBookReaderTextView(point: Point)
        fun onClickImageOnBookReaderTextview(imageSpan: ImageSpan)
    }

    fun setListener(listener: BookReaderTextviewListener) {
        this.listener  = listener
    }

    fun removeListener() {
        this.listener = null
    }

    fun changeSettings(pageViewSettings: PageViewSettings) {
        textSize = pageViewSettings.textSize
        letterSpacing = pageViewSettings.letterSpacing
        typeface = pageViewSettings.typeFace
        setLineSpacing(
            1f,
            pageViewSettings.lineSpacingMultiplier,
        )
    }
}