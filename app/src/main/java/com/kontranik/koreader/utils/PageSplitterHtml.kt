package com.kontranik.koreader.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.text.*
import android.text.style.QuoteSpan
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookPosition
import com.kontranik.koreader.model.Page

open class PageSplitterHtml(context: Context) : FontsHelper(context) {

    var pages: MutableList<Page> = mutableListOf()

    private var content: SpannableStringBuilder = SpannableStringBuilder()
    private var staticLayout: StaticLayout? = null

    fun splitPages(textView: TextView, book: Book, section: Int, html: String, reloadFonts: Boolean) {
        if (textView.measuredWidth <= 0) return
        if ( reloadFonts ) loadFonts()

        val pageWidth: Int = textView.measuredWidth - textView.paddingLeft - textView.paddingRight
        val pageHeight: Int = textView.measuredHeight - textView.paddingTop - textView.paddingBottom
        val paint = textView.paint
        val lineSpacingMultiplier: Float = textView.lineSpacingMultiplier
        val lineSpacingExtra: Float = textView.lineSpacingExtra

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val colorThemeIndex = prefs.getString(
            PrefsHelper.PREF_KEY_COLOR_SELECTED_THEME, null)?.toIntOrNull()?.minus(1)
            ?: PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT


        val co = prefs.getInt(PrefsHelper.PREF_KEY_COLOR_TEXT + colorThemeIndex+1, 0)
        val colorText = Color.parseColor(
            if (co != 0) "#" + Integer.toHexString(co)
            else context.resources.getString(PrefsHelper.colorForegroundDefaultArray[colorThemeIndex])
        )

        content =
            SpannableStringBuilder(Html.fromHtml(
                    html, Html.FROM_HTML_MODE_COMPACT, CustomImageGetter(book, pageWidth, pageHeight, colorText, section > 0), null))

        postformatContent(textView)

        staticLayout =
            StaticLayout.Builder.obtain(content, 0, content.length, paint, pageWidth)
                    .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
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
            endLine = staticLayout!!.getLineForVertical(startLineTop + pageHeight)
            endLineBottom = staticLayout!!.getLineBottom(endLine)
            var lastFullyVisibleLine = if (endLineBottom >  startLineTop + pageHeight ) endLine - 1 else endLine
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

    private fun postformatContent(textView: TextView) {
        formatQuotes(textView)
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

    private fun formatQuotes(textView: TextView) {
        val quoteColor = textView.currentTextColor
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



