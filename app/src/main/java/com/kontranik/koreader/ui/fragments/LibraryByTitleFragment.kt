package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kontranik.koreader.App
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.database.model.LibraryItem
import com.kontranik.koreader.databinding.FragmentLibraryBookListBinding
import com.kontranik.koreader.ui.adapters.PagingLibraryItemAdapter

class LibraryByTitleFragment : DialogFragment(), PagingLibraryItemAdapter.PagingLibraryItemAdapterListener {

    private lateinit var binding: FragmentLibraryBookListBinding

    private lateinit var mAdapter: PagingLibraryItemAdapter

    private lateinit var mLibraryViewModel: LibraryViewModel

    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel

    private lateinit var mFileChooseFragmentViewModel: FileChooseFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

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
                (requireContext().applicationContext as App).libraryItemRepository,
                (requireContext().applicationContext as App).authorsRepository,
                App.getApplicationScope())
        )[LibraryViewModel::class.java]

        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]

        mFileChooseFragmentViewModel = ViewModelProvider(this)[FileChooseFragmentViewModel::class.java]

        mLibraryViewModel.createNotificationChannel()

        binding.reciclerViewLibraryBooklistList.adapter = mAdapter
        binding.reciclerViewLibraryBooklistList.layoutManager = LinearLayoutManager(requireContext())

        binding.imageButtonLibraryBooklistBack.setOnClickListener {
            dismiss()
        }

        binding.ibLibrarySearchClear.setOnClickListener {
            binding.etLibrarySearchText.text = null
            mLibraryViewModel.changeSearchText(null)
        }

        binding.ibSearch.setOnClickListener {
            val searchText = if ( binding.etLibrarySearchText.text.isNotBlank() && binding.etLibrarySearchText.text.isNotBlank() ) {
                binding.etLibrarySearchText.text.toString()
            } else {
                null
            }
            mLibraryViewModel.changeSearchText(searchText)
        }

        mLibraryViewModel.libraryPageByFilter.observe(this) { libraryItems ->
            libraryItems?.let {
                mAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                mAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
            mAdapter.notifyDataSetChanged()
        }

        mLibraryViewModel.loadPageInit()
    }

    override fun onClickLibraryItem(libraryItem: LibraryItem) {
        mFileChooseFragmentViewModel.savePrefs(libraryItem.path)
        mReaderActivityViewModel.setBookPath(requireContext(), libraryItem.path)
        dismiss()
    }

    override fun onDeleteLibraryItem(position: Int, libraryItem: LibraryItem?) {
        if (libraryItem != null) {
            mLibraryViewModel.openDeleteSongDialog(
                binding.reciclerViewLibraryBooklistList.adapter as PagingLibraryItemAdapter,
                position, libraryItem, requireContext())
        }
    }

    override fun showUndoSnackbar(mRecentlyDeletedItem: LibraryItem?) {
        if (mRecentlyDeletedItem != null) {
            val view: View = requireActivity().findViewById(R.id.reciclerView_library_booklist_list)
            val snackbar: Snackbar = Snackbar.make(
                view, getString(R.string.undo_delete_snackbar_message),
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction(getString(R.string.undo_delete_undo_action)) { v -> mLibraryViewModel.insert(mRecentlyDeletedItem) }
            snackbar.show()
        }
    }


}