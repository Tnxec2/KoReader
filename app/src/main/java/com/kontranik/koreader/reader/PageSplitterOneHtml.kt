package com.kontranik.koreader.reader

import android.text.*


open class PageSplitterOneHtml(
        var pageWidth: Int,
        var pageHeight: Int,
        val paint: TextPaint,
        var lineSpacingMultiplier: Float,
        val lineSpacingExtra: Float) {

    var pageOne: SpannableStringBuilder? = null

    private var currentPage: SpannableStringBuilder = SpannableStringBuilder()

    var startOffset: Int = 0
    var endOffset: Int = 0

    private var staticLayout: StaticLayout? = null

    fun clear() {
        pageOne = null
        currentPage = SpannableStringBuilder()
        startOffset = 0
        endOffset = 0
    }

    fun pageForHtml(html: String, offset: Int?, revers: Boolean) {
        currentPage = SpannableStringBuilder(Html.fromHtml(html))
        staticLayout = StaticLayout(currentPage, paint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, true)

        val startLine: Int
        val endLine: Int
        val startLineTop: Int
        val endLineBottom: Int

        if ( ! revers ) {
            startLine = if ( offset != null ) { staticLayout!!.getLineForOffset(offset) } else { 0 }
            startLineTop = staticLayout!!.getLineTop(startLine)
            endLine = staticLayout!!.getLineForVertical(startLineTop + pageHeight)
            endLineBottom = staticLayout!!.getLineBottom(endLine)
            val lastFullyVisibleLine = if (endLineBottom >  startLineTop + pageHeight ) endLine - 1 else endLine
            startOffset = staticLayout!!.getLineStart(startLine)
            endOffset = staticLayout!!.getLineEnd(lastFullyVisibleLine)
        } else {
            endLine = if (offset != null ) { staticLayout!!.getLineForOffset(offset) } else { staticLayout!!.lineCount - 1             }
            endLineBottom = staticLayout!!.getLineBottom(endLine)
            startLine = staticLayout!!.getLineForVertical( endLineBottom - pageHeight)
            startLineTop = staticLayout!!.getLineTop(startLine)
            val firstFullyVisibleLine = if (startLineTop <  endLineBottom - pageHeight ) startLine + 1 else startLine
            startOffset = staticLayout!!.getLineStart(firstFullyVisibleLine)
            endOffset = staticLayout!!.getLineEnd(endLine)
        }

        if ( startOffset != endOffset)
            pageOne = SpannableStringBuilder().append(currentPage.subSequence(startOffset, endOffset))
    }

    companion object {
        const val TAG = "PageSplitterOneHtml"
    }
}



