package com.kontranik.koreader.compose.ui.opds

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.Html
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.opds.model.Author
import com.kontranik.koreader.opds.model.Content
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.opds.model.OpdsTypes
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.launch


@Composable
fun OpdsEntryDetailsContent(
    entry: Entry,
    startUrl: String,
    navigateToOpdsEntryLink: (Link) -> Unit,
    download: (Entry, Link) -> Unit,
    openInBrowser: (Link) -> Unit,
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
        coroutineScope.launch {
            val tempRels = hashMapOf<String, List<Link>>()

            entry.otherLinks?.let { otherLinks ->
                otherLinks
                    .sortedBy { link: Link -> link.rel }
                    .groupBy { it.rel }
                    .forEach { (rel, links) ->
                        tempRels[OpdsTypes.mapRel(rel)] = links
                    }
            }
            rels.value = tempRels

            try {
                entry.image?.href?.let { href ->
                    ImageUtils.drawableFromUrl(href, startUrl)?.let {
                        cover.value = it.asImageBitmap()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier
            .padding(paddingSmall)
            .verticalScroll(rememberScrollState())
    ) {
        Card {
            Column(Modifier.padding(paddingSmall)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleLarge
                )
                entry.author?.let {
                    Text(
                        text = it.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                cover.value?.let {
                    Image(
                        bitmap = it,
                        contentDescription = entry.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }


        Card(Modifier.padding(top = paddingSmall)) {
            Column(
                Modifier.padding(paddingSmall)
            ) {
                entry.content?.let {
                    Html(text = it.data)
                }
            }
        }


        if (rels.value.values.isNotEmpty()) {
            Card(Modifier.padding(top = paddingSmall)) {
                Column(
                    Modifier.padding(paddingSmall)
                ) {
                    if (rels.value.values.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.links),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = paddingSmall)
                        )

                        rels.value.mapKeys { rel ->
                            Text(
                                text = rel.key,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = paddingSmall)
                            )

                            rel.value.map { link ->
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = paddingSmall)
                                    . clickable {
                                        Log.d(
                                            "ENTRYLINK",
                                            "clicked entry.otherLinks: $link"
                                        )
                                        if (link.isCatalogEntry()) {
                                            navigateToOpdsEntryLink(link)
                                        } else if (link.isDownloadable()) {
                                            download(entry, link)
                                        } else {
                                            openInBrowser(link)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = link.getTitle()?.toString() ?: "",
                                        fontSize = 18.sp,
                                        style = TextStyle(
                                            textDecoration = TextDecoration.Underline
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = paddingSmall)
                                    )
                                }


                            }
                        }
                    }
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun OpdsEntryDetailsContentPreview() {

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
                    Link(
                        type = OpdsTypes.TYPE_LINK_ATOM_XML,
                        title = "Link1",
                        href = "",
                        rel = OpdsTypes.REL_RELATED
                    ),
                    Link(
                        type = OpdsTypes.TYPE_LINK_ATOM_XML,
                        title = "Link2",
                        href = "",
                        rel = OpdsTypes.REL_RELATED
                    ),
                    Link(
                        type = OpdsTypes.TYPE_APP_FB2,
                        title = "FB2",
                        href = "",
                        rel = OpdsTypes.REL_OPEN_ACCESS
                    ),
                    Link(
                        type = OpdsTypes.TYPE_APP_EPUB,
                        title = "EPUB",
                        href = "",
                        rel = OpdsTypes.REL_OPEN_ACCESS
                    ),
                )
            )

        )
    }

    AppTheme {
        OpdsEntryDetailsContent(
            entry = entrysState.value,
            startUrl = "StartUrl",
            openInBrowser = { },
            navigateToOpdsEntryLink = { },
            download = { _, _ -> },
        )
    }
}
