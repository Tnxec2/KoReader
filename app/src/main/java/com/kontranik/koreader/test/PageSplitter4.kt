package com.kontranik.koreader.test

import android.text.*
import com.kontranik.koreader.model.Word
import java.util.*


class PageSplitter4(
        private val pageWidth: Int,
        private val pageHeight: Int,
        private val lineSpacingMultiplier: Float,
        private val lineSpacingExtra: Float,
        private var textPaint: TextPaint) {

    private val pages: MutableList<CharSequence> = ArrayList()
    private val mSpannableStringBuilder = SpannableStringBuilder()
    private var dynamicLayout: DynamicLayout

    init {
        dynamicLayout = DynamicLayout(
                mSpannableStringBuilder,
                textPaint,
                pageWidth,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                false
        )
    }

    fun newLine() {
        mSpannableStringBuilder.append("\n")
    }

    fun append(word: Word) {
        append(word.data);
    }

    fun append(charSequence: CharSequence?) {
        mSpannableStringBuilder.append(charSequence)
    }

    fun split() {

        var startLine = 0
        while (startLine < dynamicLayout.lineCount) {
            val startLineTop = dynamicLayout.getLineTop(startLine)
            val endLine = dynamicLayout.getLineForVertical(startLineTop + pageHeight)
            val endLineBottom = dynamicLayout.getLineBottom(endLine)
            var lastFullyVisibleLine: Int
            lastFullyVisibleLine = if (endLineBottom > startLineTop + pageHeight) endLine - 1 else endLine
            val startOffset = dynamicLayout.getLineStart(startLine)
            val endOffset = dynamicLayout.getLineEnd(lastFullyVisibleLine)
            pages.add(mSpannableStringBuilder.subSequence(startOffset, endOffset))
            startLine = lastFullyVisibleLine + 1
        }
    }

    fun getPages(): List<CharSequence> {
        return pages
    }

}