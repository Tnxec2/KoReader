package com.kontranik.koreader.reader

import android.content.Context
import android.text.*
import android.util.Log

import com.kontranik.koreader.model.Line
import com.kontranik.koreader.model.MyStyle
import com.kontranik.koreader.model.WordSequence
import kotlin.math.min


open class PageSplitterOne(
        var pageWidth: Int,
        var pageHeight: Int,
        val paint: TextPaint,
        var lineSpacingMultiplier: Float,
        val lineSpacingExtra: Float,
        var c: Context) {

    var pageOne: SpannableStringBuilder? = null
    var status: PageSplitterStatus = PageSplitterStatus.Clear

    private var currentPage: SpannableStringBuilder = SpannableStringBuilder()

    var paragraphIndex: Int = 0
    var symbolIndex: Int = 0

    private var staticLayout: StaticLayout? = null
    private var initTextLength: Int = 0

    fun clear() {
        pageOne = null
        currentPage = SpannableStringBuilder()
        paragraphIndex = 0
        symbolIndex = 0
        initTextLength = 0
        status = PageSplitterStatus.Clear
    }

    fun append(line: Line, startParagraph: Int?, startWord: Int?, revers: Boolean) : Boolean {
        initTextLength = currentPage.toString().length
        return if ( revers ) appendRevers(line, startParagraph, startWord)
                else append(line, startParagraph!!, startWord!!)
    }

    private fun append(line: Line, startParagraph: Int, startWord: Int): Boolean {
        if (line.style == MyStyle.None) return false
        if ( line.text == null) return false

        val paragraphs = line.text!!.split("\n")

        paragraphIndex = startParagraph
        while (paragraphIndex < paragraphs.size ) {
            if ( ! appendParagraph(paragraphs[paragraphIndex], line.style, startParagraph, startWord) ) return false
            if (currentPage.isNotEmpty()) {
                if (! appendNewLine(false)) return false
            }
            paragraphIndex++
        }
        return true
    }

    private fun appendRevers(line: Line, startParagraph: Int?, startWord: Int?): Boolean {
        if (line.style == MyStyle.None) return false
        if ( line.text == null) return false

        val paragraphs = line.text!!.split("\n")

        paragraphIndex = startParagraph ?: paragraphs.size-1
        while (paragraphIndex >= 0 ) {
            if ( ! appendParagraphRevers(paragraphs[paragraphIndex], line.style, startParagraph, startWord) ) return false
            if (currentPage.isNotEmpty()) {
                if ( ! appendNewLine(true)) return false
            }
            paragraphIndex--
        }
        return true
    }

    private fun appendNewLine(revers: Boolean): Boolean {
        if ( ! appendWordSequence(WordSequence("\n\n", MyStyle.Paragraph, c), revers) ) return false
        return true
    }

    private fun appendParagraph(paragraph: String, style: MyStyle, startParagraph: Int, startSymbol: Int): Boolean {
        if (paragraph.trim().isEmpty()) return  appendNewLine(revers = false)
        symbolIndex = if ( startParagraph == paragraphIndex ) startSymbol else 0
        if ( symbolIndex >= paragraph.length ) return false
        val wordSeq = paragraph.substring(symbolIndex)
        return appendWordSequence(WordSequence(wordSeq, style, c), revers = false)
    }

    private fun appendParagraphRevers(paragraph: String, style: MyStyle, startParagraph: Int?, startSymbol: Int?): Boolean {
        if (paragraph.trim().isEmpty()) return  appendNewLine(revers = false)
        symbolIndex = if ( startParagraph == paragraphIndex && startSymbol != null ) startSymbol else paragraph.length
        symbolIndex = min(symbolIndex, paragraph.length)
        val wordSeq = paragraph.substring(0, symbolIndex)
        return appendWordSequence(WordSequence(wordSeq, style, c), revers = true)
    }

    private fun appendWordSequence(wordSequence: WordSequence, revers: Boolean): Boolean {

        if ( revers) currentPage.insert(0, wordSequence.data)
        else currentPage.append(wordSequence.data)

        staticLayout = StaticLayout(currentPage, paint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, true)

        // komplette höhe berechnen
        var startLine = 0
        var endLine = staticLayout!!.lineCount-1
        var startLineTop = staticLayout!!.getLineTop(startLine)
        var endLineBottom = staticLayout!!.getLineBottom(endLine)

        if ( status == PageSplitterStatus.Clear && currentPage.toString().trim().length > 0) status = PageSplitterStatus.PageStartet

        // Page ist voll
        if ( endLineBottom - startLineTop > pageHeight) {
            // Page erstellen
            if ( ! revers ) {
                startLineTop = staticLayout!!.getLineTop(startLine)
                endLine = staticLayout!!.getLineForVertical(startLineTop + pageHeight)
                endLineBottom = staticLayout!!.getLineBottom(endLine)
                val lastFullyVisibleLine = if (endLineBottom >  startLineTop + pageHeight ) endLine - 1 else endLine
                val startOffset = staticLayout!!.getLineStart(startLine)
                val endOffset = staticLayout!!.getLineEnd(lastFullyVisibleLine)
                pageOne = SpannableStringBuilder().append(currentPage.subSequence(startOffset, endOffset))
            } else {
                endLine = staticLayout!!.lineCount-1
                endLineBottom = staticLayout!!.getLineBottom(endLine)
                startLine = staticLayout!!.getLineForVertical( endLineBottom - pageHeight)
                startLineTop = staticLayout!!.getLineTop(startLine)
                val firstFullyVisibleLine = if (startLineTop <  endLineBottom - pageHeight ) startLine + 1 else startLine
                val startOffset = staticLayout!!.getLineStart(firstFullyVisibleLine)
                val endOffset = staticLayout!!.getLineEnd(endLine)
                pageOne = SpannableStringBuilder().append(currentPage.subSequence(startOffset, endOffset))
            }
            status = PageSplitterStatus.PageFull

            // neue BookPosition im letzten Paragraph ermitteln berechnen
             // if (moveCursor) {
                val endTextLenght = pageOne.toString().length
                val addedSymbols = endTextLenght - initTextLength
                if ( revers ) symbolIndex -= addedSymbols
                else symbolIndex += addedSymbols
//                Log.d(TAG, "danach: " + page.toString())
//                Log.d(TAG, "appenWordSequence: iTL: " + initTextLength + ", eTL: " + endTextLenght + ", sIx: " + symbolIndex)
             // }
            Log.d(TAG, "*** start ***")
            Log.d(TAG, pageOne.toString())
            Log.d(TAG, "*** ende ***")

            return false
        }

        return true
    }

    internal fun getLastPage() {
        pageOne = SpannableStringBuilder().append(currentPage)
    }

    companion object {
        const val TAG = "PageSplitterOne"
    }
}

enum class PageSplitterStatus {
    Clear,
    PageStartet,
    PageFull
}

