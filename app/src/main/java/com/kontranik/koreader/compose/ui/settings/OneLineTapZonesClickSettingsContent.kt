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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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



@Composable
fun OneLineTapZonesClickSettingsContent(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    title: String,
    left: String,
    leftTitle: String,
    onChangeLeft: (String) -> Unit,
    center: String,
    centerTitle: String,
    onChangeCenter: (String) -> Unit,
    right: String,
    rightTitle: String,
    onChangeRight: (String) -> Unit,
) {
    val items = listOf(
            TapItem(
                title = R.string.tapzone_left,
                defaultValue = left,
                defaultValueTitle = leftTitle,
                onChange = onChangeLeft
            ),
            TapItem(
                title = R.string.tapzone_center,
                defaultValue = center,
                defaultValueTitle = centerTitle,
                onChange = onChangeCenter
            ),
            TapItem(
                title = R.string.tapzone_right,
                defaultValue = right,
                defaultValueTitle = rightTitle,
                onChange = onChangeRight
            ),
    )

    Scaffold(
        topBar = {
            AppBar(
                title = R.string.settings,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier
                .padding(padding)
                .padding(paddingSmall)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {


            SettingsTitle(
                text = title,
                modifier = Modifier.padding(bottom = paddingSmall)
            )

            SettingsCard(
                modifier = Modifier.padding(bottom = paddingMedium)
            ) {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .heightIn(min = 100.dp)
                        ) {
                            items.mapIndexed { index, item ->
                                SettingsList(
                                    //title = stringResource(id = item.title),
                                    entries = getStringArrayFromResourceArray(res = tapzonen_entries),
                                    entryValues = tapzonen_values.toList(),
                                    defaultValue = item.defaultValue,
                                    defaultValueTitle = item.defaultValueTitle,
                                    onChange = item.onChange,
                                    showDefaultValue = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically),
                                    enabled = item.enabled,
                                )
                                if (index < items.size - 1) VerticalDivider(
                                    color = Color.Black,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp)
                                )
                            }
                        }

                }
            }
        }
    }

}


@PreviewPortraitLight
@Composable
private fun OneLineTapZonesClickSettingsContentPreview() {
    AppTheme {
        Surface {
            OneLineTapZonesClickSettingsContent(
                title = "One Click",
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                left = "action",
                leftTitle = "action title long long long",
                onChangeLeft = {},
                center = "action",
                centerTitle = "action title",
                onChangeCenter = {},
                right = "action",
                rightTitle = "action",
                onChangeRight = {},
            )
        }
    }
}