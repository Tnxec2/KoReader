package com.kontranik.koreader.compose.ui.settings.elements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingSmall
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Usage
 *
 * define rememberLauncher:
 *
 *     val filePickerNormalTransactions = rememberLauncherForActivityResult(
 *         contract = GetFileToOpen(),
 *         onResult = { uri ->
 *             uri?.let {
 *                 scope.launch {
 *                     // process uri
 *                 }
 *             }
 *         })
 *
 * start launcher:
 *      filePickerNormalTransactions.launch("text/*")
 *      or
 *      filePickerDatabaseRestore.launch("*/*")
 */
class getImageToOpen(): ActivityResultContract<String, Uri?>() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
    }

    override fun getSynchronousResult(
        context: Context,
        input: String
    ): SynchronousResult<Uri?>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent.takeIf {
            resultCode == Activity.RESULT_OK
        }?.getClipDataUris()
    }

    internal companion object {
        internal fun Intent.getClipDataUris(): Uri? {
            // Use a LinkedHashSet to maintain any ordering that may be
            // present in the ClipData
            return data
        }
    }

}

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
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                onChangeImageUri(uri)
            }

        }
    )

//    val imagePickerNew  = rememberLauncherForActivityResult(
//        contract = getImageToOpen(),
//        onResult = { uri ->
//            onChangeImageUri(uri)
//        })

    LaunchedEffect(key1 = imageUri) {
        backgroundImageFileName.value = readFileName(imageUri, context)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = paddingSmall)
            .clickable {
                coroutineScope.launch {
                    imagePicker.launch(arrayOf("image/*"))
                }
            }
            .fillMaxWidth()
    ) {
        icon?.let{Icon(painter = painterResource(id = it), contentDescription = title,
            modifier = Modifier.padding(end = paddingSmall))}

        Column(Modifier.weight(1f)
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

        IconButton(onClick = { onChangeImageUri(null) }) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete image")
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