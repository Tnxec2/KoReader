package com.kontranik.koreader.compose.ui.quickmenu

import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.ui.shared.DropdownList
import com.kontranik.koreader.compose.ui.shared.DropdownListThemed
import com.kontranik.koreader.compose.ui.shared.getLetterSpacing
import com.kontranik.koreader.compose.ui.shared.getLineSpacings
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.FontSizeWidget


@Composable
fun QuickMenuDialogContent(
    themes: List<String>,
    colorThemePosition: Int,
    onChangeTheme: (Int, String) -> Unit,
    textSize: Float,
    onChangeTextSize: (Float) -> Unit,
    selectedFont: Typeface,
    textSizeInfoArea: Float,
    onChangeTextSizeInfoArea: (Float) -> Unit,
    selectedFontInfoArea: Typeface,
    lineSpacingMultiplier: Float,
    itemsLineSpacing: List<String>,
    itemsLetterSpacing: List<String>,
    onChangeLineSpacing: (Float) -> Unit,
    onChangeLetterSpacing: (Float) -> Unit,
    letterSpacing: Float,
    onAddBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenBookInfo: () -> Unit,
    saveQuickSettings: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

        Column(
            modifier
                .padding(horizontal = paddingMedium)
                .padding(top = paddingSmall)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.color_theme),
                    modifier = Modifier.weight(0.3f)
                )
                Row(Modifier.weight(0.7f)) {
                    DropdownListThemed(
                        themesList = themes,
                        selectedTheme = themes[colorThemePosition],
                        selectedThemeIndex = colorThemePosition,
                        onItemClick = { pos, item -> onChangeTheme(pos, item) },
                        textSize = TextUnit(textSize, TextUnitType.Sp),
                        typeface = selectedFont,
                    )
                }
            }
            FontSizeWidget(
                title = stringResource(id = R.string.textsize),
                textSize = textSize,
                onChangeTextSize = onChangeTextSize,
                selectedFont = selectedFont,
                modifier = Modifier.fillMaxWidth()
            )
            FontSizeWidget(
                title = stringResource(id = R.string.textsize_infoarea),
                textSize = textSizeInfoArea,
                onChangeTextSize = onChangeTextSizeInfoArea,
                selectedFont = selectedFontInfoArea,
                modifier = Modifier.fillMaxWidth()
            )
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.line_spacing),
                    modifier = Modifier.weight(0.5f)
                )
                Row(
                    modifier = Modifier.weight(0.5f)) {
                    DropdownList(
                        itemList = itemsLineSpacing,
                        selectedItem = lineSpacingMultiplier.toString(),
                        onItemClick = { pos, item -> onChangeLineSpacing(item.toFloat()) },
                    )
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.letter_spacing),
                    modifier = Modifier.weight(0.5f)
                )
                Row(
                    modifier = Modifier.weight(0.5f)) {
                    DropdownList(
                        itemList = itemsLetterSpacing,
                        selectedItem = letterSpacing.toString(),
                        onItemClick = { pos, item -> onChangeLetterSpacing(item.toFloat()) },
                    )
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.bookmarks), modifier = Modifier.weight(1f))
                IconButton(onClick = { onAddBookmark() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_iconmonstr_bookmark_add),
                        contentDescription = stringResource(
                            id = R.string.add_bookmark
                        )
                    )
                }
                IconButton(onClick = { onOpenBookmarks() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_iconmonstr_bookmark_list2),
                        contentDescription = stringResource(
                            id = R.string.bookmarklist
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(paddingSmall))
            Row(Modifier.fillMaxWidth()) {
                IconButton(onClick = { onOpenBookInfo() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_iconmonstr_book_info),
                        contentDescription = stringResource(
                            id = R.string.bookinfo
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { saveQuickSettings() },
                    Modifier.padding(end = paddingSmall)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_check_24),
                        contentDescription = stringResource(
                            id = R.string.save
                        )
                    )
                }
                IconButton(onClick = { onClose() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_close_24),
                        contentDescription = stringResource(
                            id = R.string.close
                        )
                    )
                }
            }
        }


}

@Preview
@Composable
private fun QuickMenuContentPreview() {
    val themes by remember {
        mutableStateOf(
            listOf(
                "theme1",
                "theme2",
                "theme3",
                "theme4",
                "theme5"
            )
        )
    }
    var colorTheme by remember { mutableStateOf(themes[0]) }

    var textSize by remember { mutableFloatStateOf(34f) }
    var textSizeInfoArea by remember { mutableFloatStateOf(14f) }

    val itemsLineSpacing by remember { mutableStateOf(getLineSpacings().map { it.toString() }) }
    var lineSpacingMultiplier by remember { mutableFloatStateOf(itemsLineSpacing[0].toFloat()) }

    val itemsLetterSpacing by remember { mutableStateOf(getLetterSpacing().map { it.toString() }) }
    var letterSpacing by remember { mutableFloatStateOf(itemsLetterSpacing[0].toFloat()) }

    var selectedFont by remember { mutableStateOf(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)) }

    AppTheme {
        Surface(onClick = {  }) {
            QuickMenuDialogContent(
                themes = themes,
                colorThemePosition = 0,
                onChangeTheme = { pos, item -> colorTheme = item },
                textSize = textSize,
                onChangeTextSize = { textSize = it },
                selectedFont = selectedFont,
                textSizeInfoArea = textSizeInfoArea,
                onChangeTextSizeInfoArea = { },
                selectedFontInfoArea = selectedFont,
                itemsLineSpacing = itemsLineSpacing,
                itemsLetterSpacing = itemsLetterSpacing,
                lineSpacingMultiplier = lineSpacingMultiplier,
                onChangeLineSpacing = { lineSpacingMultiplier = it },
                letterSpacing = letterSpacing,
                onChangeLetterSpacing = { letterSpacing = it },
                onAddBookmark = {},
                onOpenBookmarks = {},
                onClose = {},
                onOpenBookInfo = {},
                saveQuickSettings = {}
            )
        }
    }
}
