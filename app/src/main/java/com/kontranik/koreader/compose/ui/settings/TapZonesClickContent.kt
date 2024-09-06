package com.kontranik.koreader.compose.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    val itemsNew = listOf(
    listOf(
        TapItem(title = R.string.tapzone_top_left, defaultValue = topLeft, defaultValueTitle = topLeftTitle, onChange = onChangeTopLeft),
        TapItem(title = R.string.tapzone_top_center, defaultValue = topCenter, defaultValueTitle = topCenterTitle, onChange = onChangeTopCenter),
        TapItem(title = R.string.tapzone_top_right, defaultValue = topRight, defaultValueTitle = topRightTitle, onChange = onChangeTopRight),
    ),

    listOf(
        TapItem(title = R.string.tapzone_middle_left, defaultValue = middleLeft, defaultValueTitle = middleLeftTitle, onChange = onChangeMiddleLeft),
        TapItem(title = R.string.tapzone_middle_center, defaultValue = middleCenter, defaultValueTitle = middleCenterTitle, onChange = onChangeMiddleCenter, enabled = middleCenterEnabled),
        TapItem(title = R.string.tapzone_middle_right, defaultValue = middleRight, defaultValueTitle = middleRightTitle, onChange = onChangeMiddleRight),
    ),

    listOf(
        TapItem(title = R.string.tapzone_bottom_left, defaultValue = bottomLeft, defaultValueTitle = bottomLeftTitle, onChange = onChangeBottomLeft),
        TapItem(title = R.string.tapzone_bottom_center, defaultValue = bottomCenter, defaultValueTitle = bottomCenterTitle, onChange = onChangeBottomCenter),
        TapItem(title = R.string.tapzone_bottom_right, defaultValue = bottomRight, defaultValueTitle = bottomRightTitle, onChange = onChangeBottomRight),
    )
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
                        itemsNew.mapIndexed { indexRow, row ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .heightIn(min = 100.dp)
                            ) {
                                row.mapIndexed { index, item ->
                                        SettingsList(
                                            //title = stringResource(id = item.title),
                                            entries = getStringArrayFromResourceArray(res = tapzonen_entries),
                                            entryValues = tapzonen_values.toList(),
                                            defaultValue = item.defaultValue,
                                            defaultValueTitle = item.defaultValueTitle,
                                            onChange = item.onChange,
                                            showDefaultValue = true,
                                            modifier = Modifier
                                                .weight(1f).align(Alignment.CenterVertically),
                                            enabled = item.enabled,
                                        )
                                        if (index < row.size-1) VerticalDivider(
                                            color = Color.Black,
                                            modifier = Modifier.fillMaxHeight().width(1.dp)
                                        )
                                    }
                                }
                                if (indexRow < itemsNew.size-1) HorizontalDivider(
                                    color = Color.Black,
                                    modifier = Modifier.height(1.dp)
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
                topLeftTitle = "action title long long long",
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
                middleCenterTitle = "action long long long",
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
                bottomRightTitle = "action long long long",
                onChangeBottomRight = {},
            )
        }
    }
}