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
import com.kontranik.koreader.databinding.FragmentQuickMenuBinding
import com.kontranik.koreader.utils.ColoredArrayAdapter
import com.kontranik.koreader.utils.PrefsHelper
import com.kontranik.koreader.utils.TextViewInitiator
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import kotlin.math.max
import kotlin.math.min


class QuickMenuFragment : DialogFragment() {

    private lateinit var binding: FragmentQuickMenuBinding

    private var listener: QuickMenuDialogListener? = null

    // textSize
    private var textSize: Float = 0F
    private val textSizeStep: Float = 1F

    private var lineSpacingMultiplier: Float = 1f
    private var letterSpacing: Float = 0.05f

    private var colorTheme: String = PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT

    private val typeFace: Typeface = TypefaceRecord.DEFAULT.getTypeface()

    // 1. Defines the listener interface with a method passing back data result.
    interface QuickMenuDialogListener {
        fun onFinishQuickMenuDialog(textSize: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorTheme: String)
        fun onChangeTextSize(textSize: Float)
        fun onChangeLineSpacing(lineSpacingMultiplier: Float)
        fun onChangeLetterSpacing(letterSpacing: Float)
        fun onCancelQuickMenu()
        fun onAddBookmark()
        fun onShowBookmarklist()
        fun onChangeColorTheme(colorTheme: String, colorThemeIndex: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentQuickMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("QuickMenuFragment", view.context.theme.toString())

        binding.imageButtonQuickmenuBack.setOnClickListener {
            listener!!.onCancelQuickMenu()
            dismiss()
        }

        binding.imageButtonQuickmenuSave.setOnClickListener {
            save()
        }

        val settings = requireActivity().getSharedPreferences(ReaderActivity.PREFS_FILE, AppCompatActivity.MODE_PRIVATE)

        var bookPath: String? = null
        if ( settings.contains(PrefsHelper.PREF_BOOK_PATH) ) {
            bookPath = settings!!.getString(PrefsHelper.PREF_BOOK_PATH, null)
        }

        binding.imageButtonQuickmenuBookinfo.setOnClickListener {
            openBookInfo(bookPath)
        }
        if ( bookPath == null) binding.imageButtonQuickmenuBookinfo.visibility = View.GONE

        initialTextSize()
        initialTheming()
        initialLineSpacing()
        initialLetterSpacing()
        initialBookmarks()

        listener = activity as QuickMenuDialogListener

    }

    private fun initialTheming() {

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        colorTheme = prefs.getString(
            PrefsHelper.PREF_KEY_COLOR_SELECTED_THEME,
            PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT)
            ?: PrefsHelper.PREF_COLOR_SELECTED_THEME_DEFAULT

        val valArray = resources.getStringArray(R.array.selected_theme_values)
        val entryArray = resources.getStringArray(R.array.selected_theme_entries)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ColoredArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,  entryArray.asList(), textSize, typeFace)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.spinnerQuickMenuThemes.adapter = adapter

        binding.spinnerQuickMenuThemes.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                colorTheme = valArray[position]
                listener!!.onChangeColorTheme(colorTheme, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // sometimes you need nothing here
            }
        }

        binding.spinnerQuickMenuThemes.setSelection(valArray.indexOf(colorTheme) )

    }

    private fun initialLineSpacing() {
        val valArray = resources.getStringArray(R.array.line_spacing_values)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.line_spacing_entries,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinnerQuickMenuLineSpacing.adapter = adapter
        }
        binding.spinnerQuickMenuLineSpacing.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                lineSpacingMultiplier = valArray[position].toFloat()
                listener!!.onChangeLineSpacing(lineSpacingMultiplier)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                // sometimes you need nothing here
            }
        }

        binding.spinnerQuickMenuLineSpacing.setSelection(valArray.indexOf(lineSpacingMultiplier.toString()) )
    }

    private fun initialLetterSpacing() {
        val valArray = resources.getStringArray(R.array.letter_spacing_values)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.letter_spacing_entries,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinnerQuickMenuLetterSpacing.adapter = adapter
        }
        binding.spinnerQuickMenuLetterSpacing.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                letterSpacing = valArray[position].toFloat()
                listener!!.onChangeLetterSpacing(letterSpacing)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                // sometimes you need nothing here
            }
        }

        binding.spinnerQuickMenuLetterSpacing.setSelection(valArray.indexOf(letterSpacing.toString()) )
    }

    private fun initialBookmarks() {
        binding.imageViewQuickMenuAddBookmark.setOnClickListener {
            listener!!.onAddBookmark()
            dismiss()
        }
        binding.imageViewQuickMenuListBookmark.setOnClickListener {
            listener!!.onShowBookmarklist()
            dismiss()
        }
    }

    private fun initialTextSize() {
        TextViewInitiator.initiateTextView(binding.textViewQuickMenUTextSizeExample, getString(R.string.textSizeExampleText))

        val defaultTextSize = requireContext().resources.getDimension(R.dimen.text_size)

        val typedValue = TypedValue()
        requireContext().resources.getValue(R.dimen.line_spacing, typedValue, true)
        val defaultLineSpacingMultiplier = typedValue.float
        requireContext().resources.getValue(R.dimen.letter_spacing, typedValue, true)
        val defaultLetterSpacing = typedValue.float

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val fontpath = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_PATH_NORMAL, null)
        textSize = prefs.getFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, defaultTextSize)
        val lineSpacingMultiplierString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, defaultLineSpacingMultiplier.toString() )
        if ( lineSpacingMultiplierString != null) lineSpacingMultiplier = lineSpacingMultiplierString.toFloat()
        val letterSpacingString = prefs.getString(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, defaultLetterSpacing.toString() )
        if ( letterSpacingString != null) letterSpacing = letterSpacingString.toFloat()
        val fontname = prefs.getString(PrefsHelper.PREF_KEY_BOOK_FONT_NAME_NORMAL, TypefaceRecord.DEFAULT.name)!!

        binding.textViewQuickMenUTextSizeExample.textSize = textSize

        val selectedFont = if ( fontpath != null ) {
            val f = File(fontpath)
            if (f.exists() && f.isFile && f.canRead())
                TypefaceRecord(fontname, f)
            else
                TypefaceRecord.DEFAULT
        } else {
            TypefaceRecord(fontname)
        }
        binding.textViewQuickMenUTextSizeExample.typeface = selectedFont.getTypeface()


        binding.imageViewQuickMenUTextSizeDecrease.setOnClickListener {
            decreaseTextSize()
        }
        binding.imageViewQuickMenUTextSizeIncrease.setOnClickListener {
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
        val prefEditor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
        prefEditor.putString(PrefsHelper.PREF_KEY_COLOR_SELECTED_THEME, colorTheme)
        prefEditor.putFloat(PrefsHelper.PREF_KEY_BOOK_TEXT_SIZE, textSize)
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_LINE_SPACING, lineSpacingMultiplier.toString())
        prefEditor.putString(PrefsHelper.PREF_KEY_BOOK_LETTER_SPACING, letterSpacing.toString())
        prefEditor.apply()

        // Return Data back to activity through the implemented listener
        listener!!.onFinishQuickMenuDialog(textSize, lineSpacingMultiplier, letterSpacing, colorTheme)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    private fun decreaseTextSize() {
        textSize = max(PrefsHelper.textSizeMin, textSize - textSizeStep)
        binding.textViewQuickMenUTextSizeExample.textSize = textSize
        listener!!.onChangeTextSize(textSize)
    }

    private fun increaseTextSize() {
        textSize = min(PrefsHelper.textSizeMax, textSize + textSizeStep)
        binding.textViewQuickMenUTextSizeExample.textSize = textSize
        listener!!.onChangeTextSize(textSize)
    }

    companion object {
        const val THEME = "theme"
        const val TEXTSIZE = "textSize"
        const val FONTPATH = "fontpath"
        const val FONTNAME = "fontname"
    }

}