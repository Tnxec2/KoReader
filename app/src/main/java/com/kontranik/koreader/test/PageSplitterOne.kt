package com.kontranik.koreader.test

import android.os.Build
import android.text.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.Cursor
import com.kontranik.koreader.model.Line
import com.kontranik.koreader.model.MyStyle
import com.kontranik.koreader.model.Word
import kotlin.math.ceil
import kotlin.math.max

@RequiresApi(api = Build.VERSION_CODES.Q)
class PageSplitterOne(private val pageWidth: Int, private val pageHeight: Int, paint: TextPaint, private val lineSpacingMultiplier: Float, private val lineSpacingExtra: Float) {

    private var mTextPaint: TextPaint = TextPaint(paint)

    var page: CharSequence? = null

    private var currentLine = SpannableStringBuilder()
    private var currentPage = SpannableStringBuilder()

    private var currentLineHeight: Int
    private var pageContentHeight: Int
    private var currentLineWidth: Int
    private var lastTextLineHeight: Int

    var paragraphIndex: Int = 0
    var wordIndex: Int = 0

    fun clear() {
        page = null
        currentLine = SpannableStringBuilder()
        currentPage = SpannableStringBuilder()
        currentLineHeight = 0
        pageContentHeight = 0
        currentLineWidth = 0
        lastTextLineHeight = 0
        paragraphIndex = 0
        wordIndex = 0
    }

    fun append(line: Line, startParagraph: Int, startWord: Int): Boolean {
        if (line.style == MyStyle.None) return false
        val paragraphs = line.text.split("\n")

        Log.d(TAG, "startWord:" + startWord)

        paragraphIndex = startParagraph
        while (paragraphIndex < paragraphs.size ) {
            if ( ! appendText(paragraphs[paragraphIndex], line.style, startParagraph, startWord) ) return false
            if ( ! appendNewLine(false) ) return false
            paragraphIndex++
        }
        return true
    }

    fun appendRevers(line: Line, startParagraph: Int, startWord: Int): Boolean {
        if (line.style == MyStyle.None) return false
        val paragraphs = line.text.split("\n")

        Log.d(TAG, "startWord:" + startWord)

        paragraphIndex = if ( startParagraph == 0 ) paragraphs.size-1 else startParagraph
        while (paragraphIndex >= 0 ) {
            if ( ! appendTextRevers(paragraphs[paragraphIndex], line.style, startParagraph, startWord) ) return false
            if ( ! appendNewLine(true) ) return false
            paragraphIndex--
        }
        return true
    }

    private fun appendText(text: String, style: MyStyle, startParagraph: Int, startWord: Int): Boolean {
        val words = text.split(" ")
        wordIndex = if ( startParagraph == paragraphIndex ) startWord else 0

        while (wordIndex < words.size) {
            if (!appendWord(Word(words[wordIndex] + " ", style), false)) return false
            wordIndex++
        }
        return true
    }

    private fun appendTextRevers(text: String, style: MyStyle, startParagraph: Int, startWord: Int): Boolean {
        val words = text.split(" ")
        wordIndex = if ( startParagraph == paragraphIndex && startWord != 0 ) startWord else words.size-1

        while (wordIndex >= 0) {
            if (!appendWord(Word(words[wordIndex] + " ", style), true) ) return false
            wordIndex--
        }
        return true
    }

    private fun appendWord(word: Word, revers: Boolean): Boolean {
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

        val lineHeight: Int = endLineBottom - startLineTop
        val wordWidth: Int = ceil( staticLayout.getLineWidth(0).toDouble() ).toInt()

        if (currentLineWidth + wordWidth >= pageWidth && currentLineHeight > 0) {
            if ( ! appendLineToPage(revers) ) return false
        }
        return appendTextToLine(word.data, wordWidth, lineHeight, revers)
    }

    private fun appendNewLine(revers: Boolean): Boolean {
        if (currentLineHeight == 0) currentLineHeight = lastTextLineHeight
        currentLine.append("\n")
        return appendLineToPage(revers)
    }


    private fun appendLineToPage(revers: Boolean): Boolean {
        if (pageContentHeight + currentLineHeight >= pageHeight) {
            page = currentPage
            return false
        }
        if (pageContentHeight > 0) {
            currentLine.append("\n")
        }

        if ( revers ) currentPage.insert(0, currentLine)
        else currentPage.append(currentLine)

        pageContentHeight += currentLineHeight
        Log.d(TAG, "curlineheith: $currentLineHeight, pageContentHeight: $pageContentHeight :: $currentLine")
        currentLine = SpannableStringBuilder()
        currentLineWidth = 0
        lastTextLineHeight = currentLineHeight
        currentLineHeight = 0
        return true
    }

    private fun appendTextToLine(spannableString: SpannableString, wordWidth: Int, wordHeight: Int, revers: Boolean): Boolean {
        currentLineHeight = max(currentLineHeight, wordHeight)
        if (pageContentHeight + currentLineHeight >= pageHeight) {
            page = currentPage
            return false
        }

        if ( revers) currentLine.insert(0, spannableString)
        else currentLine.append(spannableString)

        currentLineWidth += wordWidth
        return true
    }

    companion object {
        const val TAG = "PageSplitter2"
    }

    init {
        currentLineHeight = 0
        currentLineWidth = 0
        pageContentHeight = 0
        lastTextLineHeight = 0
    }
}