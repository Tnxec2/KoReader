package com.kontranik.koreader.reader

import android.content.Context
import android.text.*
import android.util.Log

import com.kontranik.koreader.model.Line
import com.kontranik.koreader.model.MyStyle
import com.kontranik.koreader.model.WordSequence


class PageSplitterOne(
        val pageWidth: Int,
        val pageHeight: Int,
        private val paint: TextPaint,
        private val lineSpacingMultiplier: Float,
        private val lineSpacingExtra: Float, var c: Context) {

    var page: SpannableStringBuilder? = null

    private var currentPage: SpannableStringBuilder = SpannableStringBuilder()

    var paragraphIndex: Int = 0
    var symbolIndex: Int = 0

    private var staticLayout: StaticLayout? = null

    fun clear() {
        page = null
        currentPage = SpannableStringBuilder()
        paragraphIndex = 0
        symbolIndex = 0
    }

    fun append(line: Line, startParagraph: Int, startWord: Int): Boolean {
        if (line.style == MyStyle.None) return false
        val paragraphs = line.text.split("\n")

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

    fun appendRevers(line: Line, startParagraph: Int?, startWord: Int?): Boolean {
        if (line.style == MyStyle.None) return false
        val paragraphs = line.text.split("\n")

        paragraphIndex = if ( startParagraph != null )  startParagraph else paragraphs.size-1
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
        if ( ! appendWordSequence(WordSequence("\n\n", MyStyle.Paragraph, c), revers, moveCursor = false) ) return false
        return true
    }

    private fun appendParagraph(paragraph: String, style: MyStyle, startParagraph: Int, startSymbol: Int): Boolean {
        symbolIndex = if ( startParagraph == paragraphIndex ) startSymbol else 0
        val wordSeq = paragraph.substring(symbolIndex)
        return appendWordSequence(WordSequence(wordSeq, style, c), revers = false, moveCursor = true)
    }

    private fun appendParagraphRevers(paragraph: String, style: MyStyle, startParagraph: Int?, startSymbol: Int?): Boolean {
        symbolIndex = if ( startParagraph == paragraphIndex && startSymbol != null ) startSymbol else paragraph.length
        val wordSeq = paragraph.substring(0, symbolIndex)
        return appendWordSequence(WordSequence(wordSeq, style, c), revers = true, moveCursor = true)
    }

    private fun appendWordSequence(wordSequence: WordSequence, revers: Boolean, moveCursor: Boolean): Boolean {

        Log.d(TAG, "appenWordSequence: wS: " + wordSequence.data )
        Log.d(TAG, "appenWordSequence: r: " + revers + ", mC: " + moveCursor + ", sIx: " + symbolIndex)

        val initTextLength = currentPage.toString().length

        if ( moveCursor ) {
            Log.d(TAG, "davor: " + currentPage.toString())
        }

        if ( revers) currentPage.insert(0, wordSequence.data)
        else currentPage.append(wordSequence.data)

        staticLayout = StaticLayout(currentPage, paint, pageWidth, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, true)


    // komplette höhe berechnen
        var startLine = 0
        var endLine = staticLayout!!.lineCount-1
        var startLineTop = staticLayout!!.getLineTop(startLine)
        var endLineBottom = staticLayout!!.getLineBottom(endLine)

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
                page = SpannableStringBuilder().append(currentPage.subSequence(startOffset, endOffset))
            } else {
                endLine = staticLayout!!.lineCount-1
                endLineBottom = staticLayout!!.getLineBottom(endLine)
                startLine = staticLayout!!.getLineForVertical( endLineBottom - pageHeight)
                startLineTop = staticLayout!!.getLineTop(startLine)
                val firstFullyVisibleLine = if (startLineTop <  endLineBottom - pageHeight ) startLine + 1 else startLine
                val startOffset = staticLayout!!.getLineStart(firstFullyVisibleLine)
                val endOffset = staticLayout!!.getLineEnd(endLine)
                page = SpannableStringBuilder().append(currentPage.subSequence(startOffset, endOffset))
            }

            // neue Cursorposition berechnen
            if (moveCursor) {
                val endTextLenght = page.toString().length
                val addedSymbols = endTextLenght - initTextLength
                if ( revers ) symbolIndex -= addedSymbols
                else symbolIndex += addedSymbols
                Log.d(TAG, "danach: " + page.toString())
                Log.d(TAG, "appenWordSequence: iTL: " + initTextLength + ", eTL: " + endTextLenght + ", sIx: " + symbolIndex)
            }

            return false
        }

        return true
    }



    companion object {
        const val TAG = "PageSplitterOne"
        const val heightOffset = 5
    }

}

