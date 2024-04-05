package com.kontranik.koreader.opds

import android.util.Xml
import com.kontranik.koreader.opds.model.OpenSearchDescription

import com.kontranik.koreader.opds.model.Url
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

private val ns: String? = null
class OpenSearchXmlParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): OpenSearchDescription {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): OpenSearchDescription {
        val urls = mutableListOf<Url>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "Url" -> readUrl(parser)?.let { urls.add(it) }
                else -> skip(parser)
            }
        }
        return OpenSearchDescription(
            urls = urls
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readUrl(parser: XmlPullParser): Url? {

        var type: String? = null
        var template: String? = null
        parser.require(XmlPullParser.START_TAG, ns, "Url")

        if (parser.name == "Url") {
            template = parser.getAttributeValue(null, "template")
            type = parser.getAttributeValue(null, "type")
        }
        parser.nextTag()
        if (type!=null && template!=null)
            return Url(type, template)
        return null
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
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