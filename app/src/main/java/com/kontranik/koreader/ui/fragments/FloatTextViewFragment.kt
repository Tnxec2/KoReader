package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentFloattextviewBinding
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File


class FloatTextViewFragment : DialogFragment() {

    private lateinit var binding: FragmentFloattextviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFloattextviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonFloattextviewClose.setOnClickListener {
            dismiss()
        }

        val textSize = requireArguments().getFloat(QuickMenuFragment.TEXTSIZE)
        // val theme = requireArguments().getString(QuickMenuFragment.THEME)
        val fontname = requireArguments().getString(QuickMenuFragment.FONTNAME, TypefaceRecord.SANSSERIF)
        val fontpath = requireArguments().getString(QuickMenuFragment.FONTPATH, null)
        val selectedFont = if ( fontpath != null ) {
            val f = File(fontpath)
            if (f.exists() && f.isFile && f.canRead())
                TypefaceRecord(fontname, f)
            else
                TypefaceRecord.DEFAULT
        } else {
            TypefaceRecord(fontname)
        }

        val html = requireArguments().getString(CONTENT, "no Content")
        with (binding.textViewFloattextviewContent) {
            text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            typeface = selectedFont.getTypeface()
            this.textSize = textSize
        }
    }


    companion object {
        const val CONTENT = "content"

        fun newInstance(
                html: String,
                textSize: Float,
                font: TypefaceRecord): FloatTextViewFragment {
            val frag = FloatTextViewFragment()
            val args = Bundle()
            args.putString(CONTENT, html)
            args.putFloat(QuickMenuFragment.TEXTSIZE, textSize)
            if ( font.file != null ) args.putString(QuickMenuFragment.FONTPATH, font.file.absolutePath)
            else args.putString(QuickMenuFragment.FONTNAME, font.name)
            frag.arguments = args

            return frag
        }
    }
}