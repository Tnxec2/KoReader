package com.kontranik.koreader.compose.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.settings.elements.SettingsList
import com.kontranik.koreader.compose.ui.settings.elements.SettingsTitle
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLight


data class TapItem(
    val title: Int,
    val defaultValue: String,
    val onChange: (String) -> Unit
)

@Composable
fun TapZonesClickSettingsContent(
    title: String,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    topLeft: String,
    onChangeTopLeft: (String) -> Unit,
    topCenter: String,
    onChangeTopCenter: (String) -> Unit,
    topRight: String,
    onChangeTopRight: (String) -> Unit,
    middleLeft: String,
    onChangeMiddleLeft: (String) -> Unit,
    middleCenter: String,
    onChangeMiddleCenter: (String) -> Unit,
    middleRight: String,
    onChangeMiddleRight: (String) -> Unit,
    bottomLeft: String,
    onChangeBottomLeft: (String) -> Unit,
    bottomCenter: String,
    onChangeBottomCenter: (String) -> Unit,
    bottomRight: String,
    onChangeBottomRight: (String) -> Unit,
) {

    val items = listOf(
        TapItem(R.string.tapzone_top_left, topLeft, onChangeTopLeft),
        TapItem(R.string.tapzone_top_center, topCenter, onChangeTopCenter),
        TapItem(R.string.tapzone_top_right, topRight, onChangeTopRight),
        TapItem(R.string.tapzone_middle_left, middleLeft, onChangeMiddleLeft),
        TapItem(R.string.tapzone_middle_center, middleCenter, onChangeMiddleCenter),
        TapItem(R.string.tapzone_middle_right, middleRight, onChangeMiddleRight),
        TapItem(R.string.tapzone_bottom_left, bottomLeft, onChangeBottomLeft),
        TapItem(R.string.tapzone_bottom_center, bottomCenter, onChangeBottomCenter),
        TapItem(R.string.tapzone_bottom_right, bottomRight, onChangeBottomRight),
    )

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.settings,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = {  navigateBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },) }
    ) { padding ->
        Column(
            modifier
                .padding(padding)
                .padding(paddingSmall)
                .fillMaxSize()
        ) {

            SettingsTitle(text = title)

            LazyColumn(
                Modifier.fillMaxWidth()
            ) {
                items(items) { item ->
                    SettingsList(
                        title = stringResource(id = item.title),
                        entries = stringArrayResource(id = R.array.tapzonen_entries).toList(),
                        entryValues = stringArrayResource(id = R.array.tapzonen_values).toList(),
                        defaultValue = item.defaultValue,
                        onChange = item.onChange,
                        showDefaultValue = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@PreviewPortraitLight
@Composable
private fun TapZonesOneClickSettingsContentPreview() {
    AppTheme {
        Surface {
            TapZonesClickSettingsContent(
                title = "One Click",
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                topLeft = "action",
                onChangeTopLeft = {},
                topCenter = "action",
                onChangeTopCenter = {},
                topRight = "action",
                onChangeTopRight = {},
                middleLeft = "action",
                onChangeMiddleLeft = {},
                middleCenter = "action",
                onChangeMiddleCenter = {},
                middleRight = "action",
                onChangeMiddleRight = {},
                bottomLeft = "action",
                onChangeBottomLeft = {},
                bottomCenter = "action",
                onChangeBottomCenter = {},
                bottomRight = "action",
                onChangeBottomRight = {},
            )
        }
    }
}