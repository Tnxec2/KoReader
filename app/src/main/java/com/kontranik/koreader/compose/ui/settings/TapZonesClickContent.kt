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
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.settings.elements.SettingsCard
import com.kontranik.koreader.compose.ui.settings.elements.SettingsList
import com.kontranik.koreader.compose.ui.settings.elements.SettingsTitle
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLight


data class TapItem(
    val title: Int,
    val defaultValue: String,
    val defaultValueTitle: String? = null,
    val onChange: (String) -> Unit,
    val enabled: Boolean = true
)

@Composable
fun TapZonesClickSettingsContent(
    title: String,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    topLeft: String,
    topLeftTitle: String,
    onChangeTopLeft: (String) -> Unit,
    topCenter: String,
    topCenterTitle: String,
    onChangeTopCenter: (String) -> Unit,
    topRight: String,
    topRightTitle: String,
    onChangeTopRight: (String) -> Unit,
    middleLeft: String,
    middleLeftTitle: String,
    onChangeMiddleLeft: (String) -> Unit,
    middleCenter: String,
    middleCenterEnabled: Boolean,
    middleCenterTitle: String,
    onChangeMiddleCenter: (String) -> Unit,
    middleRight: String,
    middleRightTitle: String,
    onChangeMiddleRight: (String) -> Unit,
    bottomLeft: String,
    bottomLeftTitle: String,
    onChangeBottomLeft: (String) -> Unit,
    bottomCenter: String,
    bottomCenterTitle: String,
    onChangeBottomCenter: (String) -> Unit,
    bottomRight: String,
    bottomRightTitle: String,
    onChangeBottomRight: (String) -> Unit,
) {

    val items = listOf(
        TapItem(title = R.string.tapzone_top_left, defaultValue = topLeft, defaultValueTitle = topLeftTitle, onChange = onChangeTopLeft),
        TapItem(title = R.string.tapzone_top_center, defaultValue = topCenter, defaultValueTitle = topCenterTitle, onChange = onChangeTopCenter),
        TapItem(title = R.string.tapzone_top_right, defaultValue = topRight, defaultValueTitle = topRightTitle, onChange = onChangeTopRight),
        TapItem(title = R.string.tapzone_middle_left, defaultValue = middleLeft, defaultValueTitle = middleLeftTitle, onChange = onChangeMiddleLeft),
        TapItem(title = R.string.tapzone_middle_center, defaultValue = middleCenter, defaultValueTitle = middleCenterTitle, onChange = onChangeMiddleCenter, enabled = middleCenterEnabled),
        TapItem(title = R.string.tapzone_middle_right, defaultValue = middleRight, defaultValueTitle = middleRightTitle, onChange = onChangeMiddleRight),
        TapItem(title = R.string.tapzone_bottom_left, defaultValue = bottomLeft, defaultValueTitle = bottomLeftTitle, onChange = onChangeBottomLeft),
        TapItem(title = R.string.tapzone_bottom_center, defaultValue = bottomCenter, defaultValueTitle = bottomCenterTitle, onChange = onChangeBottomCenter),
        TapItem(title = R.string.tapzone_bottom_right, defaultValue = bottomRight, defaultValueTitle = bottomRightTitle, onChange = onChangeBottomRight),
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

            LazyColumn(
                Modifier
            ) {
                item {
                    SettingsTitle(
                        text = title,
                        modifier = Modifier.padding(bottom = paddingSmall)
                    )
                }

                item {
                    SettingsCard(
                        modifier = Modifier.padding(bottom = paddingMedium)
                    ) {
                        Column(
                            Modifier.fillMaxWidth()
                        ) {
                            items.map { item ->
                                SettingsList(
                                    title = stringResource(id = item.title),
                                    entries = getStringArrayFromResourceArray(res = tapzonen_entries),
                                    entryValues = tapzonen_values.toList(),
                                    defaultValue = item.defaultValue,
                                    defaultValueTitle = item.defaultValueTitle,
                                    onChange = item.onChange,
                                    showDefaultValue = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = item.enabled,
                                )
                            }
                        }
                    }
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
                topLeftTitle = "action title",
                onChangeTopLeft = {},
                topCenter = "action",
                topCenterTitle = "action title",
                onChangeTopCenter = {},
                topRight = "action",
                topRightTitle = "action",
                onChangeTopRight = {},
                middleLeft = "action",
                middleLeftTitle = "action",
                onChangeMiddleLeft = {},
                middleCenter = "action",
                middleCenterEnabled = true,
                middleCenterTitle = "action",
                onChangeMiddleCenter = {},
                middleRight = "action",
                middleRightTitle = "action",
                onChangeMiddleRight = {},
                bottomLeft = "action",
                bottomLeftTitle = "action",
                onChangeBottomLeft = {},
                bottomCenter = "action",
                bottomCenterTitle = "action",
                onChangeBottomCenter = {},
                bottomRight = "action",
                bottomRightTitle = "action",
                onChangeBottomRight = {},
            )
        }
    }
}