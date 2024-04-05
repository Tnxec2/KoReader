package com.kontranik.koreader.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.kontranik.koreader.R
import com.kontranik.koreader.databinding.FragmentOpdsEntrysListBinding
import com.kontranik.koreader.opds.LoadOpds
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.opds.model.Opds
import com.kontranik.koreader.opds.model.OpdsTypes
import com.kontranik.koreader.opds.model.OpenSearchDescription
import com.kontranik.koreader.opds.model.SearchUrlTypes
import com.kontranik.koreader.ui.adapters.OpdsEntryListAdapter
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.UrlHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.net.MalformedURLException


class OpdsEntryListFragment :
    Fragment(), OpdsEntryListAdapter.OpdsEntryListAdapterClickListener,
    OpdsEntryDetailsFragment.OpdsEntryDetailsFragmentLinkClickListener,
    OpdsEntryEditFragment.OpdsOverviewEntryEditListener {

    private lateinit var binding: FragmentOpdsEntrysListBinding

    private var opdsEntryList: MutableList<Entry> = mutableListOf()

    private val navigationHistory = mutableListOf<String>()

    private var openedLink: String? = null

    var searchTerm: String? = "frank"
    var searchDescription: OpenSearchDescription? = null

    var startUrl: String? = OVERVIEW

    private lateinit var  overview_opds: Opds

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpdsEntrysListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPrefs()

        binding.imageButtonOpdsentrylistBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imageButtonOpdsentrylistSearch.setOnClickListener {
            openSearchTermDialog()
        }

        val divider = DividerItemDecoration(
            binding.reciclerViewOpdsentrylistList.context, DividerItemDecoration.VERTICAL
        )
        ResourcesCompat.getDrawable(
            requireContext().resources,
            R.drawable.recycler_view_divider,
            null
        )?.let {
            divider.setDrawable(
                it
            )
        }
        binding.reciclerViewOpdsentrylistList.addItemDecoration(divider)
        binding.reciclerViewOpdsentrylistList.adapter =
            OpdsEntryListAdapter(requireContext(), opdsEntryList, this)

        binding.imageButtonOpdsentrylistAdd.setOnClickListener {
            openAddItemDialog()
        }

        load(OVERVIEW)
    }

    private fun openSearchTermDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle(resources.getString(R.string.search_term))

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(searchTerm)
        builder.setView(input)

        builder.setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
            searchTerm = input.text.toString()
            search()
        }
        builder.setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun search() {
        if (searchDescription != null) {
            searchDescription?.urls?.firstOrNull { url -> url.type == SearchUrlTypes.atom }?.let {searchUrl ->
                val url = searchUrl.template.replace("{searchTerms}", searchTerm ?: "")
                load(url)
            }
        }
    }

    fun load(url: String) {
        if (url == OVERVIEW) {
            binding.imageButtonOpdsentrylistAdd.visibility = View.VISIBLE
        } else {
            binding.imageButtonOpdsentrylistAdd.visibility = View.GONE
        }
        openedLink?.let { navigationHistory.add(it) }
        openedLink = url
        lifecycleScope.launch(Dispatchers.IO) {
            val result: Opds?
            var icon: Bitmap? = null
            try {
                 if (url == OVERVIEW){
                     result = overview_opds
                     startUrl = OVERVIEW
                } else {
                     if (startUrl == OVERVIEW) startUrl = url
                     result = LoadOpds.loadXmlFromNetwork(UrlHelper.getUrl(url, startUrl))
                    if (searchDescription == null) {
                        Log.d("OPDS List Fragment", "searchlink: ${result?.search.toString()}")
                        result?.search?.href?.let {
                            try {
                                searchDescription = LoadOpds.loadSearchFromNetwork(UrlHelper.getUrl(it, startUrl))
                            } catch (e: Exception) {
                                Log.e("OPDS List fragment", e.localizedMessage, e)
                            }
                        }
                    }
                }

                if (result?.icon != null) {
                    icon = ImageUtils.drawableFromUrl(result.icon, startUrl)
                }

                withContext(Dispatchers.Main) {
                    result?.title.let {
                        binding.textViewOpdsentrylistContentTitle.text = it
                    }
                    if (icon != null) {
                        binding.imageViewOpdsentrylistContentIcon.setImageBitmap(icon)
                        binding.imageViewOpdsentrylistContentIcon.visibility = View.VISIBLE
                    } else {
                        binding.imageViewOpdsentrylistContentIcon.visibility = View.GONE
                    }
                    if (result?.subtitle != null) {
                        binding.textViewOpdsentrylistContentSubtitle.visibility = View.VISIBLE
                        binding.textViewOpdsentrylistContentSubtitle.text = result.subtitle
                    } else {
                        binding.textViewOpdsentrylistContentSubtitle.visibility = View.GONE
                    }
                    if (result?.author != null) {
                        binding.textViewOpdsentrylistContentAuthor.visibility = View.VISIBLE
                        binding.textViewOpdsentrylistContentAuthor.text = result.author.toString()
                    } else {
                        binding.textViewOpdsentrylistContentAuthor.visibility = View.GONE
                    }
                    opdsEntryList.clear()

                    result?.entries?.forEach { entry -> Log.d("OPDS List Fragment", "entry: ${entry.title} : ${entry.clickLink}") }

                    if (url != OVERVIEW) {
                        navigationHistory.last().let { link ->
                            opdsEntryList.add(Entry(
                                id = "back",
                                title = "..",
                                clickLink = Link(
                                    type = OpdsTypes.TYPE_LINK_OPDS_CATALOG,
                                    title = "back",
                                    href = link,
                                    rel = null
                                ),
                                thumbnail = Link(
                                    type = OpdsTypes.TYPE_LINK_IMAGE_PNG,
                                    title = null,
                                    href = resources.getString(R.string.icon_back_base64),
                                    rel = OpdsTypes.REL_IMAGE
                                )
                            ))
                        }
                    }
                    if (result != null && result.entries.isNotEmpty()) {
                        opdsEntryList.addAll(
                            result.entries
                        )
                    }
                    if (result?.links?.isNotEmpty() == true) {
                        result.links.forEach { link: Link ->
                            if (link.isCatalogEntry()) {
                                opdsEntryList.add(
                                    Entry(
                                        id = link.title,
                                        title = link.title,
                                        content = null,
                                        thumbnail = null,
                                        clickLink = link,
                                        otherLinks = null
                                    )
                                )
                            }
                        }
                    }
                    (binding.reciclerViewOpdsentrylistList.adapter as OpdsEntryListAdapter).setStartUrl(startUrl)
                    binding.reciclerViewOpdsentrylistList.adapter?.notifyDataSetChanged()
                    binding.reciclerViewOpdsentrylistList.scrollToPosition(0)
                }
            } catch (e: IOException) {
                Log.e("OPDS List fragment", e.localizedMessage, e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.connection_error), Toast.LENGTH_LONG
                    ).show()
                }
                goBack()
            } catch (e: XmlPullParserException) {
                Log.e("OPDS List fragment", e.localizedMessage, e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.xml_error), Toast.LENGTH_LONG
                    ).show()
                }
                goBack()
            } catch (e: MalformedURLException) {
                Log.e("OPDS List fragment", e.localizedMessage, e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.connection_error), Toast.LENGTH_LONG
                    ).show()
                }
                goBack()
            }
        }
    }

    fun parseStringArrayToEntryList(stringArrayResourceId: Int): MutableList<Entry> {
        val stringArray = resources.getStringArray(stringArrayResourceId)
        val outputArray = mutableListOf<Entry>()
        for (entry in stringArray) {
            outputArray.add(splitEntry(entry))
        }
        return outputArray
    }

    private fun splitEntry(s: String): Entry {
        val splitResult = s.split("\\|".toRegex(), limit = 2).toTypedArray()

        return Entry(
            title = splitResult[0],
            clickLink = Link( splitResult[0], splitResult[1])
        )
    }

    private fun goBack() {
        if (navigationHistory.isNotEmpty()) {
            val url = navigationHistory.removeLast()
            load(url)
        }
    }

    override fun onOpdsEntrylistItemClick(entry: Entry) {
        Log.d("OPDS List", "clicked opds item: ${entry.title} - ${entry.clickLink}")
        if (entry.clickLink != null) {
            entry.clickLink.href?.let {
                load(it)
            }
        } else {
            val fragment = OpdsEntryDetailsFragment.newInstance(entry, startUrl!!)
            fragment.setListener(this)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, fragment, "fragment_opds_details")
                .addToBackStack("fragment_opds_details")
                .commit()
        }
    }

    override fun onOpdslistItemDelete(position: Int) {
        overview_opds.entries.removeAt(position)
        savePrefs()
        load(OVERVIEW)
    }

    override fun onOpdslistItemEdit(position: Int) {
        val editFragment: OpdsEntryEditFragment =
            OpdsEntryEditFragment.newInstance(position, overview_opds.entries[position].title, overview_opds.entries[position].clickLink?.href)
        editFragment.setListener(this)
        editFragment.show(requireActivity().supportFragmentManager, "fragment_opds_edit")
    }

    private fun openAddItemDialog() {
        val editFragment: OpdsEntryEditFragment =
            OpdsEntryEditFragment.newInstance(overview_opds.entries.size)
        editFragment.setListener(this)
        editFragment.show(requireActivity().supportFragmentManager, "fragment_opds_edit")
    }


    private fun loadPrefs() {
        val settings = requireContext()
            .getSharedPreferences(
                PREFS_FILE,
                Context.MODE_PRIVATE)
        val eP = settings.getStringSet(PREF_OPDS_OVERVIEW, null)
        overview_opds = eP?.let { Opds(
            title = resources.getString(R.string.opds_overview),
            subtitle = null,
            entries = it.map { s -> splitEntry(s)}.toMutableList(),
            links = emptyList()
        ) } ?:  Opds(
            title = resources.getString(R.string.opds_overview),
            subtitle = null,
            entries = parseStringArrayToEntryList(R.array.opds_list),
            links = emptyList()
        )
    }

    private fun savePrefs() {
        val settings = requireContext()
            .getSharedPreferences(
                PREFS_FILE,
                Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        if ( overview_opds.entries.isNotEmpty() ) {
            prefEditor.putStringSet(PREF_OPDS_OVERVIEW, overview_opds.entries.map { e -> "${e.title}|${e.clickLink?.href}" }.toMutableSet())
        } else {
            prefEditor.remove(PREF_OPDS_OVERVIEW)
        }
        prefEditor.apply()
    }

    override fun onClickOpdsEntryLink(link: Link) {
        Log.d("OPDS List", "clicked opds entry link: $link")
        link.href?.let { load(it) }
    }

    override fun onOpdsOverviewEntryEditSave(pos: Int, name: String, url: String) {
        if (pos < overview_opds.entries.size) {
            overview_opds.entries[pos].title = name
            overview_opds.entries[pos].clickLink?.href = url
        } else {
            overview_opds.entries.add(Entry(
                title = name,
                clickLink = Link(title = name, href =  url)
            ))
        }
        savePrefs()
        load(OVERVIEW)
    }

    companion object {
        const val OVERVIEW = "OVERVIEW"

        const val PREFS_FILE = "OpdsActivitySettings"
        const val PREF_OPDS_OVERVIEW = "OPDS_OVERVIEW"

        fun newInstance(): OpdsEntryListFragment {
            val frag = OpdsEntryListFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }


}