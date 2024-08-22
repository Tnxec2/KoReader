package com.kontranik.koreader.compose.ui.settings

import android.graphics.Typeface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord

@Composable
fun SettingsFontPicker(
    title: String,
    typefaceRecord: TypefaceRecord,
    showSystemFonts: Boolean,
    shoNotoFonts: Boolean,
    onChange: (TypefaceRecord) -> Unit,
    modifier: Modifier = Modifier,
    style: Int = Typeface.NORMAL,
) {
    var showPickerDialog by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingSmall)
            .clickable { showPickerDialog = true }
    ) {
        Text(text = title)
        Text(
            text = typefaceRecord.name,
            textAlign = TextAlign.End,
            fontFamily = FontFamily(typefaceRecord.getTypeface(style)),
            modifier = Modifier.weight(1f)
                .padding(start = paddingSmall)

        )

        if (showPickerDialog)
            FontPickerDialog(
                typefaceRecord = typefaceRecord,
                showSystemFonts = showSystemFonts,
                showNotoFonts = shoNotoFonts,
                onDismissRequest = { showPickerDialog = false },
                onConfirmation = {
                    onChange(it)
                    showPickerDialog = false
                }
            )
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        SettingsFontPicker(
            title = stringResource(id = R.string.text_normal),
            style = Typeface.NORMAL,
            typefaceRecord = TypefaceRecord.DEFAULT,
            showSystemFonts = false,
            shoNotoFonts = false,
            onChange = {}
        )
    }
}