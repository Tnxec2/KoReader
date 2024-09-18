package com.kontranik.koreader.compose.ui.opds

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.koreader.AppViewModelProvider
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.UrlHelper
import kotlinx.coroutines.launch


@Composable
fun OpdsListScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    opdsViewModell: OpdsViewModell = viewModel(factory = AppViewModelProvider.Factory),
) {
    LaunchedEffect(key1 = Unit) {
        opdsViewModell.start()
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(key1 = opdsViewModell.showToastState.value) {
        if (opdsViewModell.showToastState.value) {
            Toast.makeText(
                KoReaderApplication.getContext(),
                context.getString(opdsViewModell.toastMessageResourceIdState.intValue), Toast.LENGTH_LONG
            ).show()
            opdsViewModell.hideToast()
        }
    }

    BackHandler(enabled = true, onBack = {
            coroutineScope.launch {
                if (opdsViewModell.canGoBack())
                    opdsViewModell.goBack()
                else
                    navigateBack()
            }
        }
    )

    OpdsListContent(
        drawerState = drawerState,
        entrysState = opdsViewModell.opdsEntryList,
        startUrl = opdsViewModell.startUrl,
        navigateBack = { navigateBack() },
        canAdd = opdsViewModell.canAdd,
        canSearch = opdsViewModell.canSearch,
        canReload = opdsViewModell.canReload,
        contentTitle = opdsViewModell.contentTitle,
        contentAuthor = opdsViewModell.contentAuthor,
        contentIcon = opdsViewModell.contentIcon,
        contentSubTitle = opdsViewModell.contentSubTitle,
        searchTerm = opdsViewModell.searchTerm,
        reloadPage = { opdsViewModell.reloadPage() },
        loadLink = {
            coroutineScope.launch {
                opdsViewModell.loadLink(link = it)
            }
        },
        download = { e, link ->
            coroutineScope.launch {
                downloadOpdsEntry(e, link, opdsViewModell.startUrl.value)
            }
        },
        openInBrowser = { link ->
            coroutineScope.launch {
                link.href?.let {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                UrlHelper.getUrl(
                                    it,
                                    startUrl = opdsViewModell.startUrl.value
                                )
                            )
                        )
                    )
                }
            }
        },
        modifier = modifier,
        onSearch = {
            opdsViewModell.search()
        },
        onDelete = { pos ->
            opdsViewModell.opdslistItemDelete(pos)
        },
        onSaveOpdsOverviewEntry = { pos, title, url ->
            opdsViewModell.saveOpdsOverviewEntry(pos, title, url)
        },
    )
}
