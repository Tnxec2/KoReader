package com.kontranik.koreader.opds.model

class OpdsTypes {
    companion object {
        val TAG_FEED = "feed"
        val TAG_ENTRY = "entry"
        val TAG_CONTENT = "content"
        val TAG_TITLE = "title"
        val TAG_SUBTITLE = "subtitle"
        val TAG_PUBLISHED = "published"
        val TAG_RIGHTS = "rights"
        val TAG_dcterms_LANG = "dcterms:language"
        val TAG_ICON = "icon"
        val TAG_ID = "id"
        val TAG_LINK = "link"
        val TAG_NAME = "name"
        val TAG_URI = "uri"
        val TAG_EMAIL = "email"
        val TAG_AUTHOR = "author"
        val ATTR_TITLE = "title"
        val ATTR_REL = "rel"
        val ATTR_TYPE = "type"
        val ATTR_HREF = "href"

        val TYPE_APPLICATION_PREFIX = "application/"
        val TYPE_LINK_OPDS_CATALOG = "application/atom+xml;profile=opds-catalog"
        val TYPE_LINK_ATOM_XML = "application/atom+xml"
        val TYPE_LINK_OPEN_SEARCH = "application/opensearchdescription+xml"
        val TYPE_LINK_IMAGE_PNG = "image/png"
        val TYPE_LINK_IMAGE_PREFIX = "image/"
        val TYPE_TEXT = "text"
        val TYPE_XHTML = "xhtml"
        val TYPE_HTML = "text/html"

        val TYPE_APP_FB2 = "application/fb2"
        val TYPE_APP_HTML = "application/html"
        val TYPE_APP_TXT = "application/txt"
        val TYPE_APP_RTF = "application/rtf"
        val TYPE_APP_EPUB = "application/epub"
        val TYPE_APP_FB2ZIP = "application/fb2+zip"
        val TYPE_APP_HTMLZIP = "application/html+zip"
        val TYPE_APP_TXTZIP = "application/txt+zip"
        val TYPE_APP_RTFZIP = "application/rtf+zip"
        val TYPE_APP_EPUBZIP = "application/epub+zip"
        val TYPE_APP_MOBIPOCKET = "application/x-mobipocket-ebook"

        val TYPE_DOWNLOADABLE = arrayListOf(
            TYPE_APP_FB2,
            TYPE_APP_HTML,
            TYPE_APP_TXT,
            TYPE_APP_RTF,
            TYPE_APP_EPUB,
            TYPE_APP_FB2ZIP,
            TYPE_APP_HTMLZIP,
            TYPE_APP_TXTZIP,
            TYPE_APP_RTFZIP,
            TYPE_APP_EPUBZIP,
            TYPE_APP_MOBIPOCKET
        )

        val extensions = hashMapOf(
            TYPE_APP_FB2 to "fb2",
            TYPE_APP_HTML to "html",
            TYPE_APP_TXT to "txt",
            TYPE_APP_RTF to "rtf",
            TYPE_APP_EPUB to "epub",
            TYPE_APP_FB2ZIP to "fb2.zip",
            TYPE_APP_HTMLZIP to "html.zip",
            TYPE_APP_TXTZIP to "txt.zip",
            TYPE_APP_RTFZIP to "rtf.zip",
            TYPE_APP_EPUBZIP to "epub.zip",
            TYPE_APP_MOBIPOCKET to "mobi"
        )

        val REL_THUMBNAIL = "http://opds-spec.org/image/thumbnail"
        val REL_THUMBNAIL_X = "x-stanza-cover-image"
        val REL_IMAGE = "http://opds-spec.org/image"
        val REL_IMAGE_X = "x-stanza-cover-image-thumbnail"
        val REL_SUBSECTION = "subsection"
        val REL_SEARCH = "search"
        val REL_SELF = "self"
        val REL_ALTERNATE = "alternate"
        val REL_RELATED = "related"
        val REL_START = "start"
        val REL_NEXT = "next"
        val FIRST = "first"
        val PREVIOUS = "previous"
        val REL_NEW = "http://opds-spec.org/sort/new"
        val REL_OPEN_ACCESS = "http://opds-spec.org/acquisition/open-access"
        val REL_ACQUISITION = "http://opds-spec.org/acquisition"

        val MAP_REL = hashMapOf(
            REL_OPEN_ACCESS to "Open Access",
            REL_ALTERNATE to "Alternate",
            REL_RELATED to "Related",
            REL_ACQUISITION to "Acquisition",
            REL_NEW to "New",
            REL_THUMBNAIL to "Thumbnail",
            REL_IMAGE to "Image",
            )

        fun mapRel(rel: String?): String {
            if (rel == null) return "Links"
            if (MAP_REL.containsKey(rel))
                return MAP_REL[rel]!!
            return rel
        }
    }
}