package com.kontranik.koreader.compose.ui.reader


import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.SpannedString
import android.text.style.CharacterStyle
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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

    private val borderForSelectableMode = GradientDrawable().apply {
        this.setStroke(3, Color.RED)
    }

    private var selectionModus = false

    private val mTouchListener = object :
        OnSwipeTouchListener(context) {
        override fun onClick(point: Point) {
            super.onClick(point)
            try {
                // Find the URL that was pressed
                val off = getClickedOffset(point)
                val spannable = if (text.javaClass == SpannedString::class.java)
                    text as SpannedString
                else
                    text as SpannableString
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

            try {
                // Find the Image that was pressed
                val off = getClickedOffset(point)
                val spannable = if (text.javaClass == SpannedString::class.java)
                    text as SpannedString
                else
                    text as SpannableString
                val image = spannable.getSpans(off, off, ImageSpan::class.java)
                if (image.isNotEmpty()) {
                    listener?.onClickImageOnBookReaderTextview(image[0])
                } else {
                    // checkTap(point, TapType.LongTap)
                    switchToSelectionMode()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    init {
        initUi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {

        isFocusable = true

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

        setOnTouchListener(
            mTouchListener
        )
    }

    fun switchToSelectionMode() {
        selectionModus = true

        background = borderForSelectableMode
        setOnTouchListener(null)

        setTextIsSelectable(true)

        customSelectionActionModeCallback = TextCallback(this)
    }

    fun switchToNormalMode() {
        selectionModus = false

        background = null
        customSelectionActionModeCallback = null

        setOnTouchListener(mTouchListener)
        setTextIsSelectable(false)
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
        fun onAddBookmark(start: Int, end: Int, text: CharSequence)
    }

    fun setListener(listener: BookReaderTextviewListener) {
        this.listener  = listener
    }

    fun removeListener() {
        this.listener = null
    }

    private fun changeSettings(pageViewSettings: PageViewSettings) {
        textSize = pageViewSettings.textSize
        letterSpacing = pageViewSettings.letterSpacing
        typeface = pageViewSettings.typeFace
        setLineSpacing(
            1f,
            pageViewSettings.lineSpacingMultiplier,
        )
    }

    class TextCallback(private val textview: BookReaderTextview) : android.view.ActionMode.Callback {
        private val TAG = "TextCallback"

        override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
            menu?.let {
                try {
                    val copyItem = it.findItem(R.id.copy)
                    val shareText = it.findItem(R.id.shareText)

                    it.clear()
                    it.add(0, com.kontranik.koreader.R.id.textmenu_addbookmark, 0, com.kontranik.koreader.R.string.add_bookmark)
                    it.add(0, com.kontranik.koreader.R.id.textmenu_cancel, 0, "Cancel selection mode")
                    it.add(0, R.id.copy, 0, copyItem.title)
                    it.add(0, R.id.shareText, 0, shareText.title)
                } catch (e: java.lang.Exception) {
                    // ignored
                }
            }
            return false
        }

        override fun onActionItemClicked(mode: android.view.ActionMode?, item: MenuItem?): Boolean {
            item?.let {
                Log.d(
                    TAG,
                    String.format("onActionItemClicked item=%s/%d", it.toString(), it.itemId)
                )

                when(it.itemId) {
                    com.kontranik.koreader.R.id.textmenu_addbookmark -> {
                        val start: Int = textview.selectionStart
                        val end: Int = textview.selectionEnd
                        println("start: $start, end: $end, text: ${textview.text.length}")
                        val ssb = textview.text.subSequence(start, end)
                        textview.listener?.onAddBookmark(
                            start, end, ssb
                        )
                        textview.switchToNormalMode()
                        return true
                    }

                    com.kontranik.koreader.R.id.textmenu_cancel -> {
                        textview.switchToNormalMode()
                        return true
                    }
                    else  -> {
                        return false
                    }
                }
            }

            return false
        }

        override fun onDestroyActionMode(p0: android.view.ActionMode?) {

        }
    }
}