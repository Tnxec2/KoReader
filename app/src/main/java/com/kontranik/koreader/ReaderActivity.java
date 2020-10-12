package com.kontranik.koreader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;


public class ReaderActivity extends AppCompatActivity {

    private static final String TAG = "ReaderActivity";
    public static final String INTENT_PATH = "BookPath";

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Books/");

    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    ImageButton imgbtnPrev, imgbtnNext;
    TextView txtPager;

    WebViewWidth  mWebView;
    EpubReader mEpubReader;
    Book mBook;
    Integer maxPage;

    private static int mCurrentPageNumber = 0;
    private static String mFileLocation;

    boolean scrollTop = false;
    boolean scrollBottom = false;

    private final Handler handler = new Handler();

    Book book;

    int totalScrollWidth;
    int screenWidth;
    int totalPages;
    int presentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        mFileLocation = PATH + "test.epub";
        mWebView = findViewById(R.id.webView_reader);
        mWebView.context = this;

        mWebView.setBackgroundColor(Color.rgb(255, 242, 229));
        WebSettings webSettings = mWebView.getSettings();
        //webSettings.setDefaultFontSize(24);
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        //mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        totalScrollWidth = mWebView.getContentWidth();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        mWebView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onPageFinished(WebView view, String url) {
                /*addEOCPadding();
                try {
                    //restoreBgColor();
                    restoreScrollOffsetDelayed(100);
                } catch (Throwable t) {
                    Log.e(TAG, t.getMessage(), t);
                }*/

                final WebView webView = (WebView) view;


                String varMySheet = "var mySheet = document.styleSheets[0];";

                String addCSSRule = "function addCSSRule(selector, newRule) {"
                        + "ruleIndex = mySheet.cssRules.length;"
                        + "mySheet.insertRule(selector + '{' + newRule + ';}', ruleIndex);"

                        + "}";

                String insertRule1 = "addCSSRule('html', 'padding: 0px; height: "
                        + (webView.getMeasuredHeight() / view.getContext().getResources().getDisplayMetrics().density)
                        + "px; -webkit-column-gap: 0px; -webkit-column-width: "
                        + webView.getMeasuredWidth() + "px;')";

                webView.loadUrl("javascript:" + varMySheet);
                webView.loadUrl("javascript:" + addCSSRule);
                webView.loadUrl("javascript:" + insertRule1);

                view.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);

                //giving delay of 2 sec ,else Totalscrollwidth is giving 0
                CountDownTimer test = new CountDownTimer(3000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) { }

                    @Override
                    public void onFinish() {

                        int widthis = mWebView.getContentWidth();

                        totalScrollWidth = widthis;

                        totalPages = (totalScrollWidth / screenWidth) - 1;

                        Log.d(TAG, "Totalpages = " + totalPages);

                        if ( scrollBottom ) {
                            presentPage = totalPages;
                            mWebView.scrollToPage(presentPage, screenWidth);
                        }
                        if ( scrollTop ) {
                            presentPage = 1;
                            mWebView.scrollToPage(presentPage, screenWidth);
                        }

                        //progressdialog.dismiss();

                    }
                };
                test.start();
            }

        });

        // Optimize loading times.
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setBlockNetworkImage(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setGeolocationEnabled(false);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSaveFormData(false);

        mWebView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                //
            }
            public void onSwipeRight() {
                Log.d("Nik", "swipe right");
                prevPage();
            }
            public void onSwipeLeft() {
                Log.d("Nik", "swipe left");
                nextPage();
            }
            public void onSwipeBottom() {
                //
            }

        });

        imgbtnPrev = findViewById(R.id.imgbtn_page_prev);
        imgbtnNext = findViewById(R.id.imgbtn_page_next);
        txtPager = findViewById(R.id.txtPager);

        imgbtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevPage();
            }
        });

        imgbtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });

        mEpubReader = new EpubReader();

        Bundle arguments = getIntent().getExtras();
        if ( arguments != null && arguments.containsKey(INTENT_PATH) )
            mFileLocation = arguments.getString(INTENT_PATH);
        else {
            finish();
        }

        Log.d(TAG, mFileLocation);

        loadBook();
        showPage(mCurrentPageNumber);

    }


    void nextPage() {
            if ( totalPages > presentPage) {
                presentPage++;
                mWebView.scrollToPage(presentPage, screenWidth);
            } else {
                Toast.makeText(getApplicationContext(), "The End", Toast.LENGTH_SHORT).show();
                scrollTop = true;
                showPage(mCurrentPageNumber + RIGHT);
            }
    }

    void prevPage() {
        if ( presentPage > 1) {
            presentPage--;
            mWebView.scrollToPage(presentPage,screenWidth);
        } else {
            Toast.makeText(getApplicationContext(), "The Begin", Toast.LENGTH_SHORT).show();
            scrollBottom = true;
            showPage(mCurrentPageNumber + LEFT);
        }
    }

    private void restoreScrollOffsetDelayed(int delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                restoreScrollOffset();
            }
        }, delay);
    }

    private void restoreScrollOffset() {

        //if (book==null) return;
        int spos = -1; // mBook.getSectionOffset();
        mWebView.computeScroll();
        if (spos>=0) {
            mWebView.scrollTo(0, spos);
            Log.d(TAG, "restoreScrollOffset " + spos);
        } else if (scrollBottom){
            mWebView.pageDown(true);
            //mWebView.scrollTo(0, mWebView.getContentHeight());
            Log.d(TAG, "scrollBottom ");
        } else if (scrollTop){
            Log.d(TAG, "scrollTop ");
            mWebView.pageUp(true);
        }
        scrollTop = false;
        scrollBottom = false;
    }

    void loadBook() {
        try {
            FileInputStream fileInputStream = new FileInputStream(mFileLocation);
            mBook = mEpubReader.readEpub(fileInputStream);
            maxPage = mBook.getContents().size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPage(int currentPageNumber) {
        if ( currentPageNumber < 0 || currentPageNumber > maxPage-1) return;

        String data = null;
        try {
            if ( mBook != null) {
                if ( currentPageNumber == 0) {
                    Resource cover = mBook.getCoverImage();
                    if ( cover != null) {
                        byte[] coverData = cover.getData();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(coverData, 0, coverData.length);
                        String html = "<html><body style=\"text-align: center\"><img src='{IMAGE_PLACEHOLDER}' style=\"height: auto;  width: auto;  max-width: 300px;  max-height: 300px; \" /></body></html>";

                        // Convert bitmap to Base64 encoded image for web
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        String image = "data:image/png;base64," + imgageBase64;

                        // Use image for the img src parameter in your html and load to webview
                        html = html.replace("{IMAGE_PLACEHOLDER}", image);
                        mCurrentPageNumber = currentPageNumber;
                        txtPager.setText(currentPageNumber + " of " + maxPage);
                        mWebView.loadData(html, "text/html", "utf-8");
                    } else {
                        mCurrentPageNumber = 1;
                        showPage(1);
                    }
                } else {
                    data = new String(mBook.getContents().get(currentPageNumber).getData());
                    mCurrentPageNumber = currentPageNumber;
                    txtPager.setText(currentPageNumber + " of " + maxPage);
                    mWebView.loadData(data, "text/html", "UTF-8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetJavaScriptEnabled")
    private void addEOCPadding() {
        if ( mCurrentPageNumber == 0) return;
        //Add padding to end of section to reduce confusing partial page scrolls
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.evaluateJavascript("document.getElementsByTagName('body')[0].innerHTML += '<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>'", null);
        mWebView.getSettings().setJavaScriptEnabled(false);
    }

}
