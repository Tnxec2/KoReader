package com.kontranik.koreader.test

import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import com.kontranik.koreader.model.WordSequence
import java.util.*


class PageSplitter3(
        private val pageWidth: Int,
        private val pageHeight: Int,
        private val lineSpacingMultiplier: Float,
        private val lineSpacingExtra: Float) {

    val TAG = "PageSplitter3"
    private val pages: MutableList<CharSequence> = ArrayList()
    private val mSpannableStringBuilder = SpannableStringBuilder()

    fun newLine() {
        mSpannableStringBuilder.append("\n")
    }

    fun append(wordSequence: WordSequence) {
        append(wordSequence.data);
    }

    fun append(charSequence: CharSequence?) {
        mSpannableStringBuilder.append(charSequence)
    }

    fun split(textPaint: TextPaint?) {
        val staticLayout = StaticLayout(
                mSpannableStringBuilder,
                textPaint,
                pageWidth,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                true
        )
        var startLine = 0
        while (startLine < staticLayout.lineCount) {
            val startLineTop = staticLayout.getLineTop(startLine)
            val endLine = staticLayout.getLineForVertical(startLineTop + pageHeight)
            val endLineBottom = staticLayout.getLineBottom(endLine)
            var lastFullyVisibleLine: Int
            lastFullyVisibleLine = if (endLineBottom > startLineTop + pageHeight) endLine - 1 else endLine
            val startOffset = staticLayout.getLineStart(startLine)
            val endOffset = staticLayout.getLineEnd(lastFullyVisibleLine)

            Log.d(TAG, mSpannableStringBuilder.subSequence(startOffset, endOffset).toString())
            Log.d(TAG, "*** new Page ***")
            pages.add(mSpannableStringBuilder.subSequence(startOffset, endOffset))
            startLine = lastFullyVisibleLine + 1
        }
    }

    fun getPages(): List<CharSequence> {
        return pages
    }

}