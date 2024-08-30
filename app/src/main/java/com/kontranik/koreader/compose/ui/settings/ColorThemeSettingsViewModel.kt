package com.kontranik.koreader.compose.ui.settings

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ColorThemeSettingsViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {


    private val themeIndexString: String = savedStateHandle[ColorThemeSettingsDestination.THEME_INDEX] ?: "0"

    val themeIndexState = mutableIntStateOf(0)

    init {
        themeIndexState.intValue = themeIndexString.toInt()
    }

}