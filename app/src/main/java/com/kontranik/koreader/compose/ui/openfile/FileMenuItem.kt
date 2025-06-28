package com.kontranik.koreader.compose.ui.openfile

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.rememberBookInfoForFileItem
import com.kontranik.koreader.database.model.mocupAuthors
import com.kontranik.koreader.model.BookInfo
import com.kontranik.koreader.model.BookInfoComposable
import com.kontranik.koreader.utils.FileItem
import com.kontranik.koreader.utils.ImageEnum
import com.kontranik.koreader.utils.ImageUtils


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileMenuItem(
    fileItem: FileItem,
    onClick: (bookInfo: BookInfoComposable) -> Unit,
    onDeleteStorage: () -> Unit,
    onUpdateLibrary: () -> Unit,
    modifier: Modifier = Modifier,
    onUpdateBookInfo: (bookInfoComposable: BookInfoComposable) -> Unit) {

    val bookInfoComposableState = rememberBookInfoForFileItem(fileItem, onUpdateBookInfo)

    var showPopup by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .combinedClickable(
                onClick = { onClick(bookInfoComposableState.value) },
                onLongClick = {
                    if (fileItem.isDir || fileItem.isStorage) showPopup = true
                }
            )
            .fillMaxWidth()
    ) {
        if (fileItem.isDir || fileItem.isStorage || fileItem.isRoot)
            Icon(
                bitmap = bookInfoComposableState.value.cover!!,
                contentDescription = bookInfoComposableState.value.title,
                modifier = Modifier
                    .padding(horizontal = paddingMedium, vertical = paddingSmall)
                    .size(width = 50.dp, height = 100.dp)
            )
        else
            Image(
                bitmap = bookInfoComposableState.value.cover!!,
                contentDescription = bookInfoComposableState.value.title,
                modifier = Modifier
                    .padding(horizontal = paddingMedium, vertical = paddingSmall)
                    .size(width = 50.dp, height = 100.dp)
            )
        Column(
            Modifier
                .padding(end = paddingMedium)
                .fillMaxWidth()) {
            Text(
                text = bookInfoComposableState.value.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
            if (bookInfoComposableState.value.sequenceName.isNotEmpty()) Text(
                text = "${bookInfoComposableState.value.sequenceName} #${bookInfoComposableState.value.sequenceNumber}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
            if (bookInfoComposableState.value.authorsAsString.isNotEmpty()) Text(
                text = bookInfoComposableState.value.authorsAsString,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
            Text(
                text = fileItem.path,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
        }
        if (showPopup) {
            FielItemPopupMenu(
                fileItem = fileItem,
                onDelete = { showPopup = false; onDeleteStorage() },
                onUpdateLibrary = { showPopup = false; onUpdateLibrary() },
                onClose = { showPopup = false })
        }
    }
}

@PreviewLightDark
@Composable
private fun FileMenuItemPreview() {
    val context = LocalContext.current
    val bitmap = AppCompatResources.getDrawable(context, R.drawable.book_mockup)?.let { ImageUtils.drawableToBitmap(it)}
    AppTheme {
        Surface {
            FileMenuItem(
                fileItem = FileItem(
                    image = ImageEnum.Ebook,
                    name = "Title",
                    path =  "/path/to/book",
                    uriString = null,
                    isDir = false,
                    isRoot = false,
                    bookInfo = BookInfo(
                        title = "Book title",
                        cover = bitmap,
                        authors = mocupAuthors,
                        filename = "book",
                        path = "/path/to/book",
                        annotation = "annotation",
                        sequenceName = "Book Series",
                        sequenceNumber = "1.0"
                    ),
                    isStorage = false
                ),
                onClick = {  },
                onUpdateLibrary = {},
                onDeleteStorage = {},
                onUpdateBookInfo = {},
            )
        }
    }
}