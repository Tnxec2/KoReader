package com.kontranik.koreader.opds

import android.util.Log
import android.util.Xml
import com.kontranik.koreader.opds.model.Author
import com.kontranik.koreader.opds.model.Content
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.opds.model.Opds
import com.kontranik.koreader.opds.model.OpdsTypes
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

// We don't use namespaces.
private val ns: String? = null


class OpdsXmlParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): Opds {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): Opds {
        val entries = mutableListOf<Entry>()
        var title: String? = null
        var subtitle: String? = null
        var author: Author? = null
        var icon: String? = null
        var search: Link? = null
        val links = mutableListOf<Link>()
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_FEED)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                OpdsTypes.TAG_ENTRY -> entries.add(readEntry(parser))
                OpdsTypes.TAG_TITLE -> title = readTitle(parser)
                OpdsTypes.TAG_SUBTITLE -> subtitle = readSubtitle(parser)
                OpdsTypes.TAG_AUTHOR -> author = readAuthor(parser)
                OpdsTypes.TAG_ICON -> icon = readIcon(parser)
                OpdsTypes.TAG_LINK -> {
                    readLink(parser).let { link ->
                        if (link.type == OpdsTypes.TYPE_LINK_OPEN_SEARCH && link.rel == OpdsTypes.REL_SEARCH)
                            search = link
                        else
                            links.add(link)
                    }
                }

                else -> skip(parser)
            }
        }
        return Opds(
            title = title,
            subtitle = subtitle,
            author = author,
            icon = icon,
            links = links,
            entries = entries,
            search = search
        )
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): Entry {
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_ENTRY)
        var title: String? = null
        var published: String? = null
        var rights: String? = null
        var language: String? = null
        var author: Author? = null
        var content: Content? = null
        var clickLink: Link? = null
        var thumbnail: Link? = null
        var coverImage: Link? = null
        val links = mutableListOf<Link>()
        val categorys = mutableListOf<String>()
        var id: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                OpdsTypes.TAG_TITLE -> title = readTitle(parser)
                OpdsTypes.TAG_PUBLISHED -> published = readText(parser)
                OpdsTypes.TAG_RIGHTS -> rights = readText(parser)
                OpdsTypes.TAG_dcterms_LANG -> language = readText(parser)
                OpdsTypes.TAG_AUTHOR -> author = readAuthor(parser)
                OpdsTypes.TAG_CONTENT -> content = readContent(parser)
                OpdsTypes.TAG_LINK -> {
                    readLink(parser).let { link ->
                        if (link.isCatalogEntry() && (link.rel == null || link.rel == OpdsTypes.REL_SUBSECTION))
                            clickLink = link
                        else if (link.isThumbnail())
                            thumbnail = link
                        else if (link.isCoverImage())
                            coverImage = link
                        else
                            links.add(link)
                    }
                }

                OpdsTypes.TAG_ID -> id = readID(parser)
                else -> {
                    skip(parser)
                }
            }

        }
        Log.d("OPDS", "readed entry: $title")

        return Entry(
            id = id,
            title = title,
            published = toDate(published),
            rights = rights,
            author = author,
            language = language,
            content = content,
            clickLink = clickLink,
            thumbnail = thumbnail,
            image = coverImage,
            otherLinks = links
        )
    }

    private fun toDate(dt: String?): Date? {
        if (dt == null) return null
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return try {
            val date: Date? = format.parse(dt)
            date
        } catch (e: ParseException) {
            null
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readAuthor(parser: XmlPullParser): Author {
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_AUTHOR)
        var name: String? = null
        var uri: String? = null
        var email: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                OpdsTypes.TAG_NAME -> name = readText(parser)
                OpdsTypes.TAG_URI -> uri = readText(parser)
                OpdsTypes.TAG_EMAIL -> email = readText(parser)
                else -> {
                    skip(parser)
                }
            }

        }

        return Author(name, uri, email)
    }

    // Processes title tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_TITLE)
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, OpdsTypes.TAG_TITLE)
        return title
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readSubtitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_SUBTITLE)
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, OpdsTypes.TAG_SUBTITLE)
        return title
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readIcon(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_ICON)
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, OpdsTypes.TAG_ICON)
        return title
    }

    // Processes link tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLink(parser: XmlPullParser): Link {
        var href: String? = null
        var title: String? = null
        var type: String? = null
        var relType: String? = null
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_LINK)

        if (parser.name == OpdsTypes.TAG_LINK) {
            title = parser.getAttributeValue(null, OpdsTypes.ATTR_TITLE)
            relType = parser.getAttributeValue(null, OpdsTypes.ATTR_REL)
            type = parser.getAttributeValue(null, OpdsTypes.ATTR_TYPE)
            href = parser.getAttributeValue(null, OpdsTypes.ATTR_HREF)
        }
        parser.nextTag()
        // parser.require(XmlPullParser.END_TAG, ns, "link")
        return Link(type, title, href, relType)
    }


    // Processes summary tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readContent(parser: XmlPullParser): Content {
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_CONTENT)
        val type = parser.getAttributeValue(null, OpdsTypes.ATTR_TYPE)
        val content =
            if (type == OpdsTypes.TYPE_XHTML)
                readXhtml(parser)
            else
                readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, OpdsTypes.TAG_CONTENT)
        return Content(type ?: OpdsTypes.TYPE_TEXT, content)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readID(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, OpdsTypes.TAG_ID)
        val idContent = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, OpdsTypes.TAG_ID)
        return idContent
    }

    // For the tags title and summary, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readXhtml(parser: XmlPullParser): String {
        return getInnerXml(parser)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun getInnerXml(parser: XmlPullParser): String {
        val sb = StringBuilder()
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> {
                    depth--
                    if (depth > 0) {
                        sb.append("</${parser.name}>")
                    }
                }

                XmlPullParser.START_TAG -> {
                    depth++
                    val attrs = StringBuilder()
                    var i = 0
                    while (i < parser.attributeCount) {
                        attrs.append(
                            "${parser.getAttributeName(i)}=\"${parser.getAttributeValue(i)}\" "
                        )
                        i++
                    }
                    sb.append("<${parser.name} ${attrs}>")
                }

                else -> sb.append(parser.text)
            }
        }
        return sb.toString()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}