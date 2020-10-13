package com.kontranik.koreader.pagesplitter

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.kontranik.koreader.R

class PageFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val text = arguments!!.getCharSequence(PAGE_TEXT)
        val pageView = inflater.inflate(R.layout.page, container, false) as TextView
        TextViewInitiator.initiateTextView(pageView)
        pageView.text = text

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
        return pageView
    }

    companion object {
        private const val PAGE_TEXT = "PAGE_TEXT"
        @JvmStatic
        fun newInstance(pageText: CharSequence?): PageFragment {
            val frag = PageFragment()
            val args = Bundle()
            args.putCharSequence(PAGE_TEXT, pageText)
            frag.arguments = args
            return frag
        }
    }
}