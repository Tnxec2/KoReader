package com.kontranik.koreader.compose.ui.shared

import android.graphics.Typeface
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.kontranik.koreader.R


@Composable
fun FontSizeWidget(
    title: String,
    textSize: Float,
    onChangeTextSize: (Float) -> Unit,
    selectedFont: Typeface,
    modifier: Modifier = Modifier) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(0.3f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.7f)) {
            IconButton(onClick = { onChangeTextSize(textSize-1) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_a_small),
                    contentDescription = stringResource(
                        id = R.string.decrease
                    )
                )
            }
            Text(
                text = stringResource(id = R.string.example_abcabc),
                fontSize = TextUnit(textSize, TextUnitType.Sp),
                fontFamily = FontFamily(selectedFont),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onChangeTextSize(textSize+1) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_aa),
                    contentDescription = stringResource(
                        id = R.string.increase
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun FontSizeWidgetPreview() {
    Surface {
        FontSizeWidget(
            title = stringResource(id = R.string.textsize),
            textSize = 34f,
            onChangeTextSize = {},
            selectedFont = Typeface.SANS_SERIF)
    }
    
}