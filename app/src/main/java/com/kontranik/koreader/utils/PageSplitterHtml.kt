package com.kontranik.koreader.utils

import android.text.*
import android.text.style.QuoteSpan
import android.util.TypedValue
import androidx.compose.ui.graphics.toArgb
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.compose.ui.settings.ThemeColors
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.model.PageViewSettings

open class PageSplitterHtml() : FontsHelper() {

    var pages: MutableList<Page> = mutableListOf()

    private var content: SpannableStringBuilder = SpannableStringBuilder()
    private var staticLayout: StaticLayout? = null

    private var marginTop: Int = 0
    private var marginBottom: Int = 0
    private var marginLeft: Int = 0
    private var marginRight: Int = 0

    fun splitPages(
        pageViewSettings: PageViewSettings,
        themeColors: ThemeColors,
        book: Book,
        section: Int,
        html: String,
        reloadFonts: Boolean) {
        println("PageSplitterHtml2.pageLoaderToken: ${pageViewSettings.pageSize}")

        if (pageViewSettings.pageSize.width <= 0) return
        if ( reloadFonts ) loadFonts()

        val density = KoReaderApplication.getContext().resources.displayMetrics.density
        marginTop = (pageViewSettings.marginTop * density).toInt()
        marginBottom = (pageViewSettings.marginBottom * density).toInt()
        marginRight = (pageViewSettings.marginRight * density).toInt()
        marginLeft = (pageViewSettings.marginLeft * density).toInt()

        val painter = TextPaint().apply {
            isAntiAlias = true
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                pageViewSettings.textSize,
                KoReaderApplication.getContext().resources.displayMetrics
            );
            color = themeColors.colorsText.toArgb()
            letterSpacing = pageViewSettings.letterSpacing
        }
        content =
            SpannableStringBuilder(
                Html.fromHtml(
                    html, Html.FROM_HTML_MODE_COMPACT,
                    CustomImageGetter(
                        book,
                        pageViewSettings.pageSize.width - marginRight - marginLeft,
                        pageViewSettings.pageSize.height - marginTop - marginBottom,
                        painter.color,
                        section > 0),
                    null
                )
            )

        postFormatContent(painter)

        staticLayout =
            StaticLayout.Builder.obtain(
                content,
                0,
                content.length,
                painter,
                pageViewSettings.pageSize.width - (marginRight + marginLeft)
            )
            .setLineSpacing(1f, pageViewSettings.lineSpacingMultiplier)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .build()

        pages.clear()

        staticLayout?.let { layout ->
            var startLine = 0
            var endLine: Int
            var startLineTop: Int
            var endLineBottom: Int
            var startOffset: Int
            var endOffset: Int
            var pageIndex = 0
            while (true) {
                startLineTop = layout.getLineTop(startLine)
                endLine = layout.getLineForVertical(startLineTop + pageViewSettings.pageSize.height - (marginRight + marginLeft))
                endLineBottom = layout.getLineBottom(endLine)
                var lastFullyVisibleLine = if (endLineBottom >  startLineTop + pageViewSettings.pageSize.height - marginTop - marginBottom)
                    endLine - 1 else endLine
                if ( lastFullyVisibleLine < startLine) lastFullyVisibleLine = startLine
                startOffset = layout.getLineStart(startLine)
                endOffset = layout.getLineEnd(lastFullyVisibleLine)

                pages.add(
                    Page(content = SpannableStringBuilder()
                            .append(content.subSequence(startOffset, endOffset)),
                        pageStartPosition = BookPosition(section = section, offSet = startOffset),
                        pageEndPosition = BookPosition(section = section, offSet = endOffset),
                        pageIndex = pageIndex
                ))
                pageIndex+=1

                if ( endLine >= layout.lineCount-1 ) break
                startLine = lastFullyVisibleLine + 1
            }
        }
    }

    private fun postFormatContent(painter: TextPaint) {
        formatQuotes(painter.color)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            content = SpannableStringBuilder(replaceStyledSpans(content, content.length))
            content = SpannableStringBuilder(replaceTypefaces(content))
        }
        //formatImages()
    }

/*    private fun formatImages() {
        for ( imageSpan in content.getSpans(0, content.length, ImageSpan::class.java)) {
            // get the span range
            val start: Int = content.getSpanStart(imageSpan)
            val end: Int = content.getSpanEnd(imageSpan)

            val clickableSpan = object : ClickableSpan(){
                override fun onClick(view: View) {

                }
            }

            // add an additional clickable span in the same place
            content.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }*/

    private fun formatQuotes(quoteColor: Int) {
        val newQuoteSpan = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            QuoteSpan(quoteColor, 5, 10)
        } else {
            QuoteSpan(quoteColor)
        }
        for ( quoteSpan in content.getSpans(0, content.length, QuoteSpan::class.java)) {
            // get the span range
            val start: Int = content.getSpanStart(quoteSpan)
            val end: Int = content.getSpanEnd(quoteSpan)

            // remove old span
            content.removeSpan(quoteSpan)

            // add an new QuoteSpan span in the same place
            content.setSpan(newQuoteSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}



