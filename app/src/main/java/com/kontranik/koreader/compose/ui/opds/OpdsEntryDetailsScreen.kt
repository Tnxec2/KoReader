package com.kontranik.koreader.compose.ui.opds

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.shared.Html
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.opds.model.Author
import com.kontranik.koreader.opds.model.Content
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.opds.model.OpdsTypes
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.compose.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OpdsEntryDetailsScreen(
    drawerState: DrawerState,
    entry: Entry,
    startUrl: String,
    navigateBack: () -> Unit,
    openInBrowser: (Link) -> Unit,
    onClickOpdsEntryLink: (Link) -> Unit,
    download: (Entry, Link) -> Unit,
    modifier: Modifier = Modifier
) {

    OpdsEntryDetailsComponent(
        drawerState = drawerState,
        entry = entry,
        startUrl = startUrl,
        navigateBack = { navigateBack() },
        download = { e, link -> download(e, link)},
        onClickOpdsEntryLink = { link -> onClickOpdsEntryLink(link) },
        openInBrowser = { link -> openInBrowser(link) },
        modifier = modifier,
    )
}

@Composable
fun OpdsEntryDetailsComponent(
    entry: Entry,
    startUrl: String,
    drawerState: DrawerState,
    onClickOpdsEntryLink: (Link) -> Unit,
    download: (Entry, Link) -> Unit,
    openInBrowser: (Link) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val cover = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val rels = remember {
        mutableStateOf(hashMapOf<String, List<Link>>())
    }

    LaunchedEffect(key1 = Unit) {
        val tempRels = hashMapOf<String, List<Link>>()

        entry.otherLinks?.sortedBy { link: Link -> link.rel }
            ?.groupBy { it.rel }
            ?.forEach { (rel, links) ->
                tempRels[OpdsTypes.mapRel(rel)] = links
            }
        rels.value = tempRels

        var icon: Bitmap? = null
        try {
            if (entry.image?.href != null) {
                icon = ImageUtils.drawableFromUrl(entry.image.href, startUrl)
            }
            withContext(Dispatchers.Main) {
                if (icon != null) {
                    cover.value = icon.asImageBitmap()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.opds_entry_details,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back
                            )
                        )
                    }
                },
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(paddingSmall)) {
            cover.value?.let {
                item {
                    Image(
                        bitmap = it,
                        contentDescription = entry.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            item {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                entry.author?.let {
                    Text(
                        text = it.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            item {
                entry.content?.let {
                    Html(text = it.data)
                }
            }
            item {
                if (rels.value.values.isNotEmpty())
                    Text(
                        text = stringResource(id = R.string.links),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = paddingSmall)
                    )
            }
            rels.value.mapKeys { rel ->
                item {
                    Text(
                        text = rel.key,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = paddingSmall)
                    )
                }

                rel.value.map { link ->
                    item {
                        Text(
                            text = link.title ?: "",
                            fontSize = 18.sp,
                            style = TextStyle(
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier
                                .heightIn(min = 30.dp)
                                .fillMaxWidth()
                                .padding(bottom = paddingSmall)
                                .clickable {
                                    Log.d("ENTRYLINK", "clicked entry.otherLinks: $link")
                                    if (link.isCatalogEntry()) {
                                        onClickOpdsEntryLink(link)
                                        navigateBack()
                                    } else if (link.isDownloadable()) {
                                        download(entry, link)
                                    } else {
                                        openInBrowser(link)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun OpdsEntryDetailsComponentPreview() {

    val href = stringResource(id = R.string.book_cover_base64)

    val entrysState = remember {
        mutableStateOf(
            Entry(
                title = "Sample Entry",
                author = Author(mocupAuthors[0].asString()),
                content = Content(
                    type = "HTML", data = "<h1>Header Level 1</h1>\n" +
                            "<p><strong>Auf dem letzten Hause eines kleinen Dörfchens</strong> befand sich ein <abbr title=\"Behausung eines langbeinigen Vogels\">Storchnest</abbr>. Die Storchmutter saß im Neste bei ihren vier Jungen, welche den Kopf mit dem kleinen <em>schwarzen Schnabel</em>, denn er war noch nicht rot geworden, hervorstreckten. Ein Stückchen davon stand auf der Dachfirste starr und steif der Storchvater <code>syntax</code>. Man hätte meinen können, er wäre aus Holz gedrechselt, so stille stand er. „Gewiss sieht es recht vornehm aus, dass meine Frau eine Schildwache bei dem Neste hat!“ dachte er. Und er stand unermüdlich auf <a href=\"#nirgendwo\" title=\"Title für einem Bein\">einem Beine</a>.</p>\n" +
                            "\n" +
                            "<h2>Header Level 2</h2>\n" +
                            "<ol>\n" +
                            "\t<li>Und was dann? fragten die Storchkinder.</li>\n" +
                            "\t<li>Dann werden wir aber doch gepfählt, wie die Knaben behaupteten, und höre nur, jetzt sagen sie es schon wieder!</li>\n"
                ),
                image = Link(title = "Cover", href = href),
                otherLinks = listOf(
                    Link(type = OpdsTypes.TYPE_LINK_ATOM_XML, title = "Link1", href = "", rel = OpdsTypes.REL_RELATED),
                    Link(type = OpdsTypes.TYPE_LINK_ATOM_XML, title = "Link2", href = "", rel = OpdsTypes.REL_RELATED),
                    Link(type = OpdsTypes.TYPE_APP_FB2, title = "FB2", href = "", rel = OpdsTypes.REL_OPEN_ACCESS),
                    Link(type = OpdsTypes.TYPE_APP_EPUB, title = "EPUB", href = "", rel = OpdsTypes.REL_OPEN_ACCESS),
                )
            )

        )
    }

    AppTheme {
        OpdsEntryDetailsComponent(
            drawerState = DrawerState(DrawerValue.Closed),
            entry = entrysState.value,
            startUrl = "StartUrl",
            navigateBack = { },
            openInBrowser = { },
            onClickOpdsEntryLink = { },
            download = { _, _ -> },
        )
    }
}
