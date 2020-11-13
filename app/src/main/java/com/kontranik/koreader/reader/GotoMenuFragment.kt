package com.kontranik.koreader.reader

import android.os.Bundle
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


class GotoMenuFragment : DialogFragment() {

    var listener: GotoMenuDialogListener? = null

    var section: Int = 0
    var maxsection: Int = 0
    var textViewSection: TextView? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface GotoMenuDialogListener {
        fun onFinishGotoMenuDialog(section: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.fragment_goto_menu, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStyle(STYLE_NO_FRAME, R.style.AppTheme);

        listener = activity as GotoMenuDialogListener?

        section = requireArguments().getInt(SECTION, section)
        maxsection = requireArguments().getInt(MAX_SECTION, maxsection)


        val close = view.findViewById<ImageButton>(R.id.imageButton_quickmenu_back)
        close.setOnClickListener {
            dismiss()
        }

        val save = view.findViewById<ImageButton>(R.id.imageButton_quickmenu_save)
        save.setOnClickListener {
            save()
        }

        initialGotoSection(view)
    }


    private fun initialGotoSection(view: View) {
        textViewSection = view.findViewById(R.id.textView_goto_menu_section)
        updateSectionText()

        val decrease = view.findViewById<ImageButton>(R.id.imageView_goto_menu_section_left)
        decrease.setOnClickListener {
            decreaseSection()
        }
        val increase = view.findViewById<ImageButton>(R.id.imageView_goto_menu_section_right)
        increase.setOnClickListener {
            increaseSection()
        }
    }

    private fun save() {
        // Return Data back to activity through the implemented listener
        listener!!.onFinishGotoMenuDialog(section)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    private fun decreaseSection() {
        section--
        section = max(0, section)
        updateSectionText()
    }

    private fun increaseSection() {
        section++
        section = min(maxsection-1, section)
        updateSectionText()
    }

    private fun updateSectionText() {
        textViewSection!!.text = (section+1).toString()
    }

    companion object {
        const val SECTION = "section"
        const val MAX_SECTION = "maxsection"

        fun newInstance(section: Int, maxSection: Int): GotoMenuFragment {
            val frag = GotoMenuFragment()
            val args = Bundle()
            args.putInt(SECTION, section)
            args.putInt(MAX_SECTION, maxSection)
            frag.arguments = args

            return frag
        }
    }
}