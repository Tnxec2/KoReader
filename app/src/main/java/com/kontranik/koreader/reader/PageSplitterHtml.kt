package com.kontranik.koreader.reader

import android.graphics.text.LineBreaker
import android.icu.lang.UCharacter
import android.text.*
import android.widget.TextView
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page
import com.kontranik.koreader.utils.CustomImageGetter


open class PageSplitterHtml(private val textView: TextView) {

    var pages: MutableList<Page> = mutableListOf()

    private var content: SpannableStringBuilder = SpannableStringBuilder()
    private var staticLayout: StaticLayout? = null

    fun splitPages(book: Book, section: Int, html: String) {

        val pageWidth: Int = textView.measuredWidth - textView.paddingLeft - textView.paddingRight
        val pageHeight: Int = textView.measuredHeight - textView.paddingTop - textView.paddingBottom
        val paint = textView.paint
        val lineSpacingMultiplier: Float = textView.lineSpacingMultiplier
        val lineSpacingExtra: Float = textView.lineSpacingExtra

        content = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SpannableStringBuilder(Html.fromHtml(
                    html, Html.FROM_HTML_MODE_LEGACY, CustomImageGetter(book, pageWidth, pageHeight), null))
        } else {
            SpannableStringBuilder(Html.fromHtml(html,  CustomImageGetter(book, pageWidth, pageHeight), null))
        }

        staticLayout = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(content, 0, content.length, paint, pageWidth)
                    .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .build()
        } else {
            StaticLayout(content, paint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, true)
        }

        pages = mutableListOf()

        var startLine = 0
        var endLine: Int
        var startLineTop: Int
        var endLineBottom: Int
        var startOffset: Int
        var endOffset: Int
        while (true) {
            startLineTop = staticLayout!!.getLineTop(startLine)
            endLine = staticLayout!!.getLineForVertical(startLineTop + pageHeight)
            endLineBottom = staticLayout!!.getLineBottom(endLine)
            val lastFullyVisibleLine = if (endLineBottom >  startLineTop + pageHeight ) endLine - 1 else endLine
            startOffset = staticLayout!!.getLineStart(startLine)
            endOffset = staticLayout!!.getLineEnd(lastFullyVisibleLine)
            pages.add( Page(
                    content = SpannableStringBuilder().append(content.subSequence(startOffset, endOffset)) ,
                    startBookPosition = BookPosition(section = section, offSet = startOffset),
                    endBookPosition = BookPosition(section = section, offSet = endOffset)
                )
            )

            if ( endLine >= staticLayout!!.lineCount-1 ) break
            startLine = lastFullyVisibleLine + 1
        }
    }

    companion object {
        const val TAG = "PageSplitterOneHtml"
    }
}



