package com.kontranik.koreader.utils

import android.graphics.Typeface
import android.widget.TextView

class TextViewInitiator {

    companion object {
        fun initiateTextView(textView: TextView, startText: String) {
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
            textView.setLineSpacing(0f, 1f)
            textView.letterSpacing = 0.05f
            textView.text = startText
        }
    }
}