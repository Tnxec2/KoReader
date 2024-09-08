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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.openfile.OpenFileViewModel
import com.kontranik.koreader.compose.ui.reader.BookReaderViewModel
import com.kontranik.koreader.database.BookStatusViewModel
import kotlinx.coroutines.launch


object BookInfoDestination : NavigationDestination {
    override val route = "BookInfo"
    override val titleRes = R.string.bookinfo
    const val BOOK_PATH = "bookpatn"
    val routeWithArgs = "$route?path={$BOOK_PATH}"
}

@Composable
fun BookInfoScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    navigateToReader: () -> Unit,
    navigateToAuthor: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier,
    bookInfoViewModell: BookInfoViewModell = viewModel(factory = AppViewModelProvider.Factory),
    bookReaderViewModel: BookReaderViewModel,
    openFileViewModel: OpenFileViewModel,
    bookStatusViewModel: BookStatusViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val coroutineScope = rememberCoroutineScope()

    val bookInfoDetails = bookInfoViewModell.bookInfoUiState.value.bookInfoDetails
    val canDeleteState = bookInfoViewModell.canDeleteState

    var showDeleteDilaog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = bookInfoViewModell.exit) {
        if (bookInfoViewModell.exit) coroutineScope.launch { navigateBack() }
    }

    LaunchedEffect(key1 = bookInfoDetails) {
        bookInfoViewModell.readLibraryInfo()
    }

    Scaffold(
        topBar = {
            AppBar (
            title = R.string.bookinfo,
            drawerState = drawerState,
            navigationIcon = {
                IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(
                        id = R.string.back))
                }
            },
            appBarActions = listOf {
                if (canDeleteState) IconButton(onClick = { showDeleteDilaog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                        contentDescription = "Delete",
                    )
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        bookReaderViewModel.changePath(bookInfoViewModell.bookPath)
                        navigateToReader()
                    }
                }) {
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

        BookInfoContent(
            bookInfoDetails = bookInfoDetails,
            navigateToAuthor = navigateToAuthor ,
            modifier = Modifier.padding(padding)
        )

        if (showDeleteDilaog) {
            ConfirmDialog(
                title = "Delete Book",
                text = stringResource(R.string.sure_delete_book),
                onDismissRequest = { showDeleteDilaog = false },
                onConfirmation = {
                    coroutineScope.launch {
                        bookStatusViewModel.deleteByPath(bookInfoViewModell.bookPath)
                        bookReaderViewModel.bookPath.postValue(null)
                        openFileViewModel.loadPath()
                        navigateBack()
                    }
                }
            )
        }
    }
}