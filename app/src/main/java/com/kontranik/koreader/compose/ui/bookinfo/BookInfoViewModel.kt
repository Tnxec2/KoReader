package com.kontranik.koreader.compose.ui.bookinfo

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.koreader.R
import com.kontranik.koreader.database.model.Author
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.parser.EbookHelper
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.launch

class BookInfoViewModell(
    savedStateHandle: SavedStateHandle,
    val context: Context,
) : ViewModel() {

    var bookInfoUiState by mutableStateOf(BookInfoUiState())
        private set

    var canDeleteState by mutableStateOf(false)
        private set

    var exit by mutableStateOf(false)

    val bookPath: String? = savedStateHandle[BookInfoDestination.BOOK_PATH]

    init {
        //readBookInfo(bookPath)
    }

    fun readBookInfo(bookPath: String?) {
        viewModelScope.launch {
            exit = false
            bookPath?.let { path ->
                if (bookPath == "preview") {
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
                    bookInfoUiState = BookInfoUiState(mocupBookInfo.toBookInfoDetails())
                    return@launch
                }

                val uri = Uri.parse(bookPath)
                val doc = DocumentFile.fromSingleUri(context, uri)

                canDeleteState = doc?.canWrite() == true

                val ebookHelper = EbookHelper.getHelper(context, path)

                if ( ebookHelper == null) exit = true

                ebookHelper?.let { helper ->
                    val bookInfo = helper.getBookInfoTemporary(path)
                    if ( bookInfo == null) exit = true

                    bookInfo?.let { info ->
                        bookInfoUiState = BookInfoUiState(info.toBookInfoDetails())
                    }
                }

            }
        }
    }
}

data class BookInfoUiState(
    val bookInfoDetails: BookInfoDetails = BookInfoDetails()
)


data class BookInfoDetails(
    var title: String = "",
    var cover: Bitmap? = null,
    var authors: List<Author> = listOf(),
    val allAuthors: String = "",
    val filename: String = "",
    val path: String = "",
    var annotation: String = ""
)

fun BookInfo.toBookInfoDetails(): BookInfoDetails {
    return BookInfoDetails(
        title = title ?: "",
        cover = cover,
        authors = authors?.toList() ?: listOf(),
        allAuthors = authorsAsString(),
        filename = filename,
        path = path,
        annotation = annotation
    )
}