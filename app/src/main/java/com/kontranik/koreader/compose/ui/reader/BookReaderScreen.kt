package com.kontranik.koreader.compose.ui.reader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.text.style.ImageSpan
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.kontranik.koreader.compose.ui.settings.Actions
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.compose.ui.settings.TextType
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.ui.components.BookReaderTextview
import com.kontranik.koreader.ui.components.BookReaderTextview.BookReaderTextviewListener
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.launch

@Composable
fun BookReaderScreen(
    drawerState: DrawerState,
    navigateToMainMenu: () -> Unit,
    navigateToBookmarks: () -> Unit,
    navigateToBookInfo: (String) -> Unit,
    settingsViewModel: SettingsViewModel,
    bookReaderViewModel: BookReaderViewModel,
    modifier: Modifier = Modifier
) {
    val corutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val pageViewSettings = settingsViewModel.pageViewSettings
    LaunchedEffect(key1 = pageViewSettings.value) {
        bookReaderViewModel.pageViewSettings.value = pageViewSettings.value.copy()
    }

    val colors by settingsViewModel.selectedColors

    LaunchedEffect(key1 = colors) {
        bookReaderViewModel.pageViewColorSettings.value = colors.copy()
    }

    var backgroundColor by remember {
        mutableStateOf(colors.colorBackground)
    }

    val showQuickMenu = remember {
        mutableStateOf(false)
    }

    val showGotoDialog = remember {
        mutableStateOf(false)
    }

    val imageBitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    fun tap(tapAction: Actions?) {
        println("tap. tapAction: $tapAction")
        when (tapAction) {
            Actions.PagePrev -> {
                bookReaderViewModel.doPagePrev()
            }
            Actions.PageNext -> {
                bookReaderViewModel.goToNextPage()
            }
            Actions.QuickMenu -> {
                showQuickMenu.value = true
            }
            Actions.MainMenu -> {
                corutineScope.launch { navigateToMainMenu() }
            }
            Actions.GoTo -> {
                showGotoDialog.value = true
            }
            Actions.Bookmarks -> {
                corutineScope.launch { navigateToBookmarks() }
            }
            else -> {
            }
        }
    }

    val textview = remember {
        mutableStateOf(
            BookReaderTextview(context, bookReaderViewModel)
        )
    }

    val savedBookStatusStats = bookReaderViewModel.savedBookStatus.observeAsState()
    LaunchedEffect(key1 = savedBookStatusStats.value) {
        bookReaderViewModel.goToPositionByBookStatus(textview.value, savedBookStatusStats.value)
    }

    LaunchedEffect(key1 = Unit) {
        corutineScope.launch {
            bookReaderViewModel.loadBook(context)
        }
    }

    LaunchedEffect(key1 = textview.value) {
        textview.value.setListener(
            object : BookReaderTextviewListener {
                override fun onClickLinkOnBookReaderTextview(url: String) {
                    bookReaderViewModel.loadNote(url)
                }
                override fun onTapOnBookReaderTextview(zone: ScreenZone) {
                    tap(settingsViewModel.tapOneAction.value[zone])
                }
                override fun onDoubleTapOnBookReaderTextview(zone: ScreenZone) {
                    tap(settingsViewModel.tapDoubleAction.value[zone])
                }
                override fun onLongTapOnBookReaderTextview(zone: ScreenZone) {
                    tap(settingsViewModel.tapLongAction.value[zone])
                }
                override fun onSwipeLeftOnBookReaderTextview() {
                    bookReaderViewModel.doPagePrev()
                }
                override fun onSwipeRightOnBookReaderTextview() {
                    bookReaderViewModel.goToNextPage()
                }
                override fun onSlideUpOnBookReaderTextView(point: Point) {
                    //
                }
                override fun onSlideDownOnBookReaderTextView(point: Point) {
                    //
                }
                override fun onClickImageOnBookReaderTextview(imageSpan: ImageSpan) {
                    bookReaderViewModel.getImageByteArray(imageSpan)?.let { byteArray ->

                        imageBitmap.value = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    }
                }
            }
        )
    }

    BookReaderContainer(
        backgroundColor = backgroundColor,

        imageBitmap = imageBitmap.value,
        isDarkMode = settingsViewModel.isDarkMode(context),
        onCloseImage = { imageBitmap.value = null },

        showGotoDialog = showGotoDialog.value,
        onCloseGotoDialog = { showGotoDialog.value = false},
        gotoPage = { corutineScope.launch {
                showGotoDialog.value = false
                bookReaderViewModel.goToPage(it)
            }
        },
        gotoSection = { corutineScope.launch {
            showGotoDialog.value = false
            bookReaderViewModel.goToSection(it)
        }},
        currentSection = bookReaderViewModel.book.value?.getCurSection(),
        currentPage = bookReaderViewModel.book.value?.getCurTextPage(),
        sectionList = bookReaderViewModel.book.value?.getPageScheme()?.sections,
        maxPage = bookReaderViewModel.book.value?.getPageScheme()?.countTextPages,

        showQuickMenu = showQuickMenu.value,
        onCancelQuickMenuDialog = {
            bookReaderViewModel.pageViewSettings.value = pageViewSettings.value.copy()
            bookReaderViewModel.pageViewColorSettings.value = colors.copy()
            backgroundColor = colors.colorBackground
            showQuickMenu.value = false
        },
        onAddBookmarkQuickMenuDialog = {
            bookReaderViewModel.addBookmark(textview.value.text.toString())
            showQuickMenu.value = false
        },
        onShowBookmarklistQuickMenuDialog = {
            showQuickMenu.value = false
            navigateToBookmarks()
        },
        onOpenBookInfoQuickMenuDialog = {
            showQuickMenu.value = false
            bookReaderViewModel.bookPath.value?.let { navigateToBookInfo(it) }
        },
        onChangeColorThemeQuickMenuDialog = { _: String, colorThemeIndex: Int ->
            bookReaderViewModel.pageViewColorSettings.value = settingsViewModel.colors[colorThemeIndex]!!.value
            backgroundColor = settingsViewModel.colors[colorThemeIndex]!!.value.colorBackground
        },
        onChangeTextSizeQuickMenuDialog = { textSize ->
            bookReaderViewModel.pageViewSettings.value = bookReaderViewModel.pageViewSettings.value?.copy(textSize = textSize)
        },
        onChangeLineSpacingQuickMenuDialog = { lineSpacingMultiplier ->
            bookReaderViewModel.pageViewSettings.value = bookReaderViewModel.pageViewSettings.value?.copy(lineSpacingMultiplier = lineSpacingMultiplier)
        },
        onChangeLetterSpacingQuickMenuDialog = { letterSpacing ->
            bookReaderViewModel.pageViewSettings.value = bookReaderViewModel.pageViewSettings.value?.copy(letterSpacing = letterSpacing)
        },
        onFinishQuickMenuDialog = { textSize: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorThemeIndex: Int ->
            settingsViewModel.onFinishQuickMenuDialog(textSize, lineSpacingMultiplier, letterSpacing, colorThemeIndex)
            showQuickMenu.value = false
        },
        infoLeft = bookReaderViewModel.infoTextLeft.observeAsState("").value.toString(),
        infoMiddle = bookReaderViewModel.infoTextRight.observeAsState("").value.toString(),
        infoRight = bookReaderViewModel.infoTextSystemstatus.observeAsState("").value.toString(),
        colors = colors,
        paddingValues = PaddingValues(
            top = Dp(colors.marginTop.toFloat()),
            bottom = Dp(colors.marginBottom.toFloat()),
            start = Dp(colors.marginLeft.toFloat()),
            end = Dp(colors.marginRight.toFloat()),
        ),

        onClickInfoLeft = {},
        onClickInfoMiddle = {},
        onClickInfoRight = {},
        textView = {
            AndroidView(
                factory={ ctx ->
                    BookReaderTextview(ctx, bookReaderViewModel).apply{
                        textview.value.removeListener()
                        textview.value = this
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned {
                        if (bookReaderViewModel.pageLoaderToken.pageSize.width != it.size.width ||
                            bookReaderViewModel.pageLoaderToken.pageSize.height != it.size.height
                        ) {
                            bookReaderViewModel.pageLoaderToken.pageSize = it.size
                            bookReaderViewModel.recalcCurrentPage()
                        }
                    }
            )
        },
        pageViewSettings = settingsViewModel.pageViewSettings.value,
        selectedFont = settingsViewModel.fonts.value[TextType.Normal]!!.getTypeface(),
        selectedTheme = settingsViewModel.selectedColorTheme.intValue,
        onCloseNote = { bookReaderViewModel.note.value = null },
        note = bookReaderViewModel.note.value
    )
}
