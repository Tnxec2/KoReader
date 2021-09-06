package com.kontranik.koreader.reader

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import kotlin.math.max
import kotlin.math.min


class GotoMenuFragment : DialogFragment() {

    private var listener: GotoMenuDialogListener? = null

    private var section: Int = 0

    private var pageInitial: Int = 0
    private var page: Int = 0
    private var maxpage: Int = 0

    private var aSections: Array<String>? = null

    private var textViewPage: TextView? = null
    private var sectionListView: ListView? = null

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
        return inflater.inflate(R.layout.fragment_goto_menu, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = activity as GotoMenuDialogListener?

        section = requireArguments().getInt(SECTION, section)

        page = requireArguments().getInt(PAGE, page)
        pageInitial = page
        maxpage = requireArguments().getInt(MAX_PAGE, maxpage)

        aSections = requireArguments().getStringArray(SECTIONLIST)

        val close = view.findViewById<ImageButton>(R.id.imageButton_gotomenu_close)
        close.setOnClickListener {
            dismiss()
        }

        val save = view.findViewById<ImageButton>(R.id.imageButton_gotomenu_save)
        save.setOnClickListener {
            gotoPage()
        }

        initialGotoPage(view)
        initialGotoList(view)
    }

    private fun initialGotoPage(view: View) {
        textViewPage = view.findViewById(R.id.textView_goto_menu_page)
        updatePageText()

        val decrease = view.findViewById<ImageButton>(R.id.imageView_goto_menu_page_left)
        decrease.setOnClickListener {
            decreasePage()
        }
        val increase = view.findViewById<ImageButton>(R.id.imageView_goto_menu_page_right)
        increase.setOnClickListener {
            increasePage()
        }
    }

    private fun initialGotoList(view: View) {
        sectionListView = view.findViewById(R.id.goto_menu_sectionlist)
        if ( aSections == null || aSections!!.isEmpty()) {
            sectionListView!!.visibility = View.GONE
        } else {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(view.context,
                    android.R.layout.simple_list_item_1, aSections!!)
            sectionListView!!.setAdapter(adapter)
        }
        sectionListView!!.setOnItemClickListener(
                OnItemClickListener { parent, v, position, id ->
            // val selectedItem: String = aSections!!.get(position)
            gotoSection(position)
        })
        sectionListView!!.setSelection(section)
    }

    private fun gotoPage() {
        if ( page != pageInitial)
            listener!!.onFinishGotoMenuDialogPage(page)

        // Close the dialog and return back to the parent activity
        dismiss()
    }

    private fun gotoSection(section: Int) {
        listener!!.onFinishGotoMenuDialogSection(section)

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
        textViewPage!!.text = (page).toString()
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