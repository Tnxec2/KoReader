package com.kontranik.koreader.compose.ui.reader

import android.app.ActionBar.LayoutParams
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.text.style.ImageSpan
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.kontranik.koreader.compose.ui.quickmenu.QuickMenuDialog
import com.kontranik.koreader.compose.ui.settings.Actions
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.compose.ui.settings.TextType
import com.kontranik.koreader.model.ScreenZone
import com.kontranik.koreader.compose.ui.reader.BookReaderTextview.BookReaderTextviewListener
import com.kontranik.koreader.compose.ui.shared.VolumeButtonsHandler
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

    LaunchedEffect(key1 = settingsViewModel.pageViewSettings.value) {
        bookReaderViewModel.pageViewSettings.value = settingsViewModel.pageViewSettings.value.copy()
    }

    val savedColors by settingsViewModel.selectedColors

    LaunchedEffect(key1 = savedColors) {
        bookReaderViewModel.themeColors.value = savedColors.copy()
    }

    var currentBackgroundColor by remember {
        mutableStateOf(savedColors.colorBackground)
    }

    val currentInfoAreaTextSize = remember {
        mutableFloatStateOf(settingsViewModel.pageViewSettings.value.textSizeInfoArea)
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
                    bookReaderViewModel.bookPath.value?.let { navigateToBookmarks(it) }
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
                    //
                }

                override fun onSwipeLeftOnBookReaderTextview() {
                    bookReaderViewModel.goToNextPage()
                }

                override fun onSwipeRightOnBookReaderTextview() {
                    bookReaderViewModel.doPagePrev()
                }

                override fun onSlideUpOnBookReaderTextView(point: Point) {
                    //
                }

                override fun onSlideDownOnBookReaderTextView(point: Point) {
                    //
                }

                override fun onClickImageOnBookReaderTextview(imageSpan: ImageSpan) {
                    bookReaderViewModel.getImageByteArray(imageSpan)?.let { byteArray ->
                        inBookClickedImageBitmap.value =
                            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    }
                }

                override fun onAddBookmark(start: Int, end: Int, text: CharSequence) {
                    bookReaderViewModel.addBookmark(start, text)
                }
            }
        )
    }

    VolumeButtonsHandler(
        onVolumeUp = {
            println("volume up")
            bookReaderViewModel.doPagePrev()
        },
        onVolumeDown = {
            println("volume down")
            bookReaderViewModel.goToNextPage()
        }
    )

    Scaffold(
    ) { padding ->
        BookReaderBackgroundedContent(
            backgroundColor = currentBackgroundColor,
            colors = savedColors,
            modifier = Modifier.padding(padding)
        ) {
            AndroidView(
                factory = { ctx ->
                    BookReaderTextview(ctx, bookReaderViewModel).apply {
                        textview.value = this
                        layoutParams =
                            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    }
                },
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned { layoutCoordinates ->
                        bookReaderViewModel.changeReaderSize(layoutCoordinates.size)
                    }
            )

            InfoArea(
                textSize = currentInfoAreaTextSize,
                font = settingsViewModel.fonts.value[TextType.InfoArea]!!.getTypeface(),
                left = bookReaderViewModel.infoTextLeft.observeAsState("").value.toString(),
                middle = bookReaderViewModel.infoTextRight.observeAsState("").value.toString(),
                right = bookReaderViewModel.infoTextSystemstatus.observeAsState("").value.toString(),
                onClickLeft = { showGotoDialog.value = true },
                onClickMiddle = { showGotoDialog.value = true },
                onClickRight = { showQuickMenu.value = true },
                textColor = savedColors.colorsInfo,
                modifier = Modifier
                    .padding(
                        start = Dp(savedColors.marginLeft.toFloat()),
                        end = Dp(savedColors.marginLeft.toFloat()),
                        bottom = Dp(savedColors.marginBottomInfoArea.toFloat())
                    )
            )

            bookReaderViewModel.note.value?.let {
                NoteViewDialog(
                    note = it,
                    onClose = { bookReaderViewModel.note.value = null }
                )
            }

            inBookClickedImageBitmap.value?.let {
                ImageViewerDialog(
                    imageBitmap = it,
                    isDarkMode = settingsViewModel.isDarkMode(context),
                    onClose = { inBookClickedImageBitmap.value = null },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            if (showGotoDialog.value) {
                bookReaderViewModel.book.value?.getPageScheme()?.let { scheme ->
                    GoToDialog(
                        sectionInitial = bookReaderViewModel.getCurSection(),
                        pageInitial = bookReaderViewModel.getCurTextPage(),
                        maxPage = scheme.countTextPages,
                        aSections = scheme.sections,
                        gotoSection = {
                            corutineScope.launch {
                                showGotoDialog.value = false
                                bookReaderViewModel.goToSection(it)
                            }
                        },
                        gotoPage = {
                            corutineScope.launch {
                                showGotoDialog.value = false
                                bookReaderViewModel.goToPage(it)
                            }
                        },
                        onClose = { showGotoDialog.value = false }
                    )
                }
            }

            if (showQuickMenu.value) {
                QuickMenuDialog(
                    onFinishQuickMenuDialog = { textSize: Float, textSizeInfoArea: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorThemeIndex: Int ->
                        settingsViewModel.onFinishQuickMenuDialog(
                            textSize,
                            textSizeInfoArea,
                            lineSpacingMultiplier,
                            letterSpacing,
                            colorThemeIndex
                        )
                        showQuickMenu.value = false
                    },
                    onClose = {
                        bookReaderViewModel.pageViewSettings.value =
                            settingsViewModel.pageViewSettings.value.copy()
                        bookReaderViewModel.themeColors.value = savedColors.copy()
                        currentBackgroundColor = savedColors.colorBackground
                        showQuickMenu.value = false
                        currentInfoAreaTextSize.floatValue =
                            settingsViewModel.pageViewSettings.value.textSizeInfoArea
                    },
                    onAddBookmark = {
                        bookReaderViewModel.addBookmark(textview.value.text.toString())
                        showQuickMenu.value = false
                    },
                    onOpenBookmarks = {
                        showQuickMenu.value = false
                        corutineScope.launch {
                            bookReaderViewModel.bookPath.value?.let {
                                navigateToBookmarks(it)
                            }
                        }
                    },
                    onOpenBookInfo = {
                        showQuickMenu.value = false
                        bookReaderViewModel.bookPath.value?.let { navigateToBookInfo(it) }
                    },
                    onChangeColorThemeQuickMenuDialog = { _: String, colorThemeIndex: Int ->
                        bookReaderViewModel.themeColors.value =
                            settingsViewModel.colors[colorThemeIndex]!!.value
                        currentBackgroundColor =
                            settingsViewModel.colors[colorThemeIndex]!!.value.colorBackground
                    },
                    onChangeLineSpacingQuickMenuDialog = { lineSpacingMultiplier ->
                        bookReaderViewModel.pageViewSettings.value =
                            bookReaderViewModel.pageViewSettings.value?.copy(lineSpacingMultiplier = lineSpacingMultiplier)
                    },
                    onChangeLetterSpacingQuickMenuDialog = { letterSpacing ->
                        bookReaderViewModel.pageViewSettings.value =
                            bookReaderViewModel.pageViewSettings.value?.copy(letterSpacing = letterSpacing)
                    },
                    onChangeTextSizeQuickMenuDialog = { textSize ->
                        bookReaderViewModel.pageViewSettings.value =
                            bookReaderViewModel.pageViewSettings.value?.copy(textSize = textSize)
                    },
                    onChangeTextSizeInfoAreaQuickMenuDialog = { textSizeInfoArea ->
                        bookReaderViewModel.pageViewSettings.value =
                            bookReaderViewModel.pageViewSettings.value?.copy(textSizeInfoArea = textSizeInfoArea)
                        currentInfoAreaTextSize.floatValue = textSizeInfoArea
                    },
                    selectedColorTheme = settingsViewModel.selectedColorTheme.intValue,
                    textSize = settingsViewModel.pageViewSettings.value.textSize,
                    textSizeInfoArea = currentInfoAreaTextSize.floatValue,
                    lineSpacingMultiplier = settingsViewModel.pageViewSettings.value.lineSpacingMultiplier,
                    letterSpacing = settingsViewModel.pageViewSettings.value.letterSpacing,
                    selectedFont = settingsViewModel.fonts.value[TextType.Normal]!!.getTypeface(),
                    selectedFontInfoArea = settingsViewModel.fonts.value[TextType.InfoArea]!!.getTypeface(),
                )
            }
        }
    }
}
