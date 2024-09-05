package com.kontranik.koreader.compose.ui.reader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.text.style.ImageSpan
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.kontranik.koreader.compose.ui.settings.Actions
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.compose.ui.settings.TextType
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.compose.ui.reader.BookReaderTextview.BookReaderTextviewListener
import kotlinx.coroutines.launch

@Composable
fun BookReaderScreen(
    drawerState: DrawerState,
    navigateToMainMenu: () -> Unit,
    navigateToBookmarks: (path: String) -> Unit,
    navigateToBookInfo: (String) -> Unit,
    settingsViewModel: SettingsViewModel,
    bookReaderViewModel: BookReaderViewModel,
) {
    val corutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val bookPath by bookReaderViewModel.bookPath.observeAsState()

    val pageViewSettings = settingsViewModel.pageViewSettings
    LaunchedEffect(key1 = pageViewSettings.value) {
        bookReaderViewModel.pageViewSettings.value = pageViewSettings.value.copy()
    }

    val savedColors by settingsViewModel.selectedColors

    LaunchedEffect(key1 = savedColors) {
        bookReaderViewModel.themeColors.value = savedColors.copy()
    }

    var currentBackgroundColor by remember {
        mutableStateOf(savedColors.colorBackground)
    }

    val showQuickMenu = remember {
        mutableStateOf(false)
    }

    val showGotoDialog = remember {
        mutableStateOf(false)
    }

    val inBookClickedImageBitmap = remember {
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
                corutineScope.launch {
                    bookPath?.let { navigateToBookmarks(it) }
                }
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
                        inBookClickedImageBitmap.value = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    }
                }
            }
        )
    }

    BookReaderContent(
        backgroundColor = currentBackgroundColor,
        colors = savedColors,

        clickedImageBitmap = inBookClickedImageBitmap.value,
        isDarkMode = settingsViewModel.isDarkMode(context),
        onCloseImageView = { inBookClickedImageBitmap.value = null },

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
        currentSection = bookReaderViewModel.getCurSection(),
        currentPage = bookReaderViewModel.getCurTextPage(),
        sectionList = bookReaderViewModel.book.value?.getPageScheme()?.sections,
        maxPage = bookReaderViewModel.book.value?.getPageScheme()?.countTextPages,

        showQuickMenu = showQuickMenu.value,
        onCancelQuickMenuDialog = {
            bookReaderViewModel.pageViewSettings.value = pageViewSettings.value.copy()
            bookReaderViewModel.themeColors.value = savedColors.copy()
            currentBackgroundColor = savedColors.colorBackground
            showQuickMenu.value = false
        },
        onAddBookmarkQuickMenuDialog = {
            bookReaderViewModel.addBookmark(textview.value.text.toString())
            showQuickMenu.value = false
        },
        onShowBookmarklistQuickMenuDialog = {
            showQuickMenu.value = false
            corutineScope.launch {
                bookPath?.let {
                    navigateToBookmarks(it)
                }
            }
        },
        onOpenBookInfoQuickMenuDialog = {
            showQuickMenu.value = false
            bookPath?.let { navigateToBookInfo(it) }
        },
        onChangeColorThemeQuickMenuDialog = { _: String, colorThemeIndex: Int ->
            bookReaderViewModel.themeColors.value = settingsViewModel.colors[colorThemeIndex]!!.value
            currentBackgroundColor = settingsViewModel.colors[colorThemeIndex]!!.value.colorBackground
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

        infoAreaFont = settingsViewModel.fonts.value[TextType.InfoArea]!!.getTypeface(),
        infoLeft = bookReaderViewModel.infoTextLeft.observeAsState("").value.toString(),
        infoMiddle = bookReaderViewModel.infoTextRight.observeAsState("").value.toString(),
        infoRight = bookReaderViewModel.infoTextSystemstatus.observeAsState("").value.toString(),
        onClickInfoLeft = { showGotoDialog.value = true },
        onClickInfoMiddle = { showGotoDialog.value = true },
        onClickInfoRight = { showGotoDialog.value = true },


        onSetTextview = {
            textview.value = it
        },
        bookReaderViewModel = bookReaderViewModel,
        onChangeSize = { size ->
            if (bookReaderViewModel.pageViewSettings.value!!.pageSize.width != size.width ||
                bookReaderViewModel.pageViewSettings.value!!.pageSize.height != size.height
            ) {
                bookReaderViewModel.pageViewSettings.value =
                    bookReaderViewModel.pageViewSettings.value!!.copy(
                        pageSize = size)
                bookReaderViewModel.recalcCurrentPage()
            }
        },

        pageViewSettings = settingsViewModel.pageViewSettings.value,
        selectedFont = settingsViewModel.fonts.value[TextType.Normal]!!.getTypeface(),
        selectedTheme = settingsViewModel.selectedColorTheme.intValue,
        onCloseNote = { bookReaderViewModel.note.value = null },
        note = bookReaderViewModel.note.value
    )
}
