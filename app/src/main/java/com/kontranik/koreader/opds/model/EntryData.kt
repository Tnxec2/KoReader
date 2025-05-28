package com.kontranik.koreader.opds.model

import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import com.kontranik.koreader.utils.icon_back_base64
import com.kontranik.koreader.utils.icon_loading_base64

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
        href = icon_back_base64,
        rel = OpdsTypes.REL_IMAGE
    )
)

val LOAD = Entry(
    id = "load",
    title = "load...",
    thumbnail = Link(
        type = OpdsTypes.TYPE_LINK_IMAGE_PNG,
        title = null,
        href = icon_loading_base64,
        rel = OpdsTypes.REL_IMAGE
    )
)