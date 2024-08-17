package com.kontranik.koreader.compose.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asImageBitmap
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.EntryUiDetails
import com.kontranik.koreader.opds.model.toUiDetails
import com.kontranik.koreader.utils.ImageUtils
import kotlinx.coroutines.launch

@Composable
fun rememberOpdsEntryUiDetails(entry: Entry, startUrl: String): MutableState<EntryUiDetails> {
    val coroutineScope = rememberCoroutineScope()

    val entryUiDetailsState = remember {
        mutableStateOf(
            entry.toUiDetails()
        )
    }

    LaunchedEffect(key1 = entry, startUrl) {
        coroutineScope.launch {
            entry.thumbnail?.href?.let {
                val drawable = ImageUtils.drawableFromUrl(
                    it, startUrl
                )
                if (drawable != null) {
                    entryUiDetailsState.value = entryUiDetailsState.value.copy(
                        cover = drawable.asImageBitmap()
                    )
                }
            }
        }
    }

    return entryUiDetailsState
}