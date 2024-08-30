package com.kontranik.koreader.compose.ui.opds

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.opds.LoadOpds
import com.kontranik.koreader.opds.model.BACK
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.LOAD
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.opds.model.Opds
import com.kontranik.koreader.opds.model.OpenSearchDescription
import com.kontranik.koreader.opds.model.SearchUrlTypes
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.UrlHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.net.MalformedURLException

const val OVERVIEW = "OVERVIEW"

const val PREFS_FILE = "OpdsActivitySettings"
const val PREF_OPDS_OVERVIEW = "OPDS_OVERVIEW"

class OpdsViewModell(
    savedStateHandle: SavedStateHandle,
    val context: Context,
) : ViewModel() {

    val opdsEntryList = mutableStateOf(listOf<Entry>())

    private val navigationHistory = mutableListOf<String>()
    private var openedLink: String? = null

    val canAdd = mutableStateOf(false)
    val canSearch = mutableStateOf(false)
    val canReload = mutableStateOf(false)

    val contentTitle = mutableStateOf<String?>("")
    val contentSubTitle = mutableStateOf<String?>(null)
    val contentAuthor = mutableStateOf<String?>(null)
    val contentIcon = mutableStateOf<ImageBitmap?>(null)

    var searchTerm = mutableStateOf<String>("")
    private var searchDescription: OpenSearchDescription? = null

    var startUrl = mutableStateOf(OVERVIEW)

    private lateinit var overviewOpds: Opds

    fun start() {
        loadPrefs()
        load(OVERVIEW)
    }

    private fun loadPrefs() {
        val settings = KoReaderApplication.getContext()
            .getSharedPreferences(
                com.kontranik.koreader.ui.fragments.PREFS_FILE,
                Context.MODE_PRIVATE)
        val eP = settings.getStringSet(PREF_OPDS_OVERVIEW, null)
        overviewOpds = eP?.let { Opds(
            title = KoReaderApplication.getContext().resources.getString(R.string.opds_overview),
            subtitle = null,
            entries = it.map { s -> splitEntry(s)}.toMutableList(),
            links = emptyList()
        ) } ?:  Opds(
            title = KoReaderApplication.getContext().resources.getString(R.string.opds_overview),
            subtitle = null,
            entries = parseOpdsListResource(),
            links = emptyList()
        )
    }

    private fun savePrefs() {
        val settings = KoReaderApplication.getContext()
            .getSharedPreferences(
                com.kontranik.koreader.ui.fragments.PREFS_FILE,
                Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        if ( overviewOpds.entries.isNotEmpty() ) {
            prefEditor.putStringSet(PREF_OPDS_OVERVIEW, overviewOpds.entries.map { e -> "${e.title}|${e.clickLink?.href}" }.toMutableSet())
        } else {
            prefEditor.remove(PREF_OPDS_OVERVIEW)
        }
        prefEditor.apply()
    }

    private fun parseOpdsListResource(): MutableList<Entry> {
        val stringArray = KoReaderApplication.getContext().resources.getStringArray(R.array.opds_list)
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

    fun opdslistItemDelete(position: Int) {
        overviewOpds.entries.removeAt(position)
        savePrefs()
        load(OVERVIEW)
    }

    fun search() {
        if (searchDescription != null) {
            searchDescription?.urls?.firstOrNull { url -> url.type == SearchUrlTypes.atom }?.let { searchUrl ->
                val url = searchUrl.template.replace("{searchTerms}", searchTerm.value)
                load(url)
            }
        }
    }

    fun reloadPage() {
        load(openedLink ?: OVERVIEW)
    }

    fun load(url: String, back: Boolean = false) {
        if (url == OVERVIEW) {
            canAdd.value = true
            canSearch.value = false
            canReload.value = false
        } else {
            canAdd.value = false
            canSearch.value = true
            canReload.value = true
        }
        if (!back) {
            openedLink?.let { navigationHistory.add(it) }
            openedLink = url
        }

        opdsEntryList.value = listOf()
        opdsEntryList.value = opdsEntryList.value.plus(LOAD)

        viewModelScope.launch(Dispatchers.IO) {
            val result: Opds?
            var icon: Bitmap? = null
            try {
                if (url == OVERVIEW){
                    result = overviewOpds
                    startUrl.value = OVERVIEW
                } else {
                    if (startUrl.value == OVERVIEW) startUrl.value = url
                    result = LoadOpds.loadXmlFromNetwork(UrlHelper.getUrl(url, startUrl.value))
                    if (searchDescription == null) {
                        Log.d("OPDS List Fragment", "searchlink: ${result?.search.toString()}")
                        result?.search?.href?.let {
                            try {
                                searchDescription = LoadOpds.loadSearchFromNetwork(UrlHelper.getUrl(it, startUrl.value))
                            } catch (e: Exception) {
                                Log.e("OPDS List fragment", e.localizedMessage, e)
                            }
                        }
                    }
                }

                if (result?.icon != null) {
                    icon = ImageUtils.drawableFromUrl(result.icon, startUrl.value)
                }
                opdsEntryList.value = listOf()
                withContext(Dispatchers.Main) {
                    result?.title.let {
                        contentTitle.value = it ?: ""
                    }

                    contentIcon.value = icon?.asImageBitmap()

                    contentSubTitle.value = result?.subtitle
                    contentAuthor.value = result?.author?.toString()

                    opdsEntryList.value = listOf()

                    result?.entries?.forEach { entry -> Log.d("OPDS List Fragment", "entry: ${entry.title} : ${entry.clickLink}") }

                    if (url != OVERVIEW) opdsEntryList.value = opdsEntryList.value.plus(BACK)

                    if (result != null && result.entries.isNotEmpty())
                        opdsEntryList.value = opdsEntryList.value.plus(result.entries)

                    if (result?.links?.isNotEmpty() == true) {
                        result.links.forEach { link: Link ->
                            if (link.isCatalogEntry()) {
                                Log.d("LOAD", link.toString())
                                opdsEntryList.value = opdsEntryList.value.plus(Entry(link))
                            }
                        }
                    }
                    if (url != OVERVIEW) opdsEntryList.value = opdsEntryList.value.plus(BACK)
                }
            } catch (e: IOException) {
                Log.e("OPDS List fragment", e.localizedMessage, e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        KoReaderApplication.getContext(),
                        context.getString(R.string.connection_error), Toast.LENGTH_LONG
                    ).show()
                    loadErrorList(url)
                }
            } catch (e: XmlPullParserException) {
                Log.e("OPDS List fragment", e.localizedMessage, e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        KoReaderApplication.getContext(),
                        context.getString(R.string.xml_error), Toast.LENGTH_LONG
                    ).show()
                    loadErrorList(url)
                }
            } catch (e: MalformedURLException) {
                Log.e("OPDS List fragment", e.localizedMessage, e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        KoReaderApplication.getContext(),
                        context.getString(R.string.connection_error_malformedURLException), Toast.LENGTH_LONG
                    ).show()
                    loadErrorList(url)
                }
            }
        }
    }

    private fun loadErrorList(url: String?) {
        if (url != OVERVIEW) opdsEntryList.value = listOf(BACK)
        else opdsEntryList.value = listOf()
    }

    fun goBack() {
        if (navigationHistory.isNotEmpty()) {
            val url = navigationHistory.removeLast()
            load(url, true)
        }
    }

    fun saveOpdsOverviewEntry(pos: Int, name: String, url: String) {
        if (pos < overviewOpds.entries.size) {
            overviewOpds.entries[pos].title = name
            overviewOpds.entries[pos].clickLink?.href = url
        } else {
            overviewOpds.entries.add(Entry(
                title = name,
                clickLink = Link(title = name, href =  url)
            ))
        }
        savePrefs()
        load(OVERVIEW)
    }

    fun loadLink(link: Link) {
        println("loadLink: $link")
        link.href?.let {
            if (it == "back")
                goBack()
            else
                load(it)
        }
    }
}
