package com.kontranik.koreader.compose.ui.settings.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsTextField(
    value: String,
    label: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = TextFieldValue(
                value,
                selection = TextRange(value.length)
            ),
            label = { Text(label) },
            onValueChange = {
                onChange(it.text)
            },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Preview
@Composable
private fun SettingsTextFieldPreview() {
    SettingsTextField(
        value = "Value 1",
        label = "Entry 1",
        onChange = {}
    )
}