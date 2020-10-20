package com.kontranik.koreader.reader

import android.content.Context
import android.os.Build
import android.text.*

import androidx.annotation.RequiresApi
import com.kontranik.koreader.model.Line
import com.kontranik.koreader.model.MyStyle
import com.kontranik.koreader.model.Word


class PageSplitterOne(
        val pageWidth: Int,
        val pageHeight: Int,
        private val paint: TextPaint,
        private val lineSpacingMultiplier: Float,
        private val lineSpacingExtra: Float, var c: Context) {

    var page: SpannableStringBuilder? = null

    private var currentPage: SpannableStringBuilder = SpannableStringBuilder()
    private var tempPage: SpannableStringBuilder = SpannableStringBuilder()

    var paragraphIndex: Int = 0
    var wordIndex: Int = 0

    private var staticLayout: StaticLayout? = null

    fun clear() {
        page = null
        currentPage = SpannableStringBuilder()
        paragraphIndex = 0
        wordIndex = 0
        tempPage = SpannableStringBuilder()

    }

    fun append(line: Line, startParagraph: Int, startWord: Int): Boolean {
        if (line.style == MyStyle.None) return false
        val paragraphs = line.text.split("\n")

        //Log.d(TAG, "startWord:$startWord")

        paragraphIndex = startParagraph
        while (paragraphIndex < paragraphs.size ) {
            if ( ! appendText(paragraphs[paragraphIndex], line.style, startParagraph, startWord) ) return false
            if (currentPage.isNotEmpty()) {
                if (!appendWord(Word("\n", MyStyle.Paragraph, c), false)) return false
                if (!appendWord(Word("\n", MyStyle.Paragraph, c), false)) return false
            }
            paragraphIndex++
        }
        return true
    }

    fun appendRevers(line: Line, startParagraph: Int?, startWord: Int?): Boolean {
        if (line.style == MyStyle.None) return false
        val paragraphs = line.text.split("\n")

        //Log.d(TAG, "startWord:" + startWord)

        paragraphIndex = if ( startParagraph != null )  startParagraph else paragraphs.size-1
        while (paragraphIndex >= 0 ) {
            if ( ! appendTextRevers(paragraphs[paragraphIndex], line.style, startParagraph, startWord) ) return false
            if (currentPage.isNotEmpty()) {
                if (!appendWord(Word("\n", MyStyle.Paragraph, c), true)) return false
                if (!appendWord(Word("\n", MyStyle.Paragraph, c), true)) return false
            }
            paragraphIndex--
        }
        return true
    }

    private fun appendText(text: String, style: MyStyle, startParagraph: Int, startWord: Int): Boolean {
        val words = text.split(" ")

        wordIndex = if ( startParagraph == paragraphIndex ) startWord else 0

        while (wordIndex < words.size) {
            if (!appendWord(Word(words[wordIndex] + " ", style, c), false)) return false
            wordIndex++
        }
        return true
    }

    private fun appendTextRevers(text: String, style: MyStyle, startParagraph: Int?, startWord: Int?): Boolean {
        val words = text.split(" ")
        wordIndex = if ( startParagraph == paragraphIndex && startWord != null ) startWord else words.size-1

        while (wordIndex >= 0) {
            if (!appendWord(Word(words[wordIndex] + " ", style, c), true) ) return false
            wordIndex--
        }
        return true
    }

    private fun appendWord(word: Word, revers: Boolean): Boolean {

        if ( revers) tempPage.insert(0, word.data)
        else tempPage.append(word.data)

        staticLayout = StaticLayout(tempPage, paint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, true)

        val startLineTop = staticLayout!!.getLineTop(0)
        val endLineBottom = staticLayout!!.getLineBottom(staticLayout!!.lineCount-1)

        if ( endLineBottom - startLineTop > pageHeight) {
            page = currentPage
            return false
        }

        if ( revers) currentPage.insert(0, word.data)
        else currentPage.append(word.data)

        return true
    }

    companion object {
        const val TAG = "PageSplitter2"
    }

}

