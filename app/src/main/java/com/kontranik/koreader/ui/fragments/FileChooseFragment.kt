package com.kontranik.koreader.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontranik.koreader.R
import com.kontranik.koreader.ReaderActivityViewModel
import com.kontranik.koreader.databinding.FragmentFilechooseBinding
import com.kontranik.koreader.ui.adapters.FileListAdapter
import com.kontranik.koreader.utils.FileItem

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
class FileChooseFragment : DialogFragment(),
        FileListAdapter.FileListAdapterClickListener,
        BookInfoFragment.BookInfoListener{
    
    private lateinit var binding: FragmentFilechooseBinding
    private lateinit var mReaderActivityViewModel: ReaderActivityViewModel
    private lateinit var mFileChooseFragmentViewModel: FileChooseFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFilechooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mReaderActivityViewModel = ViewModelProvider(requireActivity())[ReaderActivityViewModel::class.java]
        mFileChooseFragmentViewModel = ViewModelProvider(this)[FileChooseFragmentViewModel::class.java]

        binding.imageButtonFilechooseClose.setOnClickListener {
            dismiss()
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

        mFileChooseFragmentViewModel.showOpenBookInfo.observe(this) {
            openBookInfo(it)
        }
        mFileChooseFragmentViewModel.removedItemIndex.observe(this) {
            binding.reciclerViewFiles.adapter?.notifyItemRemoved(it)
        }
        mFileChooseFragmentViewModel.showConfirmSelectStorageDialog.observe(this) {
            if ( it ) confirmSelectStorageDialog()
        }
        mFileChooseFragmentViewModel.isVisibleImageButtonFilechooseAddStorage.observe(this) {
            binding.imageButtonFilechooseAddStorage.visibility = if (it) View.VISIBLE else View.GONE
        }
        mFileChooseFragmentViewModel.fileItemList.observe(this) {
            binding.reciclerViewFiles.adapter = FileListAdapter(requireContext(), it, this)
        }
        mFileChooseFragmentViewModel.oldSelectedDocumentFileUriString.observe(this) {
            (binding.reciclerViewFiles.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(
                    mFileChooseFragmentViewModel.
                        getPositionInFileItemList(), 0)
        }

    }

    private fun openBook(uriString: String) {
        mFileChooseFragmentViewModel.savePrefs(uriString)
        mReaderActivityViewModel.setBookPath(requireContext(), uriString)
        dismiss()
    }

    private fun deleteBook(uriString: String?) {
        if ( mReaderActivityViewModel.deleteBook(uriString)) {
            mFileChooseFragmentViewModel.removeBookFromList(uriString)
        }
    }

    override fun onFilelistItemClick(position: Int) {
        mFileChooseFragmentViewModel.onFilelistItemClick(position)
    }

    override fun onFilelistItemDelete(position: Int, fileItem: FileItem) {
        if( fileItem.isStorage )
            confirmDeleteStorage(position)
    }

    private fun confirmDeleteStorage(position: Int) {
        AlertDialog.Builder(binding.reciclerViewFiles.context)
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
            bookInfoFragment.show(requireActivity().supportFragmentManager, "fragment_bookinfo")
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
            binding.reciclerViewFiles.context,
            "Select directory or storage from dialog, and grant access",
            Toast.LENGTH_LONG)
            .show()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION   // write permission to remove book
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return

            requireActivity().contentResolver.takePersistableUriPermission(
                    directoryUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            mFileChooseFragmentViewModel.addStoragePath(directoryUri.toString())
        }
    }

    companion object {
        private const val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e
    }
}