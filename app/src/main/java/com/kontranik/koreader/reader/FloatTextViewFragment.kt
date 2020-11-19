package com.kontranik.koreader.reader

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R


class FloatTextViewFragment : DialogFragment() {

    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_floattextview, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val close = view.findViewById<ImageButton>(R.id.imageButton_floattextview_close)
        close.setOnClickListener {
            dismiss()
        }

        textView = view.findViewById(R.id.textView_floattextview_note)

        val html = requireArguments().getString(CONTENT, "no Content")
        textView!!.text = Html.fromHtml(html)
    }

    companion object {
        const val CONTENT = "content"

        fun newInstance(html: String): FloatTextViewFragment {
            val frag = FloatTextViewFragment()
            val args = Bundle()
            args.putString(CONTENT, html)
            frag.arguments = args

            return frag
        }
    }
}