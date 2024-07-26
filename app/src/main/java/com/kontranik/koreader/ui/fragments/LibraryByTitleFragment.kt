package com.kontranik.koreader.ui.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.LibraryItemWithAuthors
import com.kontranik.koreader.databinding.FragmentLibraryBookListBinding
import com.kontranik.koreader.ui.adapters.PagingLibraryItemAdapter


open class LibraryByTitleFragment : Fragment(), PagingLibraryItemAdapter.PagingLibraryItemAdapterListener,
    BookInfoFragment.BookInfoListener {

    protected lateinit var binding: FragmentLibraryBookListBinding

    private lateinit var mAdapter: PagingLibraryItemAdapter

    private lateinit var mLibraryViewModel: LibraryViewModel

    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel

    private lateinit var mFileChooseFragmentViewModel: FileChooseFragmentViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLibraryBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = PagingLibraryItemAdapter(requireContext(), this)

        mLibraryViewModel = ViewModelProvider(this,
            LibraryViewModelFactory(
                (requireContext().applicationContext as KoReaderApplication).libraryItemRepository,
                (requireContext().applicationContext as KoReaderApplication).authorsRepository,
                KoReaderApplication.getApplicationScope())
        )[LibraryViewModel::class.java]

        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]

        mFileChooseFragmentViewModel = ViewModelProvider(this)[FileChooseFragmentViewModel::class.java]

        mLibraryViewModel.createNotificationChannel()

        var author: Author? = null
        arguments?.let {
            if ( it.containsKey(KEY_AUTHOR)) {
                author = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getSerializable(KEY_AUTHOR, Author::class.java)
                } else {
                    it.getSerializable(KEY_AUTHOR) as Author
                }
            }
        }

        if (author != null) {
            binding.textViewLibraryBooklistAuthor.visibility = View.VISIBLE
            binding.textViewLibraryBooklistAuthor.text = getString(R.string.by_author, author?.asString())
        } else {
            binding.textViewLibraryBooklistAuthor.visibility = View.GONE
        }

        binding.reciclerViewLibraryBooklistList.adapter = mAdapter
        binding.reciclerViewLibraryBooklistList.layoutManager = LinearLayoutManager(requireContext())

        binding.imageButtonLibraryBooklistBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.ibLibrarySearchClear.setOnClickListener {
            binding.etLibrarySearchText.text = null
            mLibraryViewModel.changeTitleSearchText(null)
        }

        binding.ibLibrarySearch.setOnClickListener {
            val searchText = if ( binding.etLibrarySearchText.text.isNotBlank() && binding.etLibrarySearchText.text.isNotBlank() ) {
                binding.etLibrarySearchText.text.toString()
            } else {
                null
            }
            mLibraryViewModel.changeTitleSearchText(searchText)
        }

        mLibraryViewModel.libraryTitlePageByFilter.observe(viewLifecycleOwner) { libraryItems ->
            libraryItems?.let {
                mAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                mAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
            mAdapter.notifyDataSetChanged()
        }

        mLibraryViewModel.loadTitlePageInit(author)
    }

    override fun onClickLibraryItem(position: Int, libraryItem: LibraryItemWithAuthors) {
        val bookPathUri = libraryItem.libraryItem.path
        try {
            val inputStream = requireContext().contentResolver.openInputStream(Uri.parse(bookPathUri))
            inputStream?.close()
            val bookInfoFragment = BookInfoFragment.newInstance(bookPathUri)
            bookInfoFragment.setListener(this)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, bookInfoFragment, "fragment_bookinfo_from_library_by_title")
                .addToBackStack("fragment_bookinfo_from_library_by_title")
                .commit()
        } catch (e: Exception) {
            AlertDialog.Builder(context)
                .setTitle("Book does not exist")
                .setMessage("Are you sure you want to delete this book from library?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    android.R.string.yes
                ) { _, _ ->
                    mLibraryViewModel.delete(libraryItem)
                    (binding.reciclerViewLibraryBooklistList.adapter as PagingLibraryItemAdapter).deleteItem(position)
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    override fun onDeleteLibraryItem(position: Int, libraryItem: LibraryItemWithAuthors?) {
        if (libraryItem != null) {
            mLibraryViewModel.openDeleteLibraryItemDialog(
                binding.reciclerViewLibraryBooklistList.adapter as PagingLibraryItemAdapter,
                position, libraryItem, requireContext())
        }
    }

    override fun onUpdateLibraryItem(position: Int, libraryItem: LibraryItemWithAuthors?) {
        if (libraryItem != null) {
            mLibraryViewModel.updateLibraryItem(position, libraryItem)
        }
    }

//    override fun showUndoSnackbar(mRecentlyDeletedItem: LibraryItemWithAuthors?) {
//        if (mRecentlyDeletedItem != null) {
//            val view: View = requireActivity().findViewById(R.id.reciclerView_library_booklist_list)
//            val snackbar: Snackbar = Snackbar.make(
//                view, getString(R.string.undo_delete_snackbar_message),
//                Snackbar.LENGTH_LONG
//            )
//            snackbar.setAction(getString(R.string.undo_delete_undo_action)) { _  -> mLibraryViewModel.insert(mRecentlyDeletedItem) }
//            snackbar.show()
//        }
//    }

    override fun onBookInfoFragmentReadBook(bookUri: String) {
        mFileChooseFragmentViewModel.savePrefsOpenedBook(bookUri)
        mReaderActivityViewModel.setBookPath(requireContext(), bookUri)

        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onBookInfoFragmentDeleteBook(bookUri: String) {
        // TODO("Not yet implemented")
    }

    companion object {
        const val KEY_AUTHOR = "AUTHOR"

        fun newInstance(
            author: Author?
        ): LibraryByTitleFragment {
            val frag = LibraryByTitleFragment()
            val args = Bundle()
            author?.let{ args.putSerializable(KEY_AUTHOR, it) }
            frag.arguments = args

            return frag
        }
    }
}