package com.kontranik.koreader.compose.ui.settings.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.kontranik.koreader.compose.theme.paddingSmall
import kotlinx.coroutines.launch


@Composable
fun SettingsList(
    entries: List<String>,
    entryValues: List<String>,
    defaultValue: String,
    modifier: Modifier = Modifier,
    defaultValueTitle: String? = null,
    @DrawableRes icon: Int? = null,
    onChange: (String) -> Unit,
    showDefaultValue: Boolean,
    show: Boolean = false,
    enabled: Boolean = true,
    title: String? = null,
) {
    var showDropdown by rememberSaveable { mutableStateOf(show) }
    val scrollState = rememberScrollState()

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
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                if (title != null) Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showDefaultValue) Text(
                    text = defaultValueTitle ?: defaultValue,
                    modifier = Modifier
                )
            }

            SettingsListContent(
                expanded = showDropdown,
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


@Composable
fun SettingsListContent(
    expanded: Boolean = true,
    entries: List<String>,
    defaultPos: Int,
    onItemClick: (pos: Int, value: String) -> Unit,
    onClose: () -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            if (defaultPos >= 0) scrollState.scrollTo(defaultPos)
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onClose,
        scrollState = scrollState,
    ) {
        entries.forEachIndexed { index, item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = item,
                        color = if (index == defaultPos) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()

                    )
                },
                onClick = {
                    onItemClick(index, item)
                    onClose()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = if (index == defaultPos) MaterialTheme.colorScheme.secondary else Color.Transparent)
            )
        }
    }
}

@Preview
@Composable
private fun SettingsTextFieldPreview() {

    Surface(Modifier.width(300.dp).height(500.dp)) {

        Column(Modifier.padding(10.dp).fillMaxSize()) {
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
                defaultValue = "entry2",
                defaultValueTitle = "entry2",
                showDefaultValue = true,
                onChange = {},
                show = true,
            )
        }
    }
}