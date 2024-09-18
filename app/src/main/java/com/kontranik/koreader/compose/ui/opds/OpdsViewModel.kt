package com.kontranik.koreader.compose.ui.opds

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.net.MalformedURLException

const val OVERVIEW = "OVERVIEW"

val OPDS_LIST = listOf<Entry>(
    Entry(
        title = "Project Gutenberg",
        clickLink = Link("Project Gutenberg", "https://www.gutenberg.org/ebooks.opds/")
    ),
    Entry(
        title = "Flibusta",
        clickLink = Link( "Flibusta", "https://flibusta.is/opds/")
    ),
    Entry(
        title = "Qumran DE, Izzy\'s freie Bibliotek",
        clickLink = Link("Qumran DE, Izzy\'s freie Bibliotek" ,"https://ebooks.qumran.org/opds/?lang=de")
    ),
    Entry(
            title = "Qumran EN, Izzy\'s Free Library",
    clickLink = Link("Qumran EN, Izzy\'s Free Library", "https://ebooks.qumran.org/opds/?lang=en")
    ),
)


const val OPDS_PREFS_FILE = "OpdsActivitySettings"
const val OPDS_PREF_OPDS_OVERVIEW = "OPDS_OVERVIEW"

class OpdsViewModell(
) : ViewModel() {

    val opdsEntryList = mutableStateOf(listOf<Entry>())

    val showToastState = mutableStateOf(false)
    val toastMessageResourceIdState = mutableIntStateOf(0)

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
        navigationHistory.clear()
        openedLink = null
        load(OVERVIEW)
    }

    private fun loadPrefs() {
        val settings = KoReaderApplication.getContext()
            .getSharedPreferences(
                OPDS_PREFS_FILE,
                Context.MODE_PRIVATE)
        val eP = settings.getStringSet(OPDS_PREF_OPDS_OVERVIEW, null)
        overviewOpds = eP?.let { Opds(
            title = KoReaderApplication.getContext().resources.getString(R.string.opds_overview),
            subtitle = null,
            entries = it.map { s -> splitEntry(s)}.toMutableList(),
            links = emptyList()
        ) } ?:  Opds(
            title = KoReaderApplication.getContext().resources.getString(R.string.opds_overview),
            subtitle = null,
            entries = OPDS_LIST.toMutableList(),
            links = emptyList()
        )
    }

    private fun savePrefs() {
        val settings = KoReaderApplication.getContext()
            .getSharedPreferences(
                OPDS_PREFS_FILE,
                Context.MODE_PRIVATE)
        val prefEditor = settings.edit()

        if ( overviewOpds.entries.isNotEmpty() ) {
            prefEditor.putStringSet(OPDS_PREF_OPDS_OVERVIEW, overviewOpds.entries.map { e -> "${e.title}|${e.clickLink?.href}" }.toMutableSet())
        } else {
            prefEditor.remove(OPDS_PREF_OPDS_OVERVIEW)
        }
        prefEditor.apply()
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

    private fun load(url: String, back: Boolean = false) {
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
            openedLink?.let {
                if (navigationHistory.isEmpty() || navigationHistory.last() != it)
                    navigationHistory.add(it)
            }
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
                    toastMessageResourceIdState.intValue = R.string.connection_error
                    showToastState.value = true
                    loadErrorList(url)
                }
            } catch (e: XmlPullParserException) {
                Log.e("OPDS List fragment", e.localizedMessage, e)
                withContext(Dispatchers.Main) {
                    toastMessageResourceIdState.intValue = R.string.xml_error
                    showToastState.value = true
                    loadErrorList(url)
                }
            } catch (e: MalformedURLException) {
                Log.e("OPDS List fragment", e.localizedMessage, e)
                withContext(Dispatchers.Main) {
                    toastMessageResourceIdState.intValue = R.string.connection_error_malformedURLException
                    showToastState.value = true
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

    fun hideToast() {
        showToastState.value = false
    }
}
