package com.kontranik.koreader.opds.model

import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R

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
        href = KoReaderApplication.getContext().resources.getString(R.string.icon_back_base64),
        rel = OpdsTypes.REL_IMAGE
    )
)

val LOAD = Entry(
    id = "load",
    title = "load...",
    thumbnail = Link(
        type = OpdsTypes.TYPE_LINK_IMAGE_PNG,
        title = null,
        href = KoReaderApplication.getContext().resources.getString(R.string.icon_loading_base64),
        rel = OpdsTypes.REL_IMAGE
    )
)