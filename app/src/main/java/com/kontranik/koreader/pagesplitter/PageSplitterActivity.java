package com.kontranik.koreader.pagesplitter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;


import com.kontranik.koreader.R;
import com.kontranik.koreader.model.MyStyle;
import com.kontranik.koreader.model.Word;
import com.kontranik.koreader.test.PageSplitter2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

@SuppressLint("LongLogTag")
@RequiresApi(api = Build.VERSION_CODES.Q)
public class PageSplitterActivity extends FragmentActivity {

    private static final String TAG = "PageSplitterActivity";
    public static final String INTENT_PATH = "BookPath";

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Books/");

    private ViewPager pagesView;

    EpubReader mEpubReader;
    Book mBook;
    Integer maxPage;

    PageSplitter2 pageSplitter;
    TextPagerAdapter textPageAdapter;

    private static int mCurrentPageNumber = 1;
    private static String mFileLocation;

    boolean lastPageReached;
    boolean firstPageReached;

    TextView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pagesplitter_main);
        pagesView = (ViewPager) findViewById(R.id.pages);

        //pageSplitter = new PageSplitter(pagesView.getMeasuredWidth(), pagesView.getMeasuredHeight(), 1, 0);
        //textPageAdapter = new TextPagerAdapter(getSupportFragmentManager(), pageSplitter.getPages());


        testView = (TextView) findViewById(R.id.textView_test);
        testView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        testView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.text_size));

        // to get ViewPager width and height we have to wait global layout
        ViewTreeObserver vto = pagesView.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //pageSplitter = new PageSplitter(pagesView.getMeasuredWidth(), pagesView.getMeasuredHeight(), 1, 0);
                //pagesView.setAdapter(textPageAdapter);
                pagesView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                pagesView.setCurrentItem(0, false);

                pagesView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        Log.d(TAG, "onPageSelected: " + position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        Log.d(TAG, "state: " + state + ", currentItem: " + pagesView.getCurrentItem() + ", last: " + (pagesView.getAdapter().getCount() - 1) );
                        if(ViewPager.SCROLL_STATE_IDLE == state){
                            if( pagesView.getCurrentItem() == pagesView.getAdapter().getCount() - 1 ) {
                                //loadNextBookPage();
                            } else if ( pagesView.getCurrentItem() == 0) {
                                if ( firstPageReached ) {
                                    //prevBookPage();
                                } else {
                                    firstPageReached = true;
                                }
                            }
                            Log.d(TAG, "lastpagereached: " + lastPageReached);
                            Log.d(TAG, "firstPageReached: " + lastPageReached);
                        }
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
                String pageHtml = showPage(mCurrentPageNumber);

                loadPage(pageHtml);
                pagesView.setCurrentItem(0, false);
            }
        });
    }

    public void updateViewpager(){
        textPageAdapter = new TextPagerAdapter(getSupportFragmentManager(), pageSplitter.getPages()); // old items with new items
        pagesView.setAdapter(textPageAdapter);
        //pagesView.setCurrentItem(0, false);
        //textPageAdapter.refreshAdapter();
    }

    void loadPage(String page) {
        int width =  pagesView.getMeasuredWidth() - ( pagesView.getPaddingLeft() + pagesView.getPaddingRight() );
        int height = pagesView.getMeasuredHeight() - ( pagesView.getPaddingTop() + pagesView.getPaddingBottom() );
        pageSplitter = new PageSplitter2(
            width, height,
            testView.getPaint()
        );
        appendPage(page);
    }


    void appendPage(String page) {
        Document document = Jsoup.parse(page);
        Elements elements = document.body().select("*");

        for (Element element : elements) {
            String el = element.normalName();

            Log.d(TAG, el);
            Log.d(TAG, element.ownText());

            if ( el.equals("body") || el.equals("div") || el.equals("br")) {

            } else if ( el.equals("h1"))
                pageSplitter.append(el + ". " + element.ownText(), MyStyle.H1);
            else if ( el.equals("h2"))
                pageSplitter.append(el + ". " + element.ownText(), MyStyle.H2);
            else if ( el.equals("h3"))
                pageSplitter.append(el + ". " + element.ownText(), MyStyle.H3);
            else if ( el.equals("h4"))
                pageSplitter.append(el + ". " + element.ownText(), MyStyle.H4);
            else if ( el.equals("h5"))
                pageSplitter.append(el + ". " + element.ownText(), MyStyle.H5);
            else if ( el.equals("h6"))
                pageSplitter.append(el + ". " + element.ownText(), MyStyle.H6);
            else{
                pageSplitter.append(el, MyStyle.H1);
                pageSplitter.newLine();
                pageSplitter.append(element.ownText(), MyStyle.Paragraph);
            }
            pageSplitter.newLine();


            Log.d(TAG, "pageSplitter size: " + pageSplitter.getPages().size());
        }
        updateViewpager();
    }


    void loadNextBookPage() {
        if ( mCurrentPageNumber > maxPage-1) return;

        String oldPage = showPage(mCurrentPageNumber);
        loadPage(oldPage);

        int lastPage = pageSplitter.getPages().size()-1;

        mCurrentPageNumber++;
        String bookPage = showPage(mCurrentPageNumber);
        appendPage(bookPage);

        pagesView.setCurrentItem(lastPage, false); // set pager to last page
    }

    void nextBookPage() {
        if ( mCurrentPageNumber > maxPage-1) return;
        mCurrentPageNumber++;
        String bookPage = showPage(mCurrentPageNumber);
        loadPage(bookPage);
        pagesView.setCurrentItem(0, true);
        lastPageReached = false;
        firstPageReached = true;
    }

    void prevBookPage() {
        if ( mCurrentPageNumber == 1 ) return;
        mCurrentPageNumber--;
        String bookPage = showPage(mCurrentPageNumber);
        loadPage(bookPage);
        pagesView.setCurrentItem(pagesView.getAdapter().getCount()-1, true);
        firstPageReached = false;
        lastPageReached = true;
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

    private String showPage(int currentPageNumber) {
        if ( currentPageNumber < 0 || currentPageNumber > maxPage-1) return null;

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
                        data = html.replace("{IMAGE_PLACEHOLDER}", image);
                        mCurrentPageNumber = currentPageNumber;
                        return  data;
                        //txtPager.setText(currentPageNumber + " of " + maxPage);
                        //mWebView.loadData(data, "text/html", "utf-8");
                    } else {
                        mCurrentPageNumber = 1;
                        showPage(1);
                    }
                } else {
                    data = new String(mBook.getContents().get(currentPageNumber).getData());
                    mCurrentPageNumber = currentPageNumber;
                    return data;
                    //txtPager.setText(currentPageNumber + " of " + maxPage);
                    //mWebView.loadData(data, "text/html", "UTF-8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
        return null;
    }
}
