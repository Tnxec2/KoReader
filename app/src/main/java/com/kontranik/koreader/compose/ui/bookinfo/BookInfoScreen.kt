package com.kontranik.koreader.compose.ui.bookinfo

import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.compose.ui.shared.ConfirmDialog
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.navigation.NavigationDestination
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLandscapeLightDark
import com.kontranik.koreader.database.model.Author
import de.kontranik.freebudget.ui.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import kotlinx.coroutines.launch


object BookInfoDestination : NavigationDestination {
    override val route = "BookInfo"
    override val titleRes = R.string.bookinfo
    const val BOOK_PATH = "bookpatn"
    val routeWithArgs = "$route/{$BOOK_PATH}"
}

@Composable
fun BookInfoScreen(
    drawerState: DrawerState,
    bookUri: String,
    navigateBack: () -> Unit,
    navigateToAuthor: (author: Author) -> Unit,
    onReadBook: (bookUri: String)->Unit,
    onDeleteBook: (bookUri: String)-> Unit,
    modifier: Modifier = Modifier,
    viewModel: IBookInfoViewModell = viewModel(factory = AppViewModelProvider.Factory),
) {

    val coroutineScope = rememberCoroutineScope()

    val bookInfoDetails = viewModel.bookInfoUiState.bookInfoDetails
    val canDeleteState = viewModel.canDeleteState

    var showDeleteDilaog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = viewModel.exit) {
        if (viewModel.exit) coroutineScope.launch { navigateBack() }
    }

    if (showDeleteDilaog) {
        ConfirmDialog(text = stringResource(R.string.sure_delete_book), onDismissRequest = { showDeleteDilaog = false }, onConfirmation = { onDeleteBook(bookUri) })
    }

    Scaffold(
        topBar = {
            AppBar (
            title = R.string.bookinfo,
            drawerState = drawerState,
                appBarActions = listOf {
                    if (canDeleteState) IconButton(onClick = { showDeleteDilaog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                            contentDescription = "Delete",
                        )
                    }
                    IconButton(onClick = { onReadBook(bookUri) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_iconmonstr_book_opened),
                            contentDescription = "Read",
                        )
                    }
                }
            )

         },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(paddingSmall)
                .fillMaxWidth()
        ) {
            Column(modifier = modifier
                .verticalScroll(rememberScrollState())
            ) {
                bookInfoDetails.cover?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = bookInfoDetails.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text(text = bookInfoDetails.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                )
                Text(text = bookInfoDetails.allAuthors,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                )

                Card(modifier = Modifier.padding(bottom = paddingMedium)) {
                    AndroidView(factory = { ctx ->
                        TextView(ctx).apply {
                            text = HtmlCompat.fromHtml(bookInfoDetails.annotation, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                    }, modifier = Modifier.padding(paddingMedium))
                }

                Card(modifier = Modifier.padding(bottom = paddingMedium)) {
                    Column(modifier = Modifier.padding(paddingMedium)) {
                        bookInfoDetails.authors.map { author ->
                            Text(
                                text = author.asString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = paddingSmall)
                                    .clickable {
                                        coroutineScope.launch { navigateToAuthor(author) }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewPortraitLandscapeLightDark
@Composable
private fun BookInfoScreenPreview() {
    val context = LocalContext.current

    AppTheme {
        BookInfoScreen(
            drawerState = DrawerState(DrawerValue.Closed),
            bookUri = "testUri",
            navigateBack = {},
            navigateToAuthor = {},
            onReadBook = { },
            onDeleteBook = { },
            viewModel = BookInfoViewModellPreview( context)
        )
    }
}