package com.kontranik.koreader;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class WebViewWidth extends WebView {

    Context context;
    private boolean bAllowScroll = true;

    public WebViewWidth(Context context) {
        super(context);
        this.context = context;

    }

    public WebViewWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getContentWidth() {
        return this.computeHorizontalScrollRange();
    }

    public int getTotalHeight() {
        return this.computeVerticalScrollRange();
    }

    public void scrollToPage(int page, int screenWidth) {
        scrollTo(page * screenWidth, 0);
    }
}