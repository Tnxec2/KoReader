package com.kontranik.koreader.reader

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.PrefsHelper
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

    private var lineSpacing: Float = 1f

    private var selectedFont: TypefaceRecord = TypefaceRecord.DEFAULT

    // 1. Defines the listener interface with a method passing back data result.
    interface QuickMenuDialogListener {
        fun onFinishQuickMenuDialog(textSize: Float, lineSpacing: Float, font: TypefaceRecord?)
        fun onChangeTextSize(textSize: Float)
        fun onChangeLineSpacing(lineSpacing: Float)
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
        initialLineSpacing(view)
        initialBookmarks(view)

    }

    private fun initialLineSpacing(view: View) {
        val spinner: Spinner = view.findViewById(R.id.spinner_quick_menu_line_spacing)

        val valArray = view.resources.getStringArray(R.array.line_spacing_values)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                view.context,
                R.array.line_spacing_entries,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                lineSpacing = valArray[position].toFloat()
                listener!!.onChangeLineSpacing(lineSpacing)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                // sometimes you need nothing here
            }
        }

        spinner.setSelection(valArray.indexOf(lineSpacing.toString()) )
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

        val defaultTextSize = requireContext().resources.getDimension(R.dimen.text_size)

        val typedValue = TypedValue()
        requireContext().resources.getValue(R.dimen.line_spacing, typedValue, true)
        val defaultLineSpacing = typedValue.float

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val fontpath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH, null)
        textSize = prefs.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)
        val lineSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, defaultLineSpacing.toString() )
        if ( lineSpacingString != null) lineSpacing = lineSpacingString.toFloat()
        val fontname = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME, TypefaceRecord.DEFAULT.name)!!

        textViewTextSIze!!.textSize = textSize

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
        val prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        prefEditor.putFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, textSize)
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, lineSpacing.toString())
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH, selectedFont.file?.absolutePath)
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME, selectedFont.name)
        prefEditor.apply()

        // Return Data back to activity through the implemented listener
        listener!!.onFinishQuickMenuDialog(textSize, lineSpacing, selectedFont)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    private fun decreaseTextSize() {
        textSize = max(PrefsHelper.textSizeMin, textSize - textSizeStep)
        textViewTextSIze!!.textSize = textSize
        listener!!.onChangeTextSize(textSize)
    }

    private fun increaseTextSize() {
        textSize = min(PrefsHelper.textSizeMax, textSize + textSizeStep)
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
        const val LINESPACING = "lineSpacing"
        const val FONTPATH = "fontpath"
        const val FONTNAME = "fontname"

    }

}