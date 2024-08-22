package com.kontranik.koreader.compose.ui.settings

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.appbar.AppBar
import com.kontranik.koreader.compose.ui.settings.elements.SettingsButton
import com.kontranik.koreader.compose.ui.settings.elements.SettingsCard
import com.kontranik.koreader.compose.ui.settings.elements.SettingsCheckbox
import com.kontranik.koreader.compose.ui.settings.elements.SettingsColor
import com.kontranik.koreader.compose.ui.settings.elements.SettingsTextField
import com.kontranik.koreader.compose.ui.settings.elements.SettingsTitle
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLight
import kotlinx.coroutines.launch
import java.lang.Exception


fun readFileName(backgroundImagePath: String?, context: Context): String {
    var fileName = "none"
    if (backgroundImagePath != null) {
        val uri = Uri.parse(backgroundImagePath)
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            uri?.let {
                if (it.scheme.equals("file")) {
                    fileName = it.lastPathSegment.toString()
                } else {
                    var cursor: Cursor? = null
                    try {
                        cursor = context.contentResolver.query(
                            it, arrayOf(
                                MediaStore.Images.ImageColumns.DISPLAY_NAME
                            ), null, null, null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
                            val index =
                                cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                            if (index >= 0) fileName = cursor.getString(index)
                        }
                    } finally {
                        cursor?.close()
                    }
                }
            }
        } catch (_: Exception) {

        }
    }
    return fileName
}

@Composable
fun ColorThemeSettingsContent(
    title: String,
    colorBackground: Color,
    onChangeColorBackground: (Color) -> Unit,
    onSetDefaultColorBackground: () -> Unit,
    colorText: Color,
    onChangeColorText: (Color) -> Unit,
    onSetDefaultColorText: () -> Unit,
    colorLink: Color,
    onChangeColorLink: (Color) -> Unit,
    onSetDefaultColorLink: () -> Unit,
    colorInfoArea: Color,
    onChangeColorInfoArea: (Color) -> Unit,
    onSetDefaultColorInfoArea: () -> Unit,
    showBackgroundImage: Boolean,
    onChangeShowBackgroundImage: (Boolean) -> Unit,
    backgroundImage: String?,
    onChangeBackgroundImage:  (Uri?) -> Unit,
    tileBackgroundImage: Boolean,
    onChangeTileBackgroundImage: (Boolean) -> Unit,
    marginTop: String,
    onChangeMarginTop: (String) -> Unit,
    marginBottom: String,
    onChangeMarginBottom: (String) -> Unit,
    marginLeft: String,
    onChangeMarginLeft: (String) -> Unit,
    marginRight: String,
    onChangeMarginRight: (String) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backgroundImageFileName = remember {
        mutableStateOf("none")
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onChangeBackgroundImage(uri)
        }
    )

    LaunchedEffect(key1 = backgroundImage) {
        backgroundImageFileName.value = readFileName(backgroundImage, context)
    }

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
                    SettingsTitle(text = title,
                        modifier = Modifier.padding(bottom = paddingSmall))
                }

                item {
                    SettingsCard(title = stringResource(id = R.string.colors)) {
                        Column {
                            SettingsColor(
                                text = stringResource(id = R.string.color_background),
                                color = colorBackground,
                                onColorChanged = onChangeColorBackground,
                                onSelectDefaultColor = onSetDefaultColorBackground,
                                selectNoneButtonText = stringResource(id = R.string.default_color)
                            )
                            SettingsColor(
                                text = stringResource(id = R.string.color_foreground),
                                color = colorText,
                                onColorChanged = onChangeColorText,
                                onSelectDefaultColor = onSetDefaultColorText,
                                selectNoneButtonText = stringResource(id = R.string.default_color)
                            )
                            SettingsColor(
                                text = stringResource(id = R.string.color_linktext),
                                color = colorLink,
                                onColorChanged = onChangeColorLink,
                                onSelectDefaultColor = onSetDefaultColorLink,
                                selectNoneButtonText = stringResource(id = R.string.default_color)
                            )
                            SettingsColor(
                                text = stringResource(id = R.string.color_infotext),
                                color = colorInfoArea,
                                onColorChanged = onChangeColorInfoArea,
                                onSelectDefaultColor = onSetDefaultColorInfoArea,
                                selectNoneButtonText = stringResource(id = R.string.default_color)
                            )
                        }
                    }
                }
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.color_theme_backgroundimage_header),
                        modifier = Modifier.padding(top = paddingMedium)
                    ) {
                        Column {
                            SettingsCheckbox(value = showBackgroundImage,
                                label = stringResource(id = R.string.color_theme_show_background_image),
                                onChange = onChangeShowBackgroundImage)
                            SettingsCheckbox(value = tileBackgroundImage,
                                label = stringResource(id = R.string.repeat_tiled_image),
                                onChange = onChangeTileBackgroundImage)
                            SettingsButton(
                                title = stringResource(id = R.string.color_theme_selected_image),
                                defaultValue = backgroundImageFileName.value,
                                onClick = {
                                    coroutineScope.launch {
                                        imagePicker.launch("image/*")
                                    }
                                },
                                showDefaultValue = true
                            )
                        }
                    }
                }
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.margins),
                        modifier = Modifier.padding(top = paddingMedium)
                    ) {
                        Column {
                            SettingsTextField(
                                value = marginTop, label = stringResource(id = R.string.marginTop),
                                onChange = onChangeMarginTop
                            )
                            SettingsTextField(
                                value = marginBottom, label = stringResource(id = R.string.marginBottom),
                                onChange = onChangeMarginBottom
                            )
                            SettingsTextField(
                                value = marginLeft, label = stringResource(id = R.string.marginLeft),
                                onChange = onChangeMarginLeft
                            )
                            SettingsTextField(
                                value = marginRight, label = stringResource(id = R.string.marginRight),
                                onChange = onChangeMarginRight
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
private fun ColorThemeSettingsPreview() {
    AppTheme {
        Surface {
            ColorThemeSettingsContent(
                title = "theme1",
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                colorBackground = Color(0xFFDAD1C0),
                onChangeColorBackground = {},
                onSetDefaultColorBackground = {},
                colorText = Color(0xff000000),
                onChangeColorText = {},
                onSetDefaultColorText = {},
                colorLink = Color(0xFF26ADD6),
                onChangeColorLink = {},
                onSetDefaultColorLink = {},
                colorInfoArea = Color(0xFFDAD1C0),
                onChangeColorInfoArea = {},
                onSetDefaultColorInfoArea = {},
                tileBackgroundImage = true,
                onChangeTileBackgroundImage = {},
                showBackgroundImage = false,
                onChangeShowBackgroundImage = {},
                marginTop = "15",
                onChangeMarginTop = {},
                marginBottom = "5",
                onChangeMarginBottom = {},
                marginLeft = "4",
                onChangeMarginLeft = {},
                marginRight = "5",
                onChangeMarginRight = {},
                backgroundImage = null,
                onChangeBackgroundImage = {}
            )
        }
    }
}