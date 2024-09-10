package com.kontranik.koreader.compose.ui.reader

import android.app.ActionBar.LayoutParams
import android.graphics.Bitmap
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.quickmenu.QuickMenuDialog
import com.kontranik.koreader.compose.ui.settings.ThemeColors
import com.kontranik.koreader.model.PageViewSettings
import java.io.InputStream

@Composable
fun BookReaderContent(
    backgroundColor: Color,
    colors: ThemeColors,

    note: String?,
    onCloseNote: () -> Unit,

    clickedImageBitmap: Bitmap?,
    isDarkMode: Boolean,
    onCloseImageView: () -> Unit,

    currentPage: Int?,
    currentSection: Int?,
    sectionList: List<String>?,
    maxPage: Int?,
    showGotoDialog: Boolean,
    gotoSection: (section: Int) -> Unit,
    gotoPage: (section: Int) -> Unit,
    onCloseGotoDialog: () -> Unit,

    showQuickMenu: Boolean,
    pageViewSettings: PageViewSettings,
    selectedTheme: Int,
    selectedFont: Typeface,
    onCancelQuickMenuDialog: () -> Unit,
    onAddBookmarkQuickMenuDialog: () -> Unit,
    onShowBookmarklistQuickMenuDialog: () -> Unit,
    onOpenBookInfoQuickMenuDialog: () -> Unit,
    onChangeColorThemeQuickMenuDialog: (colorTheme: String, colorThemeIndex: Int) -> Unit,
    onChangeTextSizeQuickMenuDialog: (textSize: Float) -> Unit,
    onChangeLineSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,
    onChangeLetterSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,
    onFinishQuickMenuDialog: (textSize: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorThemeIndex: Int) -> Unit,

    bookReaderViewModel: BookReaderViewModel,
    onSetTextview: (BookReaderTextview) -> Unit,
    onChangeSize: (IntSize) -> Unit,

    infoAreaFont: Typeface,
    infoLeft: String,
    infoMiddle: String,
    infoRight: String,
    onClickInfoLeft: () -> Unit,
    onClickInfoMiddle: () -> Unit,
    onClickInfoRight: () -> Unit,
) {

    val context = LocalContext.current

    val backgroundImage = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val imageBrush = remember(backgroundImage.value) {
        backgroundImage.value?.let {
            ShaderBrush(
                shader = ImageShader(
                    image = it,
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                )
            )
        }
    }

    val painterModifier = if (colors.showBackgroundImage)
        backgroundImage.value?.let {
            if (colors.backgroundImageTiledRepeat)
                imageBrush?.let { brush ->
                    Modifier
                        .background(backgroundColor)
                        .background(brush) }
            else
                Modifier
                    .background(backgroundColor)
                    .paint(
                        painter = BitmapPainter(it),
                        alignment = Alignment.TopStart,
                        contentScale = if (colors.stetchBackgroundImage) ContentScale.FillBounds else
                            ContentScale.None
                    )
        } ?: Modifier.background(backgroundColor)
        else
            Modifier.background(backgroundColor)


    LaunchedEffect(key1 = colors) {
        colors.backgroundImageUri?.let {
            val uri = Uri.parse(colors.backgroundImageUri)
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapDrawable(context.resources, inputStream)
                if (colors.backgroundImageTiledRepeat)
                    bitmap.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                backgroundImage.value = bitmap.bitmap.asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    Scaffold(
    ) { padding ->

        Column(
            painterModifier
                .padding(padding)
                .fillMaxSize()
        ) {

            AndroidView(
                    factory={ ctx ->
                        BookReaderTextview(ctx, bookReaderViewModel).apply{
                            onSetTextview(this)
                            layoutParams =
                                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                        }
                    },
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned { layoutCoordinates -> onChangeSize(layoutCoordinates.size) }
                    )

            InfoArea(
                textSize = pageViewSettings.textSizeInfoArea,
                font = infoAreaFont,
                left = infoLeft,
                middle = infoMiddle,
                right = infoRight,
                onClickLeft = { onClickInfoLeft() },
                onClickMiddle = { onClickInfoMiddle() },
                onClickRight = { onClickInfoRight() },
                textColor = colors.colorsInfo,
                modifier = Modifier
                    .padding(
                        start = Dp(colors.marginLeft.toFloat()),
                        end = Dp(colors.marginLeft.toFloat()),
                        bottom = Dp(colors.marginBottomInfoArea.toFloat())
                    )
            )
        }

        note?.let {
            NoteViewDialog(
                note = it,
                onClose = { onCloseNote() }
            )
        }

        clickedImageBitmap?.let {
            ImageViewer(
                imageBitmap = it,
                isDarkMode = isDarkMode,
                onClose = onCloseImageView,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            )
        }

        if (showGotoDialog) {
            if (currentSection != null && currentPage != null && maxPage != null && sectionList != null) {
                    GoToDialog(
                        sectionInitial = currentSection,
                        pageInitial = currentPage,
                        maxPage = maxPage,
                        aSections = sectionList,
                        gotoSection = gotoSection,
                        gotoPage = gotoPage,
                        onClose = { onCloseGotoDialog() })
            } else {
                onCloseGotoDialog()
            }
        }

        if (showQuickMenu) {
            QuickMenuDialog(
                onFinishQuickMenuDialog = { textSize, lineSpacingMultiplier, letterSpacing, colorThemeIndex ->
                    onFinishQuickMenuDialog(
                        textSize,
                        lineSpacingMultiplier,
                        letterSpacing,
                        colorThemeIndex
                    )
                },
                onClose = {
                    onCancelQuickMenuDialog()
                },
                onAddBookmark = {
                    onAddBookmarkQuickMenuDialog()
                },
                onOpenBookmarks = {
                    onShowBookmarklistQuickMenuDialog()
                },
                onOpenBookInfo = {
                    onOpenBookInfoQuickMenuDialog()
                },
                onChangeColorThemeQuickMenuDialog = { item, pos ->
                    onChangeColorThemeQuickMenuDialog(item, pos)
                },
                onChangeLineSpacingQuickMenuDialog = { value ->
                    onChangeLineSpacingQuickMenuDialog(value)
                },
                onChangeLetterSpacingQuickMenuDialog = { value ->
                    onChangeLetterSpacingQuickMenuDialog(value)
                },
                onChangeTextSizeQuickMenuDialog = { value ->
                    onChangeTextSizeQuickMenuDialog(value)
                },
                selectedColorTheme = selectedTheme,
                textSize = pageViewSettings.textSize,
                lineSpacingMultiplier = pageViewSettings.lineSpacingMultiplier,
                letterSpacing = pageViewSettings.letterSpacing,
                selectedFont = selectedFont,
            )
        }
    }
}
