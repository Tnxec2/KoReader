package com.kontranik.koreader.compose.ui.reader

import android.graphics.Typeface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kontranik.koreader.compose.theme.AppTheme

@Composable
fun InfoArea(
    textSize: Float,
    font: Typeface,
    left: String,
    middle: String,
    right: String,
    onClickLeft: () -> Unit,
    onClickMiddle: () -> Unit,
    onClickRight: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier) {

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = left,
            color = textColor,
            fontSize = textSize.sp,
            fontFamily = FontFamily(font),
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onClickLeft()
                }
        )
        Text(
            text = middle,
            color = textColor,
            fontSize = textSize.sp,
            fontFamily = FontFamily(font),
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .clickable {
                    onClickMiddle()
                }
        )
        Text(
            text = right,
            color = textColor,
            fontSize = textSize.sp,
            fontFamily = FontFamily(font),
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onClickRight()
                }
        )
    }
}

@Preview
@Composable
private fun InfoAreaPreview() {
    AppTheme {
        Surface {
            InfoArea(
                textSize = 10f,
                font = Typeface.SERIF,
                left = "left",
                middle = "middle",
                right = "right",
                onClickLeft = {},
                onClickMiddle = {},
                onClickRight = {},
                textColor = Color.DarkGray
            )
        }
    }
    
}

