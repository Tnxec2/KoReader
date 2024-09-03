package com.kontranik.koreader.compose.ui.settings.elements.fontpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.kontranik.koreader.utils.FontsHelper
import com.kontranik.koreader.utils.typefacefactory.TypefaceRecord
import kotlinx.coroutines.launch

@Composable
fun FontPickerDialog(
    typefaceRecord: TypefaceRecord,
    showSystemFonts: Boolean,
    showNotoFonts: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: (TypefaceRecord) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val fontlist: MutableState<List<TypefaceRecord>> = remember {
        mutableStateOf(
            listOf()
        )
    }

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            fontlist.value = FontsHelper.loadFontsForSettings(context, showSystemFonts, showNotoFonts)
        }
    }

    FontPickerDialogContent(
        typefaceRecord = typefaceRecord,
        fontlist = fontlist,
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation)
}
