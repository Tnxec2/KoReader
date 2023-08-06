package com.kontranik.koreader.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.databinding.FragmentFilechooseBinding
import com.kontranik.koreader.ui.adapters.FileListAdapter
import com.kontranik.koreader.utils.FileItem


class FileChooseFragment : Fragment(),
        FileListAdapter.FileListAdapterClickListener,
        BookInfoFragment.BookInfoListener {
    
    private lateinit var binding: FragmentFilechooseBinding
    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel
    private lateinit var mFileChooseFragmentViewModel: FileChooseFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFilechooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]
        mFileChooseFragmentViewModel = ViewModelProvider(this)[FileChooseFragmentViewModel::class.java]

        binding.imageButtonFilechooseClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imageButtonFilechooseBack.setOnClickListener {
            mFileChooseFragmentViewModel.goBack()
        }

        binding.imageButtonFilechooseGotoStorage.setOnClickListener {
            mFileChooseFragmentViewModel.storageList()
        }

        binding.imageButtonFilechooseAddStorage.setOnClickListener {
            performFileSearchToAddStorage()
        }
        binding.imageButtonFilechooseAddStorage.visibility = View.GONE

        mFileChooseFragmentViewModel.showOpenBookInfo.observe(viewLifecycleOwner) {
            openBookInfo(it)
        }

        mFileChooseFragmentViewModel.removedItemIndex.observe(viewLifecycleOwner) {
            binding.reciclerViewFiles.adapter?.notifyItemRemoved(it)
        }
        mFileChooseFragmentViewModel.showConfirmSelectStorageDialog.observe(viewLifecycleOwner) {
            if ( it ) confirmSelectStorageDialog()
        }
        mFileChooseFragmentViewModel.isVisibleImageButtonFilechooseAddStorage.observe(viewLifecycleOwner) {
            binding.imageButtonFilechooseAddStorage.visibility = if (it) View.VISIBLE else View.GONE
            binding.imageButtonFilechooseBack.visibility = if (it) View.GONE else View.VISIBLE
            binding.imageButtonFilechooseGotoStorage.visibility = if (it) View.GONE else View.VISIBLE
        }
        mFileChooseFragmentViewModel.fileItemList.observe(viewLifecycleOwner) {
            binding.reciclerViewFiles.adapter = FileListAdapter(requireContext(), it, this)
        }
        mFileChooseFragmentViewModel.scrollToDocumentFileUriString.observe(viewLifecycleOwner) {
            (binding.reciclerViewFiles.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(
                    mFileChooseFragmentViewModel.
                        getPositionInFileItemList(), 0)
        }
    }

    private fun openBook(uriString: String) {
        mFileChooseFragmentViewModel.savePrefsOpenedBook(uriString)
        mReaderActivityViewModel.setBookPath(requireContext(), uriString)

        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun deleteBook(uriString: String?) {
        if ( mReaderActivityViewModel.deleteBook(uriString)) {
            mFileChooseFragmentViewModel.removeBookFromList(uriString)
        }
    }

    override fun onFilelistItemClick(position: Int) {
        mFileChooseFragmentViewModel.onFilelistItemClick(position)
    }

    override fun onFilelistItemDelete(position: Int, item: FileItem) {
        if( item.isStorage )
            confirmDeleteStorage(position)
    }

    private fun confirmDeleteStorage(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.title_delete_storage))
            .setMessage(getString(R.string.sure_delete_storage))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok_delete_storage)
            ) { dialogInterface, _ ->
                mFileChooseFragmentViewModel.deleteStorage(position)
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun openBookInfo(bookUri: String?) {
        if ( bookUri != null) {
            val bookInfoFragment = BookInfoFragment.newInstance(bookUri)
            bookInfoFragment.setListener(this)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, bookInfoFragment, "fragment_bookinfo_from_filechoose")
                .addToBackStack("fragment_bookinfo_from_filechoose")
                .commit()
        }
    }

    override fun onBookInfoFragmentReadBook(bookUri: String) {
        openBook(bookUri)
    }

    override fun onBookInfoFragmentDeleteBook(bookUri: String) {
        deleteBook(bookUri)
    }

    private fun confirmSelectStorageDialog() {
        AlertDialog.Builder(binding.reciclerViewFiles.context)
            .setTitle(getString(R.string.title_select_storage))
            .setMessage(getString(R.string.sure_select_storage))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok_select_storage)
            ) { dialogInterface, _ ->
                performFileSearchToAddStorage()
                dialogInterface.dismiss()
            }
            .show()
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select a directory.
     */
    private fun performFileSearchToAddStorage() {
        Toast.makeText(
            requireContext(),
            "Select directory or storage from dialog, and grant access",
            Toast.LENGTH_LONG)
            .show()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION   // write permission to remove book
        startForResultPickFileToStorage.launch(Intent.createChooser(intent, "Select file storage"))
    }

    private val startForResultPickFileToStorage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the Intent
            result.data?.data?.let {
                requireActivity().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                mFileChooseFragmentViewModel.addStoragePath(it.toString())
            }
        }
    }

}