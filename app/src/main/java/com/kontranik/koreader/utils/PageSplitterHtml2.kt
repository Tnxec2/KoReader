package com.kontranik.koreader.utils

import android.text.*
import android.text.style.QuoteSpan
import com.kontranik.koreader.compose.ui.reader.PageLoaderToken
import com.kontranik.koreader.model.Book2
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page

open class PageSplitterHtml2() : FontsHelper() {

    var pages: MutableList<Page> = mutableListOf()

    private var content: SpannableStringBuilder = SpannableStringBuilder()
    private var staticLayout: StaticLayout? = null

    fun splitPages(painter: TextPaint,
                   pageLoaderToken: PageLoaderToken,
                   book: Book2,
                   section: Int,
                   html: String,
                   reloadFonts: Boolean) {
        println("PageSplitterHtml2.pageLoaderToken: ${pageLoaderToken.pageSize}")

        if (pageLoaderToken.pageSize.width <= 0) return
        if ( reloadFonts ) loadFonts()

        content =
            SpannableStringBuilder(
                Html.fromHtml(
                    html, Html.FROM_HTML_MODE_COMPACT,
                    CustomImageGetter2(
                        book,
                        pageLoaderToken.pageSize.width,
                        pageLoaderToken.pageSize.height,
                        painter.color,
                        section > 0),
                    null
                )
            )

        postformatContent(painter)

        staticLayout =
            StaticLayout.Builder.obtain(content, 0, content.length, painter, pageLoaderToken.pageSize.width)
                    .setLineSpacing(pageLoaderToken.lineSpacingExtra, pageLoaderToken.lineSpacingMultiplier)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .build()

        pages = mutableListOf()

        var startLine = 0
        var endLine: Int
        var startLineTop: Int
        var endLineBottom: Int
        var startOffset: Int
        var endOffset: Int
        while (true) {
            startLineTop = staticLayout!!.getLineTop(startLine)
            endLine = staticLayout!!.getLineForVertical(startLineTop + pageLoaderToken.pageSize.height)
            endLineBottom = staticLayout!!.getLineBottom(endLine)
            var lastFullyVisibleLine = if (endLineBottom >  startLineTop + pageLoaderToken.pageSize.height ) endLine - 1 else endLine
            if ( lastFullyVisibleLine < startLine) lastFullyVisibleLine = startLine
            startOffset = staticLayout!!.getLineStart(startLine)
            endOffset = staticLayout!!.getLineEnd(lastFullyVisibleLine)
            pages.add(Page(
                    content = SpannableStringBuilder().append(content.subSequence(startOffset, endOffset)),
                    startBookPosition = BookPosition(section = section, offSet = startOffset),
                    endBookPosition = BookPosition(section = section, offSet = endOffset)
            ))

            if ( endLine >= staticLayout!!.lineCount-1 ) break
            startLine = lastFullyVisibleLine + 1
        }
    }

    private fun postformatContent(painter: TextPaint) {
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



