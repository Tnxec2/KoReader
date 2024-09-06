package com.kontranik.koreader.compose.ui.settings.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingSmall


@Composable
fun SettingsButton(
    title: String,
    defaultValue: String? = null,
    @DrawableRes icon: Int? = null,
    onClick: () -> Unit,
    showDefaultValue: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
    ) {
        icon?.let{Icon(painter = painterResource(id = it), contentDescription = title,
            modifier = Modifier.padding(end = paddingSmall))}

        Column(Modifier
            .padding(vertical = paddingSmall)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
            if (showDefaultValue) defaultValue?.let {Text(
                text = it,
                modifier = Modifier
            )}
        }
    }
}



@Preview
@Composable
private fun SettingsButtonPreview() {
    SettingsButton(
        title = "Title",
        defaultValue = "entry1",
        icon = R.drawable.ic_iconmonstr_paintbrush_10,
        showDefaultValue = true,
        onClick = {},
    )
}