package com.kontranik.koreader.compose.ui.reader

import android.graphics.Typeface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall

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
            .height(IntrinsicSize.Min),
    ) {
        Text(
            text = left,
            color = textColor,
            fontSize = textSize.sp,
            fontFamily = FontFamily(font),
            maxLines = 1,
            modifier = Modifier
                .clickable {
                    onClickLeft()
                }
        )
        if (middle.isNotEmpty()) {
        Spacer(modifier = Modifier.weight(1f))
        VerticalDivider(
            color = textColor,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

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
        )}
        Spacer(modifier = Modifier.weight(1f))
        VerticalDivider(
                color = textColor,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = right,
            color = textColor,
            fontSize = textSize.sp,
            fontFamily = FontFamily(font),
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier
                .padding(start = paddingSmall)
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
                left = "long long long long very long left",
                middle = "long long middle",
                right = "long right",
                onClickLeft = {},
                onClickMiddle = {},
                onClickRight = {},
                textColor = Color.DarkGray
            )
        }
    }
}


@Preview
@Composable
private fun InfoAreaPreview2() {
    AppTheme {
        Surface {
            InfoArea(
                textSize = 10f,
                font = Typeface.SERIF,
                left = "no book",
                middle = "",
                right = "long right",
                onClickLeft = {},
                onClickMiddle = {},
                onClickRight = {},
                textColor = Color.DarkGray
            )
        }
    }
}

