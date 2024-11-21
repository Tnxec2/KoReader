package com.kontranik.koreader.compose.ui.bookinfo

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.reader.GoToDialog
import com.kontranik.koreader.compose.ui.shared.ConfirmDialog
import com.kontranik.koreader.compose.ui.shared.TitledDialog
import com.kontranik.koreader.compose.ui.shared.rememberBookInfoUiStateForPath
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookInfoComposable
import com.kontranik.koreader.model.toBookInfoComposable
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.ImageUtils.getBitmap
import kotlinx.coroutines.launch


@Composable
fun BookInfoDialog(
    bookPath: String? = null,
    bookInfoComposable: BookInfoComposable = BookInfoComposable("", getBitmap(LocalContext.current, ImageEnum.Ebook).asImageBitmap(), path = ""),
    navigateBack: () -> Unit,
    deleteBook: (path: String) -> Unit,
    navigateToReader: (path: String) -> Unit,
    navigateToAuthor: (authorId: Long) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val uiState by rememberBookInfoUiStateForPath(bookPath = bookPath ?: bookInfoComposable.path, bookInfoComposable)

    var showDeleteDilaog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = uiState.exit) {
        if (uiState.exit) coroutineScope.launch { navigateBack() }
    }

    TitledDialog(
        title = stringResource(id = R.string.bookinfo),
        onClose = { coroutineScope.launch { navigateBack() }  },
        actionIcons = {
            if (uiState.canDelete) IconButton(onClick = {
                showDeleteDilaog = true
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                    contentDescription = "Delete",
                )
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    navigateToReader(bookInfoComposable.path)
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_iconmonstr_book_opened),
                    contentDescription = "Read",
                )
            }
        }
    ) {
        BookInfoContent(
            bookInfoDetails = uiState.bookInfoComposable,
            navigateToAuthor = navigateToAuthor,
        )
    }

    if (showDeleteDilaog) {
        ConfirmDialog(
            title = "Delete Book",
            text = stringResource(R.string.sure_delete_book),
            onDismissRequest = { showDeleteDilaog = false },
            onConfirmation = {
                coroutineScope.launch {
                    deleteBook(bookInfoComposable.path)
                    navigateBack()
                }
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun BookInfoDialogPreview() {
    val context = LocalContext.current

    val mocupBookInfo = BookInfo(
        title = "Book title",
        authors = mocupAuthors,
        cover = AppCompatResources.getDrawable(context, R.drawable.book_mockup)
            ?.let { ImageUtils.drawableToBitmap(it) },
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
            BookInfoDialog(
                bookInfoComposable = mocupBookInfo.toBookInfoComposable(),
                navigateBack = {  },
                deleteBook = {},
                navigateToReader = {}) {

            }
        }

    }
}