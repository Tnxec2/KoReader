package com.kontranik.koreader.reader

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentFontMenuBinding
import com.kontranik.koreader.utils.FontPickerListItemAdapter
import com.kontranik.koreader.utils.PrefsHelper
import com.kontranik.koreader.utils.typefacefactory.FontManager
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import java.io.File
import java.util.*


class FontPickerFragment :
        DialogFragment(),
        FontPickerListItemAdapter.FontPickerListAdapterClickListener {

    private lateinit var binding: FragmentFontMenuBinding

    private var listener: FontPickerDialogListener? = null

    private var fontList: MutableList<TypefaceRecord> = mutableListOf()

    private var selectedFont: TypefaceRecord? = null

    private var showSystemFonts: Boolean = false
    private var showNotoFonts: Boolean = false

    private var permissionGranted = false

    private var mView: View? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface FontPickerDialogListener {
        fun onSaveFontPickerDialog(font: TypefaceRecord?)
    }

    fun setCallBack(listener: FontPickerDialogListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFontMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        showSystemFonts = prefs.getBoolean(PrefsHelper.PREF_KEY_USE_SYSTEM_FONTS, false)
        showNotoFonts = prefs.getBoolean(PrefsHelper.PREF_KEY_SHOW_NOTO_FONTS, false)

        mView = view

        if ( ! permissionGranted) {
            checkPermissions()
        }

        loadFonts()
    }

    private fun save() {
        // Return Data back to activity through the implemented listener
        listener!!.onSaveFontPickerDialog(selectedFont)

        // Close the dialog and return back to the parent activity
        if ( !parentFragmentManager.popBackStackImmediate() ) {
            dismiss()
        }
    }

    private fun loadFonts() {
        val fonts: HashMap<String, File>?

        fontList = mutableListOf(
                TypefaceRecord(name = TypefaceRecord.SANSSERIF),
                TypefaceRecord(name = TypefaceRecord.SERIF),
                TypefaceRecord(name = TypefaceRecord.MONO))
        try {
            fonts = FontManager.enumerateFonts(requireContext(), showSystemFonts, showNotoFonts)
            // val collectedFonts = ZLTTFInfoDetector().collectFonts(fonts!!.values)
            val collectedFonts: MutableList<TypefaceRecord> = mutableListOf()
            if ( fonts != null) {
                for (fontName in fonts.keys) {
                    collectedFonts.add(TypefaceRecord(
                            name = fontName,
                            file = fonts[fontName]!!))
                }
            }
            collectedFonts.sortBy { it.name }
            fontList.addAll(collectedFonts)
        } catch (e: Exception) {
            if (e is RuntimeException) {
                throw e
            }
            e.printStackTrace()
            dismiss()
            return
        }

        binding.reciclerViewFontList.adapter = FontPickerListItemAdapter(
                mView!!.context,
                // textSize,
                fontList,
                this
        )

        val fontpath = requireArguments().getString(FONTPATH, null)
        val fontname = requireArguments().getString(FONTNAME, TypefaceRecord.SANSSERIF)
        var pos = 0
        for ( i in 0 until fontList.size) {
            if ( fontpath != null && fontList[i].file != null && fontpath == fontList[i].file!!.absolutePath ) {
                pos = i
            } else if ( fontname == fontList[i].name) {
                pos = i
            }
        }
        if ( pos < fontList.size) binding.reciclerViewFontList.scrollToPosition(pos)

        AdapterView.OnItemClickListener { parent, v, position, id ->
            selectedFont = fontList[position]
        }
    }



    override fun onFontlistItemClickListener(position: Int) {
        selectedFont = fontList[position]
        save()
    }

    fun isExternalStorageReadable(): Boolean {
        val state: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state ||
                Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    private fun checkPermissions(): Boolean {
        if (!isExternalStorageReadable() ) {
            Toast.makeText(requireContext(), "external storage not available", Toast.LENGTH_LONG).show()
            return false
        }
        val permissionCheck = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true
                    Toast.makeText(requireContext(), "read permissions granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "need permissions to read external storage", Toast.LENGTH_LONG).show()
                }
                loadFonts()
            }
        }
    }

    companion object {
        const val READ_STORAGE_PERMISSION_REQUEST_CODE=0x3

        const val TEXTSIZE = "textsize"
        const val FONTPATH = "fontpath"
        const val FONTNAME = "fontname"

        fun newInstance(font: TypefaceRecord): FontPickerFragment {
            val frag = FontPickerFragment()
            val args = Bundle()
            if ( font.file != null ) args.putString(QuickMenuFragment.FONTPATH, font.file.absolutePath)
            else args.putString(QuickMenuFragment.FONTNAME, font.name)

            frag.arguments = args
            return frag
        }

        private const val textSizeMin: Float = 6F
    }
}