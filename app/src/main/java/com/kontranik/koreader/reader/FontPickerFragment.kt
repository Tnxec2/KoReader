package com.kontranik.koreader.reader

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.FontPickerListItemAdapter
import com.kontranik.koreader.utils.typefacefactory.FontManager
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import java.util.*


class FontPickerFragment :
        DialogFragment(),
        FontPickerListItemAdapter.FontPickerListAdapterClickListener {

    var listener: FontPickerDialogListener? = null

    private var fontList: MutableList<TypefaceRecord>? = null

    private var selectedFont: TypefaceRecord? = null
    private var textSize: Float = 0F

    // 1. Defines the listener interface with a method passing back data result.
    interface FontPickerDialogListener {
        fun onSaveFontPickerDialog(font: TypefaceRecord?)
    }

    fun setCallBack(listener: FontPickerDialogListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.fragment_font_menu, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStyle(STYLE_NO_FRAME, R.style.AppTheme);

        textSize = requireArguments().getFloat(TEXTSIZE, textSizeMin)


        val fonts: HashMap<String, File>?

        fontList = mutableListOf(
                TypefaceRecord(name = TypefaceRecord.SANSSERIF),
                TypefaceRecord(name = TypefaceRecord.SERIF),
                TypefaceRecord(name = TypefaceRecord.MONO))
        try {
            fonts = FontManager.enumerateFonts(false)
            // val collectedFonts = ZLTTFInfoDetector().collectFonts(fonts!!.values)
            if ( fonts != null) {
                for (fontName in fonts.keys) {
                    fontList!!.add(TypefaceRecord(
                            name = fontName,
                            file = fonts[fontName]!!))
                }
            }
        } catch (e: Exception) {
            if (e is RuntimeException) {
                throw e
            }
            e.printStackTrace()
            dismiss()
            return
        }

        val fontListView = view.findViewById<RecyclerView>(R.id.reciclerView_font_list)

        fontListView.adapter = FontPickerListItemAdapter(
                view.context,
                textSize,
                fontList,
                this
        )

        val fontpath = requireArguments().getString(FONTPATH, null)
        val fontname = requireArguments().getString(FONTNAME, TypefaceRecord.SANSSERIF)
        var pos = 0
        for ( i in 0 .. fontList!!.size) {
            if ( fontpath != null && fontList!![i].file != null && fontpath == fontList!![i].file!!.absolutePath ) {
                pos = i
                break
            } else if ( fontname == fontList!![i].name) {
                pos = i
                break
            }
        }
        fontListView.scrollToPosition(pos)

        val itemListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            selectedFont = fontList!![position]
        }

    }

    private fun save() {
        // Return Data back to activity through the implemented listener
        listener!!.onSaveFontPickerDialog(selectedFont)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    companion object {
        const val TEXTSIZE = "textsize"
        const val FONTPATH = "fontpath"
        const val FONTNAME = "fontname"

        fun newInstance(textSize: Float, font: TypefaceRecord): FontPickerFragment {
            val frag = FontPickerFragment()
            val args = Bundle()
            args.putFloat(TEXTSIZE, textSize)

            if ( font.file != null ) args.putString(QuickMenuFragment.FONTPATH, font.file.absolutePath)
            else args.putString(QuickMenuFragment.FONTNAME, font.name)

            frag.arguments = args
            return frag
        }

        private const val textSizeMin: Float = 6F
    }

    override fun onFontlistItemClickListener(position: Int) {
        selectedFont = fontList!![position]
        save()
    }


}