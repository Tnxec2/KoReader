package com.kontranik.koreader.pagesplitter

import android.graphics.Typeface
import android.graphics.text.LineBreaker
import android.os.Build
import android.text.Layout
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.kontranik.koreader.R

class TextViewInitiator {

    companion object {
        fun initiateTextView(textView: TextView) {
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textView.context.resources.getDimension(R.dimen.text_size))
            textView.setLineSpacing(0f, 1f)
        }
    }
}