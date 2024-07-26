package com.kontranik.koreader.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.databinding.FragmentLibraryAuthorsListBinding
import com.kontranik.koreader.ui.adapters.PagingAuthorItemAdapter


open class LibraryByAuthorFragment : Fragment(), PagingAuthorItemAdapter.PagingAuthorItemAdapterListener  {

    protected lateinit var binding: FragmentLibraryAuthorsListBinding

    private lateinit var mAdapter: PagingAuthorItemAdapter

    private lateinit var mLibraryViewModel: LibraryViewModel

    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLibraryAuthorsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = PagingAuthorItemAdapter(requireContext(), this)

        mLibraryViewModel = ViewModelProvider(this,
            LibraryViewModelFactory(
                (requireContext().applicationContext as KoReaderApplication).libraryItemRepository,
                (requireContext().applicationContext as KoReaderApplication).authorsRepository,
                KoReaderApplication.getApplicationScope())
        )[LibraryViewModel::class.java]

        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]

        binding.reciclerViewLibraryAuthorsList.adapter = mAdapter
        binding.reciclerViewLibraryAuthorsList.layoutManager = LinearLayoutManager(requireContext())

        binding.imageButtonLibraryAuthorsBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.ibLibraryAuthorsSearchClear.setOnClickListener {
            binding.etLibraryAuthorsSearchText.text = null
            mLibraryViewModel.changeAuthorSearchText(null)
        }

        binding.ibLibraryAuthorsSearch.setOnClickListener {
            val searchText = if ( binding.etLibraryAuthorsSearchText.text.isNotBlank() && binding.etLibraryAuthorsSearchText.text.isNotBlank() ) {
                binding.etLibraryAuthorsSearchText.text.toString()
            } else {
                null
            }
            mLibraryViewModel.changeAuthorSearchText(searchText)
        }

        mLibraryViewModel.libraryAuthorPageByFilter.observe(viewLifecycleOwner) { pageAuthors ->
            pageAuthors?.let {
                mAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                mAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
            mAdapter.notifyDataSetChanged()
        }

        mLibraryViewModel.loadAuthorPageInit()
    }

    override fun onClickAuthorItem(author: Author) {
        val fragment = LibraryByTitleFragment.newInstance(author)
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, fragment, "fragment_library_by_title")
            .addToBackStack("fragment_library_by_title")
            .commit()
    }

    override fun onDeleteAuthorItem(position: Int, author: Author?) {
        // TODO("Not yet implemented")
    }



}