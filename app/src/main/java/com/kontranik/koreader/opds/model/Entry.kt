package com.kontranik.koreader.opds.model

import android.graphics.Bitmap
import android.view.View
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import java.io.Serializable
import java.util.Date


data class Entry(
    val id: String? = null,
    var title: String,
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
    constructor(link: Link) : this(
            id = link.title,
            title = link.getTitle().toString(),
            content = null,
            thumbnail = null,
            clickLink = link,
            otherLinks = null
        )

    @Transient var thumbnailBitmap: Bitmap? = null
    @Transient var thumbnailBitmapLoaded: Boolean = false
}

data class EntryEditDetails(
    var title: String = "",
    var url: String = "",
)

data class EntryUiDetails(
    val title: String,
    val cover: ImageBitmap? = null,
    val author: String? = null,
    val content: String? = null,
)

fun Entry.toEntryEditDetails(): EntryEditDetails {
    return EntryEditDetails(
        title = title,
        url = clickLink?.href ?: ""
    )
}


fun Entry.toUiDetails(): EntryUiDetails {
    return EntryUiDetails(
        title = title,
        cover = thumbnailBitmap?.asImageBitmap(),
        author = author?.toString(),
        content = if (content?.type == OpdsTypes.TYPE_TEXT) content.data else null,
    )
}


