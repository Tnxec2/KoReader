package com.kontranik.koreader.reader

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.TextViewInitiator

import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File

import kotlin.math.max
import kotlin.math.min

class QuickMenuFragment : DialogFragment(), FontPickerFragment.FontPickerDialogListener {

    private var listener: QuickMenuDialogListener? = null

    // textSize
    private var textViewTextSIze: TextView? = null
    private var textSize: Float = 0F
    private val textSizeStep: Float = 1F

    private var selectedFont: TypefaceRecord = TypefaceRecord.DEFAULT


    // 1. Defines the listener interface with a method passing back data result.
    interface QuickMenuDialogListener {
        fun onFinishQuickMenuDialog(textSize: Float, font: TypefaceRecord?)
        fun onChangeTextSize(textSize: Float)
        fun onCancelQuickMenu()
        fun onAddBookmark(): Boolean
        fun onShowBookmarklist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_quick_menu, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("QuickMenuFragment", view.context.theme.toString())

        listener = activity as QuickMenuDialogListener

        textSize = requireArguments().getFloat(TEXTSIZE, textSizeMin)

        val close = view.findViewById<ImageButton>(R.id.imageButton_quickmenu_back)
        close.setOnClickListener {
            listener!!.onCancelQuickMenu()
            dismiss()
        }

        val save = view.findViewById<ImageButton>(R.id.imageButton_quickmenu_save)
        save.setOnClickListener {
            save()
        }

        initialTextSize(view)
        initialBookmarks(view)

    }

    private fun initialBookmarks(view: View) {
        val addBookmark = view.findViewById<ImageButton>(R.id.imageView_quick_menu_addBookmark)
        addBookmark.setOnClickListener {
            listener!!.onAddBookmark()
            dismiss()
        }
        val listBookmark = view.findViewById<ImageButton>(R.id.imageView_quick_menu_listBookmark)
        listBookmark.setOnClickListener {
            listener!!.onShowBookmarklist()
            dismiss()
        }
    }

    private fun initialTextSize(view: View) {
        textViewTextSIze = view.findViewById(R.id.textView_quick_menU_textSizeExample)
        TextViewInitiator.initiateTextView(textViewTextSIze!!, getString(R.string.textSizeExampleText))
        textViewTextSIze!!.textSize = textSize

        val fontname = requireArguments().getString(FONTNAME, TypefaceRecord.SANSSERIF)
        val fontpath = requireArguments().getString(FONTPATH, null)

        selectedFont = if ( fontpath != null ) {
            val f = File(fontpath)
            if (f.exists() && f.isFile && f.canRead())
                TypefaceRecord(fontname, f)
            else
                TypefaceRecord.DEFAULT
        } else {
            TypefaceRecord(fontname)
        }
        textViewTextSIze!!.typeface = selectedFont.getTypeface()

        textViewTextSIze!!.setOnClickListener {
            openFontPicker(view)
        }

        val decrease = view.findViewById<ImageButton>(R.id.imageView_quick_menU_textSizeDecrease)
        decrease.setOnClickListener {
            decreaseTextSize()
        }
        val increase = view.findViewById<ImageButton>(R.id.imageView_quick_menU_textSizeIncrease)
        increase.setOnClickListener {
            increaseTextSize()
        }
    }

    private fun openFontPicker(view: View) {
        val fontPickerFragment: FontPickerFragment = FontPickerFragment.newInstance(textSize, selectedFont)
        fontPickerFragment.setCallBack(this)
        fontPickerFragment.show(requireActivity().supportFragmentManager, "fragment_font_picker")
    }

    private fun save() {
        // Return Data back to activity through the implemented listener
        listener!!.onFinishQuickMenuDialog(textSize, selectedFont)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    private fun decreaseTextSize() {
        textSize = max(Companion.textSizeMin, textSize - textSizeStep)
        textViewTextSIze!!.textSize = textSize
        listener!!.onChangeTextSize(textSize)
    }

    private fun increaseTextSize() {
        textSize = min(Companion.textSizeMax, textSize + textSizeStep)
        textViewTextSIze!!.textSize = textSize
        listener!!.onChangeTextSize(textSize)
    }

    override fun onSaveFontPickerDialog(font: TypefaceRecord?) {
        if ( font != null ) {
            selectedFont = font
            textViewTextSIze!!.typeface = font.getTypeface()
        }
    }

    companion object {
        const val TEXTSIZE = "textSize"
        const val FONTPATH = "fontpath"
        const val FONTNAME = "fontname"

        fun newInstance(textSize: Float, font: TypefaceRecord): QuickMenuFragment {
            val frag = QuickMenuFragment()
            val args = Bundle()
            args.putFloat(TEXTSIZE, textSize)

            if ( font.file != null ) args.putString(FONTPATH, font.file.absolutePath)
            else args.putString(FONTNAME, font.name)

            frag.arguments = args
            return frag
        }

        private const val textSizeMax: Float = 50F
        private const val textSizeMin: Float = 6F
    }

}