package com.kontranik.koreader.compose.ui.bookinfo

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.ConfirmDialog
import com.kontranik.koreader.compose.ui.shared.rememberBookInfoUiStateForPath
import com.kontranik.koreader.model.BookInfoComposable
import com.kontranik.koreader.utils.ImageEnum
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

        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { coroutineScope.launch { navigateBack() } }) {
            Card(modifier = Modifier
                .fillMaxSize().padding(horizontal = paddingSmall)) {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(
                                    id = R.string.back
                                )
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.bookinfo),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
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
                    BookInfoContent(
                        bookInfoDetails = uiState.bookInfoComposable,
                        navigateToAuthor = navigateToAuthor,
                        modifier = Modifier
                    )
                }
            }
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