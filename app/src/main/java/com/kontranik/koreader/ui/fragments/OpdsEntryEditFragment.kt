package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentOpdsOverviewEntryEditBinding

class OpdsEntryEditFragment : DialogFragment() {
    private lateinit var binding: FragmentOpdsOverviewEntryEditBinding

    private var mListener: OpdsOverviewEntryEditListener? = null

    private var name: String? = null
    private var url: String? = null
    private var pos: Int = 0

    // 1. Defines the listener interface with a method passing back data result.
    interface OpdsOverviewEntryEditListener{
        fun onOpdsOverviewEntryEditSave(pos: Int, name: String, url: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentOpdsOverviewEntryEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pos = requireArguments().getInt(POSITION, 0)
        name = requireArguments().getString(NAME, name)
        url = requireArguments().getString(URL, url)

        binding.editTextAddopdsentryName.setText(name)
        binding.editTextAddopdsentryUrl.setText(url)

        binding.imageButtonAddopdsentryClose.setOnClickListener {
            dismiss()
        }

        binding.imageButtonAddopdsentrySave.setOnClickListener {
            name = binding.editTextAddopdsentryName.text.toString()
            url = binding.editTextAddopdsentryUrl.text.toString()
            if (!name.isNullOrEmpty() && !url.isNullOrEmpty())
                mListener?.onOpdsOverviewEntryEditSave(pos, name!!, url!!)
            dismiss()
        }
    }

    fun setListener(listener: OpdsOverviewEntryEditListener) {
        this.mListener = listener
    }

    override fun onDetach() {
        super.onDetach()

        mListener = null
    }

    companion object {
        const val POSITION = "position"
        const val NAME = "name"
        const val URL = "url"

        fun newInstance(pos: Int, name: String? = null, url: String? = null): OpdsEntryEditFragment {
            val frag = OpdsEntryEditFragment()
            val args = Bundle()
            args.putInt(POSITION, pos)
            args.putString(NAME, name)
            args.putString(URL, url)
            frag.arguments = args

            return frag
        }
    }
}