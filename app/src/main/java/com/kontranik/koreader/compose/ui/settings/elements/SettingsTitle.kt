package com.kontranik.koreader.compose.ui.settings.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsTitle(
    text: String,
    modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun SettingsTitlePreview() {
    SettingsTitle(
        text = "Title",
    )
}