package com.kontranik.koreader.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;

import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class Word {
    SpannableString Data;
    String text;
    float wRelativeTextSize;
    int wColor;
    int wTypeFace;
    MyStyle style;

    public Word(String wordtext, MyStyle style) {
        this.style = style;
        text = wordtext;

        SpannableString spannable = new SpannableString(wordtext);

        wRelativeTextSize = 1.0f;
        wTypeFace = Typeface.NORMAL;
        wColor = Color.BLACK;

        // TODO: style auswerten und entsprechend Spans setzen

        /*

        BackgroundColorSpan(Color.green)
        ForegroundColorSpan(Color.PINK)

        StyleSpan(Typeface.ITALIC)
        StyleSpan(Typeface.BOLD)

         */
        switch (style) {
            case Title:
            case H1:
                wRelativeTextSize = 2.6f;
                wTypeFace = Typeface.BOLD;
                wColor = Color.MAGENTA;
                break;
            case H2:
                wRelativeTextSize = 1.95f;
                wTypeFace = Typeface.BOLD;
                wColor = Color.MAGENTA;
                break;
            case H3:
                wRelativeTextSize = 1.521f;
                wTypeFace = Typeface.BOLD;
                wColor = Color.MAGENTA;
                break;
            case H4:
                wRelativeTextSize = 1.2f;
                wTypeFace = Typeface.BOLD;
                wColor = Color.MAGENTA;
                break;
            case H5:
                wRelativeTextSize = 1.079f;
                wTypeFace = Typeface.BOLD;
                wColor = Color.MAGENTA;
                break;
            case H6:
                wRelativeTextSize = 0.871f;
                wTypeFace = Typeface.BOLD;
                //wColor = Color.MAGENTA;
                break;
            default:
        }
        spannable.setSpan( new RelativeSizeSpan(wRelativeTextSize), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannable.setSpan( new StyleSpan(wTypeFace), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan( new ForegroundColorSpan(wColor), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Data = spannable;
    }

    public SpannableString getData() {
        return Data;
    }

    public void setData(SpannableString data) {
        Data = data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MyStyle getStyle() {
        return style;
    }

    public void setStyle(MyStyle style) {
        this.style = style;
    }

    public float getwRelativeTextSize() {
        return wRelativeTextSize;
    }

    public void setwRelativeTextSize(float wRelativeTextSize) {
        this.wRelativeTextSize = wRelativeTextSize;
    }

    public int getwColor() {
        return wColor;
    }

    public void setwColor(int wColor) {
        this.wColor = wColor;
    }

    public int getwTypeFace() {
        return wTypeFace;
    }

    public void setwTypeFace(int wTypeFace) {
        this.wTypeFace = wTypeFace;
    }
}

