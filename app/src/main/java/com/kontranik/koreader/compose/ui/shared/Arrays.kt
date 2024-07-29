package com.kontranik.koreader.compose.ui.shared

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R


fun getThemes(context: Context): List<String> {
    return listOf(
        context.getString(R.string.color_theme1_header),
        context.getString(R.string.color_theme2_header),
        context.getString(R.string.color_theme3_header),
        context.getString(R.string.color_theme4_header),
        context.getString(R.string.color_theme5_header),
    )
}

fun getLineSpacings(): List<Float> {
    return listOf(1f,
    1.15f,
    1.5f,
    2.0f,
    2.5f,
    3.0f,
    )
}
fun getLetterSpacing(): List<Float> {
    return listOf(
        0f,
    0.01f,
    0.05f,
    0.1f,
    0.15f,
    0.2f,
    0.25f,
    0.3f,
    )
}