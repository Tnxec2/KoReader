package com.kontranik.koreader.model


data class PageViewColorSettings (
    var showBackgroundImage: Boolean,
    var backgroundImageUri: String?,
    var backgroundImageTiledRepeat: Boolean,
    var colorText: String,
    var colorBack: String,
    var colorLink: String,
    var colorInfoText: String,
){}