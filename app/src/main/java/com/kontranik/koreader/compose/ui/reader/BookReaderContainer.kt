package com.kontranik.koreader.compose.ui.reader

import android.graphics.Bitmap
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.quickmenu.QuickMenuDialog
import com.kontranik.koreader.compose.ui.settings.ThemeColors
import com.kontranik.koreader.compose.ui.settings.defaultColors
import com.kontranik.koreader.model.PageViewSettings
import com.kontranik.koreader.utils.ImageUtils
import java.io.InputStream

inline fun <T : Any> Modifier.ifNotNull(value: T?, builder: (T) -> Modifier): Modifier =
    then(if (value != null) builder(value) else Modifier)

@Composable
fun BookReaderContainer(
    note: String?,
    onCloseNote: () -> Unit,
    backgroundColor: Color,

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
    onCancelQuickMenuDialog: () -> Unit,
    onAddBookmarkQuickMenuDialog: () -> Unit,
    onShowBookmarklistQuickMenuDialog: () -> Unit,
    onOpenBookInfoQuickMenuDialog: () -> Unit,
    onChangeColorThemeQuickMenuDialog: (colorTheme: String, colorThemeIndex: Int) -> Unit,
    onChangeTextSizeQuickMenuDialog: (textSize: Float) -> Unit,
    onChangeLineSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,
    onChangeLetterSpacingQuickMenuDialog: (lineSpacingMultiplier: Float) -> Unit,

    textView: @Composable ColumnScope.() -> Unit,
    infoLeft: String,
    infoMiddle: String,
    infoRight: String,
    colors: ThemeColors,
    pageViewSettings: PageViewSettings,
    selectedTheme: Int,
    selectedFont: Typeface,

    onClickInfoLeft: () -> Unit,
    onClickInfoMiddle: () -> Unit,
    onClickInfoRight: () -> Unit,
    onFinishQuickMenuDialog: (textSize: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorThemeIndex: Int) -> Unit,
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
                imageBrush?.let { brush -> Modifier.background(backgroundColor).background(brush) }
            else
                Modifier.background(backgroundColor).paint(
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
            textView.invoke(this)

            InfoArea(
                left = infoLeft,
                middle = infoMiddle,
                right = infoRight,
                onClickLeft = { onClickInfoLeft() },
                onClickMiddle = { onClickInfoMiddle() },
                onClickRight = { onClickInfoRight() },
                textColor = colors.colorsInfo,
                modifier = Modifier.padding(horizontal = paddingSmall)
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
                pageViewSettings = pageViewSettings,
                selectedFont = selectedFont,
                selectedColorTheme = selectedTheme
            )
        }
    }

        
}

@Preview
@Composable
private fun BookReaderContainerPreview() {

    AppTheme {
        BookReaderContainer(
            textView =  {  AndroidView(
                factory = {
                    ctx -> TextView(ctx).apply {
                        text = "book content"
                    }
                },
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )},
            onClickInfoLeft = {},
            onClickInfoMiddle = {},
            onClickInfoRight = {},
            infoLeft = "left",
            infoMiddle = "center",
            infoRight = "right",
            colors = defaultColors.first(),
            showQuickMenu = false,
            showGotoDialog = false,
            onChangeTextSizeQuickMenuDialog = { _ -> },
            onChangeLineSpacingQuickMenuDialog = { _ ->},
            onChangeColorThemeQuickMenuDialog = { _, _ ->},
            onChangeLetterSpacingQuickMenuDialog = { _ ->},
            onCancelQuickMenuDialog = {},
            onFinishQuickMenuDialog = {_,_,_,_ ->},
            onOpenBookInfoQuickMenuDialog = { ->},
            onShowBookmarklistQuickMenuDialog = {},
            onAddBookmarkQuickMenuDialog = {},
            pageViewSettings = PageViewSettings(),
            selectedFont = Typeface.DEFAULT,
            selectedTheme = 0,
            backgroundColor = defaultColors.first().colorBackground,
            note = null,
            onCloseNote = {},
            onCloseGotoDialog = {},
            gotoPage = {},
            gotoSection = {},
            sectionList = listOf(),
            maxPage = 10,
            currentPage = 0,
            currentSection = 1,
            clickedImageBitmap = null,
            isDarkMode = false,
            onCloseImageView = {},
        )
    }
}