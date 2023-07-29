package com.kontranik.koreader.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kontranik.koreader.App
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentLibrarySettingsBinding
import com.kontranik.koreader.ui.adapters.LibraryScanPointListAdapter

class LibrarySettingsFragment : Fragment(), LibraryScanPointListAdapter.LibraryScanPointListAdapterClickListener {

    private lateinit var binding: FragmentLibrarySettingsBinding

    private lateinit var mLibraryViewModel: LibraryViewModel

    private var scanPointList: MutableList<String> = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLibrarySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLibraryViewModel = ViewModelProvider(this,
            LibraryViewModelFactory(
                (requireContext().applicationContext as App).libraryItemRepository,
                (requireContext().applicationContext as App).authorsRepository,
                App.getApplicationScope())
        )[LibraryViewModel::class.java]

        mLibraryViewModel.createNotificationChannel()

        binding.imageButtonLibrarySettingsClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imageButtonLibrarySettingsRefresh.setOnClickListener {
            performRefreshLibrary()
        }

        binding.imageButtonLibrarySettingsAddScanPoint.setOnClickListener {
            performFileSearchToAddStorage()
        }

        binding.imageButtonLibrarySettingsClearLibrary.setOnClickListener {
            performClearLibrary()
        }

        loadPrefs()

        binding.reciclerViewLibrarySettingsScanPoints.adapter =
            LibraryScanPointListAdapter(requireContext(), scanPointList, this)

        mLibraryViewModel.refreshInProgress.observe(viewLifecycleOwner) {
            binding.imageButtonLibrarySettingsRefresh.visibility = if (it) View.GONE else View.VISIBLE
        }
    }

    private fun addScanPoint(path: String) {
        scanPointList.add(path)
        savePrefs()
        binding.reciclerViewLibrarySettingsScanPoints.adapter?.notifyItemInserted(scanPointList.size-1)
    }

    private fun loadPrefs() {
        val settings = requireContext()
            .getSharedPreferences(
                PREFS_FILE,
                Context.MODE_PRIVATE)
        if ( settings.contains(PREF_SCAN_POINTS)) {
            val eP = settings.getStringSet(PREF_SCAN_POINTS, null)
            scanPointList = eP?.toMutableList() ?: mutableListOf()
        }
        updateUi()
    }

    private fun savePrefs() {
        val settings = requireContext()
            .getSharedPreferences(
                PREFS_FILE,
                Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        if ( scanPointList.isNotEmpty()) {
            prefEditor.putStringSet(PREF_SCAN_POINTS, scanPointList.toMutableSet())
        } else {
            prefEditor.remove(PREF_SCAN_POINTS)
        }
        prefEditor.apply()
        updateUi()
    }

    private fun updateUi() {
        if (scanPointList.isEmpty())
            binding.imageButtonLibrarySettingsRefresh.visibility = View.GONE
        else
            binding.imageButtonLibrarySettingsRefresh.visibility = View.VISIBLE
    }

    companion object {
        const val PREFS_FILE = "LibraryActivitySettings"
        const val PREF_SCAN_POINTS = "LibraryScanPoints"
    }

    private fun performRefreshLibrary() {
        AlertDialog.Builder(binding.reciclerViewLibrarySettingsScanPoints.context)
            .setTitle(getString(R.string.refresh_library))
            .setMessage(getString(R.string.are_you_sure_to_refresh_the_library))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.refresh_ok_button)
            ) { _, _ ->
                Toast.makeText(binding.reciclerViewLibrarySettingsScanPoints.context,
                    getString(R.string.start_refresh), Toast.LENGTH_SHORT).show()
                mLibraryViewModel.readRecursive(requireContext(), scanPointList)
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun performClearLibrary() {
        AlertDialog.Builder(binding.reciclerViewLibrarySettingsScanPoints.context)
            .setTitle(getString(R.string.clear_library_alert_title))
            .setMessage(getString(R.string.are_you_sure_to_clear_the_whole_library))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.clear_library_ok_button)
            ) { _, _ ->
                mLibraryViewModel.deleteAll()
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onLibraryScanPointListItemDelete(position: Int, item: String) {
        AlertDialog.Builder(binding.reciclerViewLibrarySettingsScanPoints.context)
            .setTitle(getString(R.string.delete_scan_point))
            .setMessage(getString(R.string.library_are_you_sure_to_delete_this_scan_point))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.delete_scan_point_ok_button)
            ) { dialogInterface, _ ->
                deleteScanPoint(position)
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun deleteScanPoint(position: Int) {
        scanPointList.removeAt(position)
        savePrefs()
        binding.reciclerViewLibrarySettingsScanPoints.adapter?.notifyItemRemoved(position)
    }

    override fun onLibraryScanPointListItemClick(position: Int) {
        // nothing to do here
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select a directory.
     */
    private fun performFileSearchToAddStorage() {
        Toast.makeText(
            binding.reciclerViewLibrarySettingsScanPoints.context,
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
                addScanPoint(it.toString())
            }
        }
    }

}