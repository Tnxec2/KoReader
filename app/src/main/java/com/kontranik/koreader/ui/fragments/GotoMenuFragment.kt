package com.kontranik.koreader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentGotoMenuBinding
import kotlin.math.max
import kotlin.math.min


class GotoMenuFragment : DialogFragment() {
    private lateinit var binding: FragmentGotoMenuBinding

    private var mListener: GotoMenuDialogListener? = null

    private var section: Int = 0

    private var pageInitial: Int = 0
    private var page: Int = 0
    private var maxpage: Int = 0

    private var aSections: Array<String>? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface GotoMenuDialogListener {
        fun onFinishGotoMenuDialogSection(section: Int)
        fun onFinishGotoMenuDialogPage(page: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentGotoMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        section = requireArguments().getInt(SECTION, section)

        page = requireArguments().getInt(PAGE, page)
        pageInitial = page
        maxpage = requireArguments().getInt(MAX_PAGE, maxpage)

        aSections = requireArguments().getStringArray(SECTIONLIST)

        binding.imageButtonGotomenuClose.setOnClickListener {
            dismiss()
        }

        binding.imageButtonGotomenuSave.setOnClickListener {
            gotoPage()
        }

        initialGotoPage()
        initialGotoList(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GotoMenuDialogListener) {
            mListener = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement GotoMenuDialogListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()

        mListener = null
    }

    private fun initialGotoPage() {
        updatePageText()

        binding.imageViewGotoMenuPageLeft.setOnClickListener {
            decreasePage()
        }
        binding.imageViewGotoMenuPageRight.setOnClickListener {
            increasePage()
        }
    }

    private fun initialGotoList(view: View) {
        if ( aSections == null || aSections!!.isEmpty()) {
            binding.gotoMenuSectionlist.visibility = View.GONE
        } else {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(view.context,
                    android.R.layout.simple_list_item_1, aSections!!)
            binding.gotoMenuSectionlist.adapter = adapter
        }
        binding.gotoMenuSectionlist.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                // val selectedItem: String = aSections!!.get(position)
                gotoSection(position)
            }
        binding.gotoMenuSectionlist.setSelection(section)
    }

    private fun gotoPage() {
        if ( page != pageInitial)
            mListener!!.onFinishGotoMenuDialogPage(page)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    private fun gotoSection(section: Int) {
        mListener!!.onFinishGotoMenuDialogSection(section)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    private fun decreasePage() {
        page--
        page = max(0, page)
        updatePageText()
    }

    private fun increasePage() {
        page++
        page = min(maxpage, page)
        updatePageText()
    }

    private fun updatePageText() {
        binding.textViewGotoMenuPage.text = (page).toString()
    }

    companion object {
        const val SECTION = "section"
        const val PAGE = "page"
        const val MAX_PAGE = "maxpage"
        const val SECTIONLIST = "sectionlist"

        fun newInstance(section: Int, page: Int, maxPage: Int, sectionList: Array<String>): GotoMenuFragment {
            val frag = GotoMenuFragment()
            val args = Bundle()
            args.putInt(SECTION, section)
            args.putInt(PAGE, page)
            args.putInt(MAX_PAGE, maxPage)
            args.putStringArray(SECTIONLIST, sectionList)
            frag.arguments = args

            return frag
        }
    }
}