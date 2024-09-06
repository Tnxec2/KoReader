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
fun SettingsList(
    entries: List<String>,
    entryValues: List<String>,
    defaultValue: String,
    defaultValueTitle: String? = null,
    @DrawableRes icon: Int? = null,
    onChange: (String) -> Unit,
    showDefaultValue: Boolean,
    modifier: Modifier = Modifier,
    show: Boolean = false,
    enabled: Boolean = true,
    title: String? = null,
) {
    var showDropdown by rememberSaveable { mutableStateOf(show) }
    val scrollState = rememberLazyListState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(paddingSmall)
            .clickable { if (enabled) showDropdown = !showDropdown; },
    ) {
        icon?.let {
            Icon(painter = painterResource(id = it), contentDescription = title,
                modifier = Modifier.padding(end = paddingSmall))
        }

        Column {
            if (title != null) Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
            if (showDefaultValue) Text(
                text = defaultValueTitle ?: defaultValue,
                modifier = Modifier
            )
        }

        Box {
            if (showDropdown) {
                SettingsListContent(
                    entries = entries,
                    defaultPos = entryValues.indexOf(defaultValue),
                    onItemClick = { pos, _ ->
                        if (enabled) onChange(entryValues[pos])
                        showDropdown = false
                    },
                    onClose = { showDropdown = false; },
                    scrollState = scrollState,
                )
            }
        }
    }
}


@Composable
fun SettingsListContent(
    entries: List<String>,
    defaultPos: Int,
    onItemClick: (pos: Int, value: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            if (defaultPos >= 0) scrollState.scrollToItem(defaultPos)
        }
    }

    Popup(
        alignment = Alignment.TopCenter,
        properties = PopupProperties(
            excludeFromSystemGesture = true,
        ),
        onDismissRequest = { onClose() }
    ) {
        LazyColumn(
            state = scrollState,
            modifier = modifier
                .heightIn(max = 300.dp)
                .padding(paddingSmall)
                .border(width = 1.dp, color = Color.Gray)
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(
                entries,
                key = { _, item -> item}
            ) { index, item ->
                if (index != 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
                }
                Box(
                    modifier = Modifier
                        .clickable {
                            onItemClick(index, item)
                            onClose()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(paddingSmall)
                    )
                }
            }

        }
    }
}

@Preview
@Composable
private fun SettingsTextFieldPreview() {

    Column {
        SettingsList(
            title = "Title",
            entries = listOf(
                "entry 1",
                "entry 2",
                "entry 3",
            ),
            entryValues = listOf(
                "entry1",
                "entry2",
                "entry3",
            ),
            defaultValue = "entry1",
            showDefaultValue = true,
            onChange = {},
            show = false,
        )
    SettingsList(
        title = "Title",
        entries = listOf(
            "entry 1",
            "entry 2",
            "entry 3",
        ),
        entryValues = listOf(
            "entry1",
            "entry2",
            "entry3",
        ),
        defaultValue = "entry1",
        defaultValueTitle = "entry_title",
        icon = R.drawable.ic_iconmonstr_paintbrush_10,
        showDefaultValue = true,
        onChange = {},
        show = false,
    )
    }
}