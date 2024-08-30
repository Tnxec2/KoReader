package com.kontranik.koreader.compose.ui.reader

import android.graphics.Typeface
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.quickmenu.QuickMenuDialog
import com.kontranik.koreader.compose.ui.settings.ThemeColors
import com.kontranik.koreader.compose.ui.settings.defaultColors
import com.kontranik.koreader.model.PageViewSettings


@Composable
fun BookReaderContainer(
    note: String?,
    onCloseNote: () -> Unit,
    backgroundColor: Color,
    showQuickMenu: State<Boolean>,
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
    paddingValues: PaddingValues,
    pageViewSettings: PageViewSettings,
    selectedTheme: Int,
    selectedFont: Typeface,
    onClickInfoLeft: () -> Unit,
    onClickInfoMiddle: () -> Unit,
    onClickInfoRight: () -> Unit,
    onFinishQuickMenuDialog: (textSize: Float, lineSpacingMultiplier: Float, letterSpacing: Float, colorThemeIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .background(backgroundColor)
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            textView.invoke(this).apply {
                Modifier
                    .fillMaxWidth()
                    .weight(1f) }

            InfoArea(
                left = infoLeft,
                middle = infoMiddle,
                right = infoRight,
                onClickLeft = { onClickInfoLeft() },
                onClickMiddle = { onClickInfoMiddle() },
                onClickRight = { onClickInfoRight() },
                textColor = colors.colorsInfo,
            )
        }

        note?.let {
            NoteViewDialog(
                note = it,
                colorBackground = colors.colorBackground,
                colorText = colors.colorsText,
                pageViewSettings = pageViewSettings,
                selectedFont = selectedFont,
                onClose = { onCloseNote() }
            )
        }

        if (showQuickMenu.value) {
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
            paddingValues = PaddingValues(paddingSmall),
            onClickInfoLeft = {},
            onClickInfoMiddle = {},
            onClickInfoRight = {},
            infoLeft = "left",
            infoMiddle = "center",
            infoRight = "right",
            colors =     ThemeColors(
                Color(0xFFFBF0D9),
                Color(0xFF5F4B32),
                Color(0xFF2196F3),
                Color(0xFF5F4B32),
                showBackgroundImage = false,
                backgroundImageTiledRepeat = false,
                backgroundImageUri = null,
                ),
            showQuickMenu = remember {
                mutableStateOf(true)
            },
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
            note = "",
            onCloseNote = {}
        )
    }
}