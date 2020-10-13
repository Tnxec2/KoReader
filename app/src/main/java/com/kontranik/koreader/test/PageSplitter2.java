package com.kontranik.koreader.test;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kontranik.koreader.model.MyStyle;
import com.kontranik.koreader.model.Word;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class PageSplitter2 {

    public static final String TAG = "PageSplitter2";

    private final int pageWidth;
    private final int pageHeight;
    float textSize;

    private final List<CharSequence> pages = new ArrayList<>();
    private SpannableStringBuilder currentLine = new SpannableStringBuilder();
    private SpannableStringBuilder currentPage = new SpannableStringBuilder();
    private int currentLineHeight;
    private int pageContentHeight;
    private int currentLineWidth;
    private int lastTextLineHeight;


    TextPaint mTextPaint;
    Typeface typeFace;


    public PageSplitter2(int pageWidth, int pageHeight, TextPaint paint) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;

        mTextPaint = new TextPaint(paint);
        textSize = paint.getTextSize();
        typeFace = Typeface.create(paint.getTypeface(), Typeface.NORMAL);

        currentLineHeight = 0;
        currentLineWidth = 0;
        pageContentHeight = 0;
        lastTextLineHeight = 0;
    }

    public void append(String text, MyStyle style) {
        String[] paragraphs = text.split("\n", -1);
        int i;
        for (i = 0; i < paragraphs.length - 1; i++) {
            appendText(paragraphs[i], style);
            //appendNewLine();
        }
        appendText(paragraphs[i], style);
    }

    private void appendText(String text, MyStyle style) {
        String[] words = text.split(" ", -1);
        int i;
        for (i = 0; i < words.length - 1; i++) {
            appendWord(new Word(words[i] + " ", style));
        }
        appendWord( new Word(words[i] , style) );
    }

    public void appendWord(Word word) {
        mTextPaint.setTypeface( Typeface.create( typeFace, word.getWStyle()));
        mTextPaint.setTextSize( textSize  * word.getwRelativeTextSize());

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        int _lineHeight = (int) Math.ceil(fm.bottom - fm.top + fm.leading);

        //Log.d( TAG, "Textsize: " + textSize + ", relativeTextSize: " + textSize * word.getwRelativeTextSize() + ", lineHeight: "  + _lineHeight);

        int _wordWidth = (int) Math.ceil(mTextPaint.measureText(word.getData().toString()));

        //textLineHeight = Math.max(textLineHeight, _lineHeight);

        if ( currentLineWidth + _wordWidth >= pageWidth && currentLineHeight > 0) {
            appendLineToPage();
        }
        appendTextToLine(word.getData(), _wordWidth, _lineHeight);
    }

    public void newLine() {
        appendNewLine();
    }

    private void appendNewLine() {
        if ( currentLineHeight == 0) currentLineHeight = lastTextLineHeight;
        currentLine.append("\n");
        appendLineToPage();
    }

    private void checkForPageEnd() {
        if (pageContentHeight + currentLineHeight >= pageHeight) {
            newPage();
        }
    }

    private void newPage() {
        pages.add(currentPage);
        Log.d(TAG, "pageHeight: " + pageHeight + ", pageContentHeight: " + pageContentHeight);
        Log.d(TAG, "new Page");
        currentPage = new SpannableStringBuilder();
        pageContentHeight = 0;
    }

    private void appendLineToPage() {

        checkForPageEnd();

        if ( pageContentHeight > 0 ) {
            currentLine.append("\n");
        }

        currentPage.append(currentLine);
        pageContentHeight += currentLineHeight;

        Log.d(TAG, "curlineheith: " + currentLineHeight + ", pageContentHeight: " + pageContentHeight + " :: " + currentLine.toString());


        currentLine = new SpannableStringBuilder();
        currentLineWidth = 0;
        lastTextLineHeight = currentLineHeight;
        currentLineHeight = 0;
    }

    private void appendTextToLine(SpannableString spannableString, int wordWidth, int wordHeight) {
        currentLineHeight = Math.max(currentLineHeight, wordHeight);
        currentLine.append(spannableString);
        currentLineWidth += wordWidth;
    }

    public List<CharSequence> getPages() {
        List<CharSequence> copyPages = new ArrayList<CharSequence>(pages);
        SpannableStringBuilder lastPage = new SpannableStringBuilder(currentPage);
        if (pageContentHeight + currentLineHeight > pageHeight) {
            copyPages.add(lastPage);
            lastPage = new SpannableStringBuilder();
        }
        lastPage.append(currentLine);
        copyPages.add(lastPage);
        return copyPages;
    }


}
