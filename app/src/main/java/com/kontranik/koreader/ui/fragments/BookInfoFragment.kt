package com.kontranik.koreader.ui.fragments

import android.app.AlertDialog
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.ui.bookinfo.BookInfoContent
import com.kontranik.koreader.compose.ui.bookinfo.toBookInfoDetails
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.databinding.FragmentBookinfoBinding
import com.kontranik.koreader.parser.EbookHelper


class BookInfoFragment: Fragment()  {

    private lateinit var binding: FragmentBookinfoBinding

    private var mListener: BookInfoListener? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface BookInfoListener {
        fun onBookInfoFragmentReadBook(bookUri: String)
        fun onBookInfoFragmentDeleteBook(bookUri: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentBookinfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookPath = requireArguments().getString(BOOK_PATH, null)
        if ( bookPath == null)
            requireActivity().supportFragmentManager.popBackStack()

        binding.imageButtonBookinfoClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imageButtonBookinfoRead.setOnClickListener {
            if ( mListener != null) {
                mListener?.onBookInfoFragmentReadBook(bookPath!!)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        val uri = Uri.parse(bookPath)
        val doc = DocumentFile.fromSingleUri(requireContext(), uri)
        if (doc == null || !doc.canWrite())
            binding.imageButtonBookinfoDelete.visibility = View.GONE

        binding.imageButtonBookinfoDelete.setOnClickListener {
            AlertDialog.Builder(binding.composeView.context)
            .setTitle(getString(R.string.title_delete_book))
            .setMessage(getString(R.string.sure_delete_book))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok_delete_book)
            ) { dialogInterface, _ ->
                if ( mListener != null) {
                    mListener?.onBookInfoFragmentDeleteBook(bookPath)
                    requireActivity().supportFragmentManager.popBackStack()
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
        }

        if ( mListener == null) {
            binding.imageButtonBookinfoRead.visibility = View.GONE
            binding.imageButtonBookinfoDelete.visibility = View.GONE
        }

        val ebookHelper = EbookHelper.getHelper(requireContext(), bookPath)

        if ( ebookHelper == null)
            requireActivity().supportFragmentManager.popBackStack()

        val bookInfo = ebookHelper?.getBookInfoTemporary(bookPath)

        if ( bookInfo == null )
            requireActivity().supportFragmentManager.popBackStack()

        bookInfo?.let {
            binding.composeView.setContent {
                BookInfoContent(
                    bookInfoDetails = it.toBookInfoDetails(),
                    navigateToAuthor = { onClickAuthorItem(it)})
            }
        }
    }

    private fun onClickAuthorItem(author: Author) {
        val fragment = LibraryByTitleFragment.newInstance(author)
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, fragment, "fragment_library_by_title")
            .addToBackStack("fragment_library_by_title")
            .commit()
    }

    fun setListener(listener: BookInfoListener) {
        this.mListener = listener
    }

    override fun onDetach() {
        super.onDetach()
        this.mListener = null
    }

    companion object {
        private const val BOOK_PATH = "book_path"

        fun newInstance(bookPath: String?): BookInfoFragment {
            val frag = BookInfoFragment()
            val args = Bundle()
            args.putString(BOOK_PATH, bookPath)
            frag.arguments = args
            return frag
        }
    }

}