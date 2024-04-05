package com.kontranik.koreader.opds.model

import java.io.Serializable

class Link(val type: String?, var title: String?, var href: String?, val rel: String?) :
    Serializable {

    constructor(title: String?, href: String?) : this(type = OpdsTypes.TYPE_LINK_OPDS_CATALOG, title, href, rel = OpdsTypes.REL_SUBSECTION )

    fun getTitle() : CharSequence? {
        return title ?: if ( type?.startsWith(OpdsTypes.TYPE_APPLICATION_PREFIX) == true) type.split("/")[1] else href
    }
    fun isThumbnail(): Boolean {
        return type?.startsWith(OpdsTypes.TYPE_LINK_IMAGE_PREFIX) == true && ( rel == OpdsTypes.REL_THUMBNAIL || rel == OpdsTypes.REL_THUMBNAIL_X)
    }

    fun isCoverImage(): Boolean {
        return type?.startsWith(OpdsTypes.TYPE_LINK_IMAGE_PREFIX) == true && ( rel == OpdsTypes.REL_IMAGE || rel == OpdsTypes.REL_IMAGE_X)
    }

    fun isCatalogEntry(): Boolean {
        return (type == OpdsTypes.TYPE_LINK_OPDS_CATALOG || type == OpdsTypes.TYPE_LINK_ATOM_XML)
    }

    override fun toString(): String {
        return "$title [$href] | (type=$type, rel=$rel)"
    }

    fun isDownloadable(): Boolean {
        return OpdsTypes.TYPE_DOWNLOADABLE.contains(type)
    }

    fun getExtension(): String? {
        return OpdsTypes.extensions[type]
    }
}