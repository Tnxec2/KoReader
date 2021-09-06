package com.kontranik.koreader.reader

import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivity
import com.kontranik.koreader.utils.ColoredArrayAdapter
import com.kontranik.koreader.utils.PrefsHelper
import com.kontranik.koreader.utils.TextViewInitiator
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import kotlin.math.max
import kotlin.math.min


class QuickMenuFragment : DialogFragment() {

    private var listener: QuickMenuDialogListener? = null

    // textSize
    private var textViewTextSIze: TextView? = null
    private var textSize: Float = 0F
    private val textSizeStep: Float = 1F

    private var lineSpacing: Float = 1f
    private var letterSpacing: Float = 0.05f

    private var theme: String = "Auto"

    private val typeFace: Typeface = TypefaceRecord.DEFAULT.getTypeface()

    // 1. Defines the listener interface with a method passing back data result.
    interface QuickMenuDialogListener {
        fun onFinishQuickMenuDialog(textSize: Float, lineSpacing: Float, letterSpacing: Float, theme: String)
        fun onChangeTextSize(textSize: Float)
        fun onChangeLineSpacing(lineSpacing: Float)
        fun onChangeLetterSpacing(letterSpacing: Float)
        fun onCancelQuickMenu()
        fun onAddBookmark(): Boolean
        fun onShowBookmarklist()
        fun onChangeColorTheme(theme: String)
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

        val close = view.findViewById<ImageButton>(R.id.imageButton_quickmenu_back)
        close.setOnClickListener {
            listener!!.onCancelQuickMenu()
            dismiss()
        }

        val save = view.findViewById<ImageButton>(R.id.imageButton_quickmenu_save)
        save.setOnClickListener {
            save()
        }

        val settings = requireActivity().getSharedPreferences(ReaderActivity.PREFS_FILE, AppCompatActivity.MODE_PRIVATE)

        var bookPath: String? = null
        if ( settings.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            bookPath = settings!!.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        val bookinfo = view.findViewById<ImageButton>(R.id.imageButton_quickmenu_bookinfo)
        bookinfo.setOnClickListener {
            openBookInfo(bookPath)
        }
        if ( bookPath == null) bookinfo.visibility = View.GONE

        initialTextSize(view)
        initialTheming(view)
        initialLineSpacing(view)
        initialLetterSpacing(view)
        initialBookmarks(view)

        listener = activity as QuickMenuDialogListener

    }

    private fun initialTheming(view: View) {

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        theme = prefs.getString(PrefsHelper.PREF_KEY_COLOR_SELECTED_THEME, "1") ?: "1"

        val spinner: Spinner = view.findViewById(R.id.spinner_quick_menu_themes)

        val valArray = view.resources.getStringArray(R.array.selected_theme_values)
        val entryArray = view.resources.getStringArray(R.array.selected_theme_entries)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ColoredArrayAdapter(view.context, android.R.layout.simple_spinner_item,  entryArray.asList(), textSize, typeFace)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                theme = valArray[position]
                listener!!.onChangeColorTheme(theme)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // sometimes you need nothing here
            }
        }

        spinner.setSelection(valArray.indexOf(theme) )

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

    private fun initialLetterSpacing(view: View) {
        val spinner: Spinner = view.findViewById(R.id.spinner_quick_menu_letter_spacing)

        val valArray = view.resources.getStringArray(R.array.letter_spacing_values)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                view.context,
                R.array.letter_spacing_entries,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                letterSpacing = valArray[position].toFloat()
                listener!!.onChangeLetterSpacing(letterSpacing)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                // sometimes you need nothing here
            }
        }

        spinner.setSelection(valArray.indexOf(letterSpacing.toString()) )
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
        requireContext().resources.getValue(R.dimen.letter_spacing, typedValue, true)
        val defaultLetterSpacing = typedValue.float

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val fontpath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL, null)
        textSize = prefs.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)
        val lineSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, defaultLineSpacing.toString() )
        if ( lineSpacingString != null) lineSpacing = lineSpacingString.toFloat()
        val letterSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, defaultLetterSpacing.toString() )
        if ( letterSpacingString != null) letterSpacing = letterSpacingString.toFloat()
        val fontname = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL, TypefaceRecord.DEFAULT.name)!!

        textViewTextSIze!!.textSize = textSize

        val selectedFont = if ( fontpath != null ) {
            val f = File(fontpath)
            if (f.exists() && f.isFile && f.canRead())
                TypefaceRecord(fontname, f)
            else
                TypefaceRecord.DEFAULT
        } else {
            TypefaceRecord(fontname)
        }
        textViewTextSIze!!.typeface = selectedFont.getTypeface()


        val decrease = view.findViewById<ImageButton>(R.id.imageView_quick_menU_textSizeDecrease)
        decrease.setOnClickListener {
            decreaseTextSize()
        }
        val increase = view.findViewById<ImageButton>(R.id.imageView_quick_menU_textSizeIncrease)
        increase.setOnClickListener {
            increaseTextSize()
        }
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment: BookInfoFragment = BookInfoFragment.newInstance(bookUri)
            bookInfoFragment.show(requireActivity().supportFragmentManager, "fragment_bookinfo")
        }
    }

    private fun save() {
        val prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        prefEditor.putString(PrefsHelper.PREF_KEY_COLOR_SELECTED_THEME, theme)
        prefEditor.putFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, textSize)
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, lineSpacing.toString())
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, letterSpacing.toString())
        prefEditor.apply()

        // Return Data back to activity through the implemented listener
        listener!!.onFinishQuickMenuDialog(textSize, lineSpacing, letterSpacing, theme)

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

    companion object {
        const val THEME = "theme"
        const val TEXTSIZE = "textSize"
        const val LINESPACING = "lineSpacing"
        const val LETTERSPACING = "letterSpacing"
        const val FONTPATH = "fontpath"
        const val FONTNAME = "fontname"
    }

}