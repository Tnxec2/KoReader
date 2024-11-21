package com.kontranik.koreader.compose.ui.settings.elements.fontpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.TitledDialog
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord


@Composable
fun FontPickerDialogContent(
    typefaceRecord: TypefaceRecord,
    fontlist: MutableState<List<TypefaceRecord>>,
    onDismissRequest: () -> Unit,
    onConfirmation: (TypefaceRecord) -> Unit,
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(key1 = fontlist) {
        scrollState.scrollToItem(
            fontlist.value.indexOfFirst { item -> item.name == typefaceRecord.name }
        )
    }

    TitledDialog(
        title = stringResource(id = R.string.font_select_title),
        onClose = { onDismissRequest() }
    ) {

        LazyColumn(
            Modifier.weight(1f)
        ) {
            itemsIndexed(fontlist.value) { _, item ->
                Text(
                    text = item.name,
                    fontFamily = FontFamily(item.getTypeface()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                        .clickable {
                            onConfirmation(item)
                        }
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun FontPickerDialogPreview() {
    AppTheme {
        Surface {
            FontPickerDialogContent(
                typefaceRecord = TypefaceRecord.DEFAULT,
                fontlist = remember {
                    mutableStateOf(
                        listOf(
                            TypefaceRecord(name = TypefaceRecord.SANSSERIF),
                            TypefaceRecord(name = TypefaceRecord.SERIF),
                            TypefaceRecord(name = TypefaceRecord.MONO),
                        )
                    )
                },
                onDismissRequest = {},
                onConfirmation = {})
        }
    }
}

