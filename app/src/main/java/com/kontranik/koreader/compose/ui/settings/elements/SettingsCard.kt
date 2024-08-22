package com.kontranik.koreader.compose.ui.settings.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.koreader.compose.theme.paddingMedium

@Composable
fun SettingsCard(
    title: String?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(
            paddingMedium
        )) {
            if (title != null) SettingsTitle(
                text = title
            )
            content()
        }
    }
}

@Preview
@Composable
private fun SettingsCardPreview() {
    SettingsCard(
        title = "Settings"
    ) {
        SettingsCheckbox(
            value = true,
            label = "Checkbox",
            onChange = {}
        )
        SettingsTextField(
            value = "Value 1",
            label = "Entry 1",
            onChange = {}
        )
    }

}
