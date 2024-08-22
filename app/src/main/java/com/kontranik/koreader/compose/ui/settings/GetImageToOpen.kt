package com.kontranik.koreader.compose.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

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
class GetImageToOpen(): ActivityResultContract<String, Uri?>() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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