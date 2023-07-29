package com.kontranik.koreader.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.kontranik.koreader.App
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.ReaderActivityViewModelFactory
import com.kontranik.koreader.database.BookStatusViewModel
import com.kontranik.koreader.database.BookStatusViewModelFactory
import com.kontranik.koreader.databinding.FragmentLastOpenedBookListBinding
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.ui.adapters.BookListAdapter
import com.kontranik.koreader.utils.FileHelper
import com.kontranik.koreader.utils.ImageUtils

class LastOpenedBookListFragment :
    Fragment(),
    BookListAdapter.BookListAdapterClickListener,
    BookInfoFragment.BookInfoListener {

    private lateinit var binding: FragmentLastOpenedBookListBinding

    private var bookInfoList: MutableList<BookInfo> = mutableListOf()

    private lateinit var mBookStatusViewModel: BookStatusViewModel

    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLastOpenedBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBookStatusViewModel = ViewModelProvider(this,
            BookStatusViewModelFactory((requireContext().applicationContext as App)
                .bookStatusRepository))[BookStatusViewModel::class.java]

        mReaderActivityViewModel = ViewModelProvider(this,
            ReaderActivityViewModelFactory((requireContext().applicationContext as App)
                .bookStatusRepository))[ReaderActivityViewModel::class.java]


        binding.imageButtonBooklistBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.reciclerViewBooklistList.adapter = BookListAdapter(requireContext(), bookInfoList, this)

        mBookStatusViewModel.lastOpenedBooks.observe(viewLifecycleOwner) {
            if (it != null) {
                bookInfoList.clear()
                for (bookStatus in it) {
                    if (bookStatus.path != null) {
                        if (!FileHelper.contentFileExist(requireContext(), bookStatus.path)) {
                            mBookStatusViewModel.delete(bookStatus.id!!)
                        }
                        val bookInfo = BookInfo(
                            title = bookStatus.title,
                            cover = bookStatus.cover?.let { it1 -> ImageUtils.getImage(it1) },
                            authors = mutableListOf(),
                            filename = bookStatus.path!!,
                            path = bookStatus.path!!,
                            annotation = ""
                        )
                        bookInfoList.add(bookInfo)
                    }
                }
                binding.reciclerViewBooklistList.adapter?.notifyDataSetChanged()
            }
        }

    }

    private fun savePrefs(uriString: String?) {
        val settings: SharedPreferences? = requireActivity()
            .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        val prefEditor = settings!!.edit()
        if ( uriString != null) prefEditor!!.putString(PREF_LAST_PATH, uriString)
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



    override fun onBookInfoFragmentReadBook(bookUri: String) {
        savePrefs(bookUri)
        mReaderActivityViewModel.setBookPath(requireContext(), bookUri)

        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onBookInfoFragmentDeleteBook(bookUri: String) {
        if ( mReaderActivityViewModel.deleteBook(bookUri)) {
            bookInfoList.removeAll { it.path == bookUri }
            mBookStatusViewModel.deleteByPath(bookUri)
            binding.reciclerViewBooklistList.adapter?.notifyDataSetChanged()
        }
    }
}