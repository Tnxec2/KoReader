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
import kotlin.math.max
import kotlin.math.min


class QuickMenuFragment : DialogFragment() {

    var listener: QuickMenuDialogListener? = null

    // textSize
    private var textViewTextSIze: TextView? = null
    private val textSizeMin: Float = 6F
    private var textSize: Float = 0F
    private val textSizeStep: Float = 1F
    private val textSizeMax: Float = 50F

    // 1. Defines the listener interface with a method passing back data result.
    interface QuickMenuDialogListener {
        fun onFinishQuickMenuDialog(textSize: Float)
        fun onChangeTextSize(textSize: Float)
        fun onCancelQuickMenu()
        fun onAddBookmark(): Boolean
        fun onShowBookmarklist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.fragment_quick_menu, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStyle(STYLE_NO_FRAME, R.style.AppTheme);


        Log.d("QuickMenuFragment", view.context.theme.toString())

        listener = activity as QuickMenuDialogListener?

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

        val decrease = view.findViewById<ImageButton>(R.id.imageView_quick_menU_textSizeDecrease)
        decrease.setOnClickListener {
            decreaseTextSize()
        }
        val increase = view.findViewById<ImageButton>(R.id.imageView_quick_menU_textSizeIncrease)
        increase.setOnClickListener {
            increaseTextSize()
        }
    }

    private fun save() {
        // Return Data back to activity through the implemented listener
        listener!!.onFinishQuickMenuDialog(textSize)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    private fun decreaseTextSize() {
        textSize = max(textSizeMin, textSize - textSizeStep)
        textViewTextSIze!!.textSize = textSize
        listener!!.onChangeTextSize(textSize)
    }

    private fun increaseTextSize() {
        textSize = min(textSizeMax, textSize + textSizeStep)
        textViewTextSIze!!.textSize = textSize
        listener!!.onChangeTextSize(textSize)
    }

    companion object {
        const val TEXTSIZE = "textSize"

        fun newInstance(textSize: Float): QuickMenuFragment {
            val frag = QuickMenuFragment()
            val args = Bundle()
            args.putFloat(TEXTSIZE, textSize)
            frag.setArguments(args)

            return frag
        }
    }
}