package com.kontranik.koreader.compose.ui.shared

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

const val devicePortrait = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=portrait"
const val deviceLandscape = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"

@Preview(name = "Light Mode", device = devicePortrait)
annotation class PreviewPortraitLight

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, device = devicePortrait)
annotation class PreviewPortraitDark

@Preview(name = "Landscape Light Mode", device = deviceLandscape)
annotation class PreviewLandscapeLight

@Preview(name = "Landscape Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, device = deviceLandscape)
annotation class PreviewLandscapeDark

@PreviewPortraitLight
@PreviewPortraitDark
annotation class PreviewPortraitLightDark

@PreviewLandscapeDark
@PreviewLandscapeLight
annotation class PreviewLandscapeLightDark

@PreviewPortraitLightDark
@PreviewLandscapeLightDark
annotation class PreviewPortraitLandscapeLightDark