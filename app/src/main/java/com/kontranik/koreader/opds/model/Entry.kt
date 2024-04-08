package com.kontranik.koreader.opds.model

import android.graphics.Bitmap
import com.kontranik.koreader.App
import com.kontranik.koreader.R
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

    companion object {
        val BACK = Entry(
            id = "back",
            title = "..",
            clickLink = Link(
                type = OpdsTypes.TYPE_LINK_OPDS_CATALOG,
                title = "back",
                href = "back",
                rel = null
            ),
            thumbnail = Link(
                type = OpdsTypes.TYPE_LINK_IMAGE_PNG,
                title = null,
                href = App.getContext().resources.getString(R.string.icon_back_base64),
                rel = OpdsTypes.REL_IMAGE
            )
        )
    }

}

