package com.kontranik.koreader.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.databinding.FragmentBookListBinding
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.ui.adapters.BookListAdapter
import com.kontranik.koreader.utils.FileHelper

class BookListFragment :
    DialogFragment(),
    BookListAdapter.BookListAdapterClickListener,
    BookInfoFragment.BookInfoListener{

    private lateinit var binding: FragmentBookListBinding

    private var bookInfoList: MutableList<BookInfo> = mutableListOf()

    private lateinit var mBookStatusViewModel: BookStatusViewModel

    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBookStatusViewModel = ViewModelProvider(requireActivity())[BookStatusViewModel::class.java]
        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]

        binding.imageButtonBooklistBack.setOnClickListener {
            dismiss()
        }

        binding.reciclerViewBooklistList.adapter = BookListAdapter(requireContext(), bookInfoList, this)

        mBookStatusViewModel.lastOpenedBooks.observe(this) {
            if (it != null) {
                bookInfoList.clear()
                for (bookStatus in it) {
                    if (bookStatus.path != null) {
                        if (!FileHelper.contentFileExist(requireContext(), bookStatus.path)) {
                            mBookStatusViewModel.delete(bookStatus.id!!)
                        }
                        val bookInfo = BookInfo(
                            title = bookStatus.title,
                            cover = null,
                            authors = mutableListOf(),
                            filename = bookStatus.path!!,
                            path = bookStatus.path!!,
                            annotation = ""
                        )
                        bookInfoList.add(bookInfo)
                    }
                }
            }
        }
    }

    private fun savePrefs(uriString: String?) {
        val settings: SharedPreferences? = requireActivity()
            .getSharedPreferences(FileChooseFragment.PREFS_FILE, Context.MODE_PRIVATE)
        val prefEditor = settings!!.edit()
        if ( uriString != null) prefEditor!!.putString(FileChooseFragment.PREF_LAST_PATH, uriString)
        prefEditor.apply()
    }


    override fun onBooklistItemClickListener(position: Int) {
        val selectedBook = bookInfoList[position]
        openBookInfo(selectedBook.path)
    }

    private fun openBookInfo(bookPathUri: String?) {
        if ( bookPathUri != null) {
            val bookInfoFragment = BookInfoFragment.newInstance(bookPathUri)
            bookInfoFragment.setListener(this)
            bookInfoFragment.show(requireActivity().supportFragmentManager, "fragment_bookinfo")
        }
    }

    companion object {
        const val BOOKLIST_TYP = "typ"
        const val BOOKLIST_TYP_LAST_OPENED = "lastopened"
        const val BOOKLIST_TYP_DEFAULT = BOOKLIST_TYP_LAST_OPENED

    }

    override fun onBookInfoFragmentReadBook(bookUri: String) {
        savePrefs(bookUri)
        mReaderActivityViewModel.setBookPath(requireContext(), bookUri)
        dismiss()
    }

    override fun onBookInfoFragmentDeleteBook(bookUri: String) {
        if ( mReaderActivityViewModel.deleteBook(bookUri)) {
            bookInfoList.removeAll { it.path == bookUri }
            mBookStatusViewModel.deleteByPath(bookUri)
            binding.reciclerViewBooklistList.adapter?.notifyDataSetChanged()
        }
    }
}