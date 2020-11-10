package com.kontranik.koreader.reader

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import com.kontranik.koreader.R

class TextViewInitiator {

    companion object {
        fun initiateTextView(textView: TextView, startText: String) {
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
            // textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textView.context.resources.getDimension(R.dimen.text_size))
            textView.setLineSpacing(0f, 1f)
            textView.text = startText
        }
    }
}