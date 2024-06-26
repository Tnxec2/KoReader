package com.kontranik.koreader.opds.model

import java.io.Serializable

class Link(val type: String?, var title: String?, var href: String?, val rel: String?) :
    Serializable {

    constructor(title: String?, href: String?) : this(type = OpdsTypes.TYPE_LINK_OPDS_CATALOG, title, href, rel = OpdsTypes.REL_SUBSECTION )

    fun getTitle() : CharSequence? {
        return title ?:
            if ( type?.startsWith(OpdsTypes.TYPE_APPLICATION_PREFIX) == true && !type.startsWith(OpdsTypes.TYPE_LINK_ATOM_XML))
                type.split("/")[1]
            else if (rel != null)
                OpdsTypes.MAP_REL[rel] ?: rel
            else href
    }

    fun isThumbnail(): Boolean {
        return type?.startsWith(OpdsTypes.TYPE_LINK_IMAGE_PREFIX) == true && ( rel == OpdsTypes.REL_THUMBNAIL || rel == OpdsTypes.REL_THUMBNAIL_X)
    }

    fun isCoverImage(): Boolean {
        return type?.startsWith(OpdsTypes.TYPE_LINK_IMAGE_PREFIX) == true && ( rel == OpdsTypes.REL_IMAGE || rel == OpdsTypes.REL_IMAGE_X || rel == OpdsTypes.REL_COVER)
    }

    fun isCatalogEntry(): Boolean {
        return (type?.startsWith(OpdsTypes.TYPE_LINK_ATOM_XML) == true)
    }

    override fun toString(): String {
        return "[$title]($href) - (type=$type, rel=$rel)"
    }

    fun isDownloadable(): Boolean {
        return OpdsTypes.TYPE_DOWNLOADABLE.contains(type)
    }

    fun getExtension(): String {
        return OpdsTypes.extensions[type] ?: "unknown"
    }
}