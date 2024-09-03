package com.kontranik.koreader.compose.ui.bookinfo

import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.SavedStateHandle
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLandscapeLightDark
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.Html
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.launch


@Composable
fun BookInfoContent(
    bookInfoDetails: BookInfoDetails,
    navigateToAuthor: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()


        Column(
            modifier = modifier
                .padding(paddingSmall)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Card {
                Column(Modifier.padding(paddingMedium)) {
                    Text(
                        text = bookInfoDetails.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingSmall)
                    )
                    Text(
                        text = bookInfoDetails.allAuthors,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingSmall)
                    )
                    bookInfoDetails.cover?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = bookInfoDetails.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

            }
            Card(modifier = Modifier.padding(vertical = paddingMedium)) {
                Html(
                    bookInfoDetails.annotation,
                    modifier = Modifier.padding(paddingMedium)
                )
            }
            Card(modifier = Modifier.padding(bottom = paddingMedium)) {
                Column(modifier = Modifier.padding(paddingMedium)) {
                    bookInfoDetails.authors.filter { it.id != null }.map { author ->
                        Text(
                            text = author.asString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = paddingSmall)
                                .clickable {
                                    coroutineScope.launch {
                                        author.id?.let { navigateToAuthor(it) }
                                    }
                                }
                        )
                    }
                }
            }
        }


}

@PreviewPortraitLandscapeLightDark
@Composable
private fun BookInfoContentPreview() {
    val context = LocalContext.current

    val mocupBookInfo = BookInfo(
        title = "Book title",
        authors = mocupAuthors,
        cover = AppCompatResources.getDrawable(context, R.drawable.book_mockup)?.let { ImageUtils.drawableToBitmap(it)},
        filename = "filename",
        path = "path",
        annotation = "<h1>Header Level 1</h1>\n" +
                "<p><strong>Auf dem letzten Hause eines kleinen Dörfchens</strong> befand sich ein <abbr title=\"Behausung eines langbeinigen Vogels\">Storchnest</abbr>. Die Storchmutter saß im Neste bei ihren vier Jungen, welche den Kopf mit dem kleinen <em>schwarzen Schnabel</em>, denn er war noch nicht rot geworden, hervorstreckten. Ein Stückchen davon stand auf der Dachfirste starr und steif der Storchvater <code>syntax</code>. Man hätte meinen können, er wäre aus Holz gedrechselt, so stille stand er. „Gewiss sieht es recht vornehm aus, dass meine Frau eine Schildwache bei dem Neste hat!“ dachte er. Und er stand unermüdlich auf <a href=\"#nirgendwo\" title=\"Title für einem Bein\">einem Beine</a>.</p>\n" +
                "\n" +
                "<h2>Header Level 2</h2>\n" +
                "<ol>\n" +
                "\t<li>Und was dann? fragten die Storchkinder.</li>\n" +
                "\t<li>Dann werden wir aber doch gepfählt, wie die Knaben behaupteten, und höre nur, jetzt sagen sie es schon wieder!</li>\n" +
                "</ol>\n"
    )

    AppTheme {
        Surface {
            BookInfoContent(
                bookInfoDetails = mocupBookInfo.toBookInfoDetails(),

                navigateToAuthor = {},
            )
        }
    }
}