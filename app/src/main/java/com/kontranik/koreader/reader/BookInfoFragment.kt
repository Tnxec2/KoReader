package com.kontranik.koreader.reader

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.kontranik.koreader.R
import com.kontranik.koreader.model.Book
import com.kontranik.koreader.model.BookInfo

class BookInfoFragment : DialogFragment() {

    private var listener: BookInfoListener? = null

    // 1. Defines the listener interface with a method passing back data result.
    interface BookInfoListener {
        fun onBookInfoFragmentReadBook(bookUri: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_bookinfo, container)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookPath = requireArguments().getString(BOOK_PATH, null)
        if ( bookPath == null) dismiss()

        val close = view.findViewById<ImageButton>(R.id.imageButton_bookinfo_close)
        close.setOnClickListener {
            dismiss()
        }

        val read = view.findViewById<ImageButton>(R.id.imageButton_bookinfo_read)
        read.setOnClickListener {
            if ( listener != null) {
                listener!!.onBookInfoFragmentReadBook(bookPath!!)
                dismiss()
            }
        }

        if ( listener == null) {
            read.visibility = View.GONE
        }

        val titleView = view.findViewById<TextView>(R.id.textView_bookinfo_title)
        val booktitleView = view.findViewById<TextView>(R.id.textView_bookinfo_booktitle)
        val annotationView = view.findViewById<TextView>(R.id.textView_bookinfo_annotation)
        val autorsView = view.findViewById<TextView>(R.id.textView_bookinfo_autors)
        val coverView = view.findViewById<ImageView>(R.id.imageView_bookinfo_cover)

        val ebookHelper = Book.getHelper(requireContext(), bookPath!!)
        val bookInfo = ebookHelper?.getBookInfoTemporary(bookPath)

        if ( bookInfo != null ) {
            titleView.text = bookInfo.title

            if ( bookInfo.cover != null) {
                coverView.setImageBitmap(bookInfo.cover)
            } else {
                coverView.visibility = View.GONE
            }
            booktitleView.text = bookInfo.title
            autorsView.text = bookInfo.authorsAsString()
            annotationView.text = Html.fromHtml(bookInfo.annotation)
        }
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