package com.kontranik.koreader.model

import android.graphics.Typeface
import androidx.compose.ui.unit.IntSize
import com.kontranik.koreader.compose.theme.defaultLetterSpacing
import com.kontranik.koreader.compose.theme.defaultLineSpacingMultiplier
import com.kontranik.koreader.compose.theme.defaultTextSize
import com.kontranik.koreader.compose.ui.settings.PREF_DEFAULT_MARGIN
import com.kontranik.koreader.compose.ui.settings.SettingsViewModel
import com.kontranik.koreader.utils.PrefsHelper

data class PageViewSettings(
    var textSize: Float = defaultTextSize,
    var lineSpacingMultiplier: Float = defaultLineSpacingMultiplier,
    var letterSpacing: Float = defaultLetterSpacing,
    var typeFace: Typeface = Typeface.DEFAULT,
    var marginTop: Int = PREF_DEFAULT_MARGIN,
    var marginBottom: Int = PREF_DEFAULT_MARGIN,
    var marginLeft: Int = PREF_DEFAULT_MARGIN,
    var marginRight: Int = PREF_DEFAULT_MARGIN,
    var pageSize: IntSize = IntSize(0, 0)
)

