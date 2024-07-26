package com.kontranik.koreader.compose.ui.bookinfo

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.launch

val mocupAuthors = listOf(
    Author(0L, "Fannie", null,"Rothfuss"),
    Author(2L, "Darline", null,"Amis"),
    Author(3L, "Patti", null,"Barrowman"),
)



class BookInfoViewModellPreview(
    context: Context,
) : ViewModel(), IBookInfoViewModell {

    override var bookInfoUiState by mutableStateOf(BookInfoUiState())
        private set

    override var canDeleteState by mutableStateOf(false)
        private set

    override var exit by mutableStateOf(false)

    override val bookPath: String? = null

    init {
        viewModelScope.launch {
            val mocupBookInfo = BookInfo(
                title = "Book title",
                authors = mocupAuthors.toMutableList(),
                cover = AppCompatResources.getDrawable(context, R.drawable.book_mockup)?.let {ImageUtils.drawableToBitmap(it)},
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

            exit = false
            bookInfoUiState = BookInfoUiState(mocupBookInfo.toBookInfoDetails())
        }
    }
}
