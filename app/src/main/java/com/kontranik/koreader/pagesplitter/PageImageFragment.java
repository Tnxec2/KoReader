package com.kontranik.koreader.pagesplitter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.kontranik.koreader.R;

public class PageImageFragment extends Fragment {
    private final static String PAGE_TEXT = "PAGE_TEXT";


    public static PageImageFragment newInstance(CharSequence pageText) {
        PageImageFragment frag = new PageImageFragment();
        Bundle args = new Bundle();
        args.putCharSequence(PAGE_TEXT, pageText);
        frag.setArguments(args);
        return frag;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CharSequence text = getArguments().getCharSequence(PAGE_TEXT);
        TextView pageView = (TextView) inflater.inflate(R.layout.page, container, false);
        pageView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        pageView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.text_size));
        pageView.setLineSpacing(0, 1f);
        pageView.setText(text);

        /*

        // show TextView dimensions

        ViewTreeObserver viewTreeObserver = pageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                float dw = pageView.getWidth();
                float dh = pageView.getHeight();

                pageView.append(dw +" - "+dh);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    pageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }

        });
        */

        return pageView;
    }

}