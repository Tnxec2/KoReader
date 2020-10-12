package com.kontranik.koreader.test;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

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
    private int textLineHeight;

    TextPaint mTextPaint;


    public PageSplitter2(int pageWidth, int pageHeight, TextPaint paint) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.textLineHeight = 0;

        mTextPaint = paint;
        textSize = paint.getTextSize();
    }

    public void append(String text, MyStyle style) {
        String[] paragraphs = text.split("\n", -1);
        int i;
        for (i = 0; i < paragraphs.length - 1; i++) {
            appendText(paragraphs[i], style);
            appendNewLine();
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

        mTextPaint.setTextSize( textSize * word.getwRelativeTextSize()  );

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();

        float textHeight = fm.descent - fm.ascent;
        int _lineHeight = (int) Math.ceil(fm.bottom - fm.top + fm.leading);
        // int _lineHeight = (int) Math.ceil(mTextPaint.getFontMetrics(null) * lineSpacingMultiplier + lineSpacingExtra);
        int _wordWidth = (int) Math.ceil(mTextPaint.measureText(word.getData().toString()));

/*        StaticLayout tempLayout = new StaticLayout(word.getData(), mTextPaint, 100000, android.text.Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false);
        int lineCount = tempLayout.getLineCount();
        _wordWidth = 0;
        for(int i=0 ; i < lineCount ; i++){
            _wordWidth += tempLayout.getLineWidth(i);
        }*/
        //_lineHeight = tempLayout.getHeight();

        textLineHeight = Math.max(textLineHeight, _lineHeight);

        if ( currentLineWidth + _wordWidth >= pageWidth) {
            appendLineToPage(textLineHeight);
            appendNewLine();
        }
        appendTextToLine(word, _wordWidth);
    }

    public void newLine() {
        appendNewLine();
    }

    private void appendNewLine() {
        currentLine.append("\n");
        appendLineToPage(textLineHeight);
    }

    private void checkForPageEnd() {
        if (pageContentHeight + currentLineHeight > pageHeight) {
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

    private void appendLineToPage(int textLineHeight) {

        checkForPageEnd();

        Log.d(TAG, currentLine.toString());
        Log.d(TAG, "pageWidth: " + pageWidth + ", currentLineWidth: " + currentLineWidth);

        currentPage.append(currentLine);

        currentLine = new SpannableStringBuilder();
        currentLineHeight = textLineHeight;
        pageContentHeight += currentLineHeight;
        currentLineWidth = 0;
    }

    private void appendTextToLine(Word word, int wordWidth) {
        currentLineHeight = Math.max(currentLineHeight, textLineHeight);
        currentLine.append(word.getData());
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
