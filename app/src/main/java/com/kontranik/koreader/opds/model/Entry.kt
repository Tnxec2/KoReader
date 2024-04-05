package com.kontranik.koreader.opds.model

import android.graphics.Bitmap
import java.io.Serializable
import java.util.Date


data class Entry(
    val id: String? = null,
    var title: String? = null,
    val published: Date? = null,
    val rights: String? = null,
    val author: Author? = null,
    val content: Content? = null,
    val clickLink: Link? = null,
    val thumbnail: Link? = null,
    val image: Link? = null,
    val otherLinks: List<Link>? = null,
    val language: String? = null
) : Serializable {
    @Transient var thumbnailBitmap: Bitmap? = null
    @Transient var thumbnailBitmapLoaded: Boolean = false

}

