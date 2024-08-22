package com.kontranik.koreader.compose.ui.settings.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsCheckbox(
    value: Boolean,
    label: String,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = {
                    onChange(value.not())
                }
            )
            .fillMaxWidth()) {

        Text(text = label, modifier = Modifier.weight(1f))
        Checkbox(
            checked = value,
            onCheckedChange = {
                onChange(value.not())
            })
    }
}

@Preview
@Composable
private fun SettingsCheckboxPreview() {
    SettingsCheckbox(
        value = true,
        label = "Checkbox",
        onChange = {}
    )
}