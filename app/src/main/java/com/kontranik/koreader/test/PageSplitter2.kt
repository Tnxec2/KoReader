package com.kontranik.koreader.test

import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.style.StyleSpan
import android.util.Log
import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.Cursor
import com.kontranik.koreader.model.MyStyle
import com.kontranik.koreader.model.Word
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.Q)
class PageSplitter2(private val pageWidth: Int, private val pageHeight: Int, paint: TextPaint, private val lineSpacingMultiplier: Float, private val lineSpacingExtra: Float,) {
    var textSize: Float
    private val pages: MutableList<CharSequence> = ArrayList()
    private var currentLine = SpannableStringBuilder()
    private var currentPage = SpannableStringBuilder()

    private var currentLineHeight: Int
    private var pageContentHeight: Int
    private var currentLineWidth: Int
    private var lastTextLineHeight: Int
    var mTextPaint: TextPaint
    var typeFace: Typeface

    fun append(text: String, style: MyStyle) {
        val paragraphs = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var i: Int
        i = 0
        while (i < paragraphs.size - 1) {
            appendText(paragraphs[i], style)
            i++
        }
        appendText(paragraphs[i], style)
    }

    private fun appendText(text: String, style: MyStyle) {
        val words = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var i: Int
        i = 0
        while (i < words.size - 1) {
            appendWord(Word(words[i] + " ", style))
            i++
        }
        appendWord(Word(words[i], style))
    }

    fun appendWord(word: Word) {

        val staticLayout = StaticLayout(
                word.data,
                mTextPaint,
                100000,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                true
        )
        val startLineTop = staticLayout.getLineTop(0)
        val endLineBottom = staticLayout.getLineBottom(0)

        val _lineHeight: Int = endLineBottom - startLineTop;
        val _wordWidth: Int = Math.ceil( staticLayout.getLineWidth(0).toDouble() ).toInt();

        //textLineHeight = Math.max(textLineHeight, _lineHeight);
        if (currentLineWidth + _wordWidth >= pageWidth && currentLineHeight > 0) {
            appendLineToPage()
        }
        appendTextToLine(word.data, _wordWidth, _lineHeight)
    }

    fun newLine() {
        appendNewLine()
    }

    private fun appendNewLine() {
        if (currentLineHeight == 0) currentLineHeight = lastTextLineHeight
        currentLine.append("\n")
        appendLineToPage()
    }

    private fun checkForPageEnd() {
        if (pageContentHeight + currentLineHeight >= pageHeight) {
            newPage()
        }
    }

    private fun newPage() {
        pages.add(currentPage)
        Log.d(TAG, "pageHeight: $pageHeight, pageContentHeight: $pageContentHeight")
        Log.d(TAG, "new Page")
        currentPage = SpannableStringBuilder()
        pageContentHeight = 0
    }

    private fun appendLineToPage() {
        checkForPageEnd()
        if (pageContentHeight > 0) {
            currentLine.append("\n")
        }
        currentPage.append(currentLine)
        pageContentHeight += currentLineHeight
        Log.d(TAG, "curlineheith: $currentLineHeight, pageContentHeight: $pageContentHeight :: $currentLine")
        currentLine = SpannableStringBuilder()
        currentLineWidth = 0
        lastTextLineHeight = currentLineHeight
        currentLineHeight = 0
    }

    private fun appendTextToLine(spannableString: SpannableString, wordWidth: Int, wordHeight: Int) {
        currentLineHeight = Math.max(currentLineHeight, wordHeight)
        currentLine.append(spannableString)
        currentLineWidth += wordWidth
    }

    fun getPages(): List<CharSequence> {
        val copyPages: MutableList<CharSequence> = ArrayList(pages)
        var lastPage = SpannableStringBuilder(currentPage)
        if (pageContentHeight + currentLineHeight > pageHeight) {
            copyPages.add(lastPage)
            lastPage = SpannableStringBuilder()
        }
        lastPage.append(currentLine)
        copyPages.add(lastPage)
        return copyPages
    }

    companion object {
        const val TAG = "PageSplitter2"
    }

    init {
        mTextPaint = TextPaint(paint)
        textSize = paint.textSize
        typeFace = Typeface.create(paint.typeface, Typeface.NORMAL)
        currentLineHeight = 0
        currentLineWidth = 0
        pageContentHeight = 0
        lastTextLineHeight = 0
    }
}