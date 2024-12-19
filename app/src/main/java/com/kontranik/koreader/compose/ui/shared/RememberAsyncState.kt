package com.kontranik.koreader.compose.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun <T> rememberAsyncState(
    key: Any?,
    fetcher: suspend () -> T,
    initialValue: T
): State<T> {
    // Initialisiere den State mit einem Anfangswert
    val state = remember(key) { mutableStateOf(initialValue) }

    // LaunchedEffect für asynchrones Laden
    LaunchedEffect(key) {
        val result = withContext(Dispatchers.IO) {
            fetcher()
        }
        state.value = result
    }

    return state
}