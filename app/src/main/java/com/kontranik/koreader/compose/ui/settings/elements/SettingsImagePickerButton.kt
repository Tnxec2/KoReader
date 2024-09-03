package com.kontranik.koreader.compose.ui.settings.elements

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingSmall
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
fun SettingsImagePickerButton(
    title: String,
    imageUri: String?,
    @DrawableRes icon: Int? = null,
    onChangeImageUri: (Uri?) -> Unit,
    showImageUriValue: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val backgroundImageFileName = remember {
        mutableStateOf("none")
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onChangeImageUri(uri)
        }
    )

    LaunchedEffect(key1 = imageUri) {
        backgroundImageFileName.value = readFileName(imageUri, context)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = paddingSmall)
            .clickable {
                coroutineScope.launch {
                    imagePicker.launch("image/*")
                }
            }
            .fillMaxWidth()
    ) {
        icon?.let{Icon(painter = painterResource(id = it), contentDescription = title,
            modifier = Modifier.padding(end = paddingSmall))}

        Column(
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
            if (showImageUriValue) Text(
                text = imageUri ?: "None",
                modifier = Modifier
            )
        }
    }
}



@Preview
@Composable
private fun SettingsImagePickerButtonPreview() {
    SettingsImagePickerButton(
        title = "Title",
        imageUri = "entry1",
        icon = R.drawable.ic_iconmonstr_paintbrush_10,
        showImageUriValue = true,
        onChangeImageUri = {}
    )
}