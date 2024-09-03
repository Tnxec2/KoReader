package com.kontranik.koreader.compose.ui.settings.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingSmall
import kotlinx.coroutines.launch


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