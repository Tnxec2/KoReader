package com.kontranik.koreader.ui.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentBookinfoBinding
import com.kontranik.koreader.model.Book

class BookInfoFragment : DialogFragment() {

    private lateinit var binding: FragmentBookinfoBinding

    private var listener: BookInfoListener? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface BookInfoListener {
        fun onBookInfoFragmentReadBook(bookUri: String)
        fun onBookInfoFragmentDeleteBook(bookUri: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentBookinfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookPath = requireArguments().getString(BOOK_PATH, null)
        if ( bookPath == null) dismiss()

        binding.imageButtonBookinfoClose.setOnClickListener {
            dismiss()
        }

        binding.imageButtonBookinfoRead.setOnClickListener {
            if ( listener != null) {
                listener?.onBookInfoFragmentReadBook(bookPath!!)
                dismiss()
            }
        }

        val uri = Uri.parse(bookPath)
        val doc = DocumentFile.fromSingleUri(requireContext(), uri)
        if (doc == null || !doc.canWrite())
            binding.imageButtonBookinfoDelete.visibility = View.GONE

        binding.imageButtonBookinfoDelete.setOnClickListener {
            AlertDialog.Builder(binding.textViewBookinfoTitle.context)
            .setTitle(getString(R.string.title_delete_book))
            .setMessage(getString(R.string.sure_delete_book))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok_delete_book)
            ) { dialogInterface, _ ->
                if ( listener != null) {
                    listener?.onBookInfoFragmentDeleteBook(bookPath)
                    dismiss()
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

        if ( listener == null) {
            binding.imageButtonBookinfoRead.visibility = View.GONE
            binding.imageButtonBookinfoDelete.visibility = View.GONE
        }

        val ebookHelper = Book.getHelper(requireContext(), bookPath)

        if ( ebookHelper == null) dismiss()

        val bookInfo = ebookHelper?.getBookInfoTemporary(bookPath)

        if ( bookInfo == null ) dismiss()

        binding.textViewBookinfoTitle.text = bookInfo!!.title

        if ( bookInfo.cover != null) {
            binding.imageViewBookinfoCover.setImageBitmap(bookInfo.cover)
        } else {
            binding.imageViewBookinfoCover.visibility = View.GONE
        }
        binding.textViewBookinfoBooktitle.text = bookInfo.title
        binding.textViewBookinfoAutors.text = bookInfo.authorsAsString()
        binding.textViewBookinfoAnnotation.text = Html.fromHtml(bookInfo.annotation)
    }

    fun setListener(listener: BookInfoListener) {
        this.listener  = listener
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