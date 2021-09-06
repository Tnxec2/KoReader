package com.kontranik.koreader.parser.fb2reader

import android.util.Log
import com.kontranik.koreader.parser.fb2reader.model.FB2Elements
import com.kontranik.koreader.parser.fb2reader.model.FB2ParserObject
import com.kontranik.koreader.parser.fb2reader.model.FB2Scheme
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class FB2Parser(appDir: String, private var uri: String, private var fileInputStream: InputStream) : DefaultHandler() {
    // SAX-EventHandler erstellen
    // private DefaultHandler handler = this;
    // Inhalt mit dem Default-Parser parsen
    private var saxParser: SAXParser = SAXParserFactory.newInstance().newSAXParser()
    private var fB2ParserObject = FB2ParserObject(FileHelper(appDir))

    @Throws(Exception::class)
    fun parseBook(): FB2Scheme {
        return parse(false)
    }

    @Throws(Exception::class)
    fun parseScheme(): FB2Scheme {
        return parse(true)
    }

    @Throws(Exception::class)
    private fun parse(onlyscheme: Boolean): FB2Scheme {
        fB2ParserObject.onlyscheme = onlyscheme
        if (!onlyscheme) fB2ParserObject.fileHelper.clearworkdir()
        Log.d("fb2parser", uri)
        if (uri.endsWith(".zip")) {
            val zis = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ZipInputStream(fileInputStream, Charset.forName("Cp437"))
            } else {
                ZipInputStream(fileInputStream)
            }
            var ze: ZipEntry

            while (true) {
                ze = zis.nextEntry
                if ( ze == null) break
                if (!ze.isDirectory) {
                    if (ze.name.toLowerCase(Locale.getDefault()).endsWith(".fb2")) {
                        saxParser.parse(zis, this)
                        break
                    }
                }
            }

            zis.close()
        } else if (uri.toLowerCase(Locale.getDefault()).endsWith(".fb2")) {
            saxParser.parse(fileInputStream, this)
        }
        fB2ParserObject.fb2scheme.path = uri
        if (!fB2ParserObject.onlyscheme) {
            fB2ParserObject.fileHelper.writeSchema(fB2ParserObject.fb2scheme)
        }
        return fB2ParserObject.fb2scheme
    }

    // SAX DefaultHandler Methoden
    @Throws(SAXException::class)
    override fun startDocument() {
    }

    @Throws(SAXException::class)
    override fun endDocument() {
    }

    @Throws(SAXException::class)
    override fun startElement(namespaceURI: String, localName: String,
                              qName: String, attrs: Attributes) {
        val eName = if ("" == localName) qName else localName
        val fel = FB2Elements.fromString(eName.toLowerCase(Locale.getDefault()))
        if (fel == null) {
            println(eName)
            return
        }
        try {
            FB2StartElement.startElement(eName, fel, attrs, fB2ParserObject)
        } catch (e: Exception) {
            throw SAXException(e)
        }
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
        val eName = if ("" == localName) qName else localName
        val fel = FB2Elements.fromString(eName.toLowerCase(Locale.getDefault()))
        if (fel == null) {
            println("EndElement: $eName")
            return
        }
        try {
            FB2EndElement.endElement(eName, fel, fB2ParserObject)
        } catch (e: Exception) {
            e.printStackTrace()
            throw SAXException(e)
        }
    }

    // Erzeugt einen String aus den Char-Arrays und liest
    // diesen in einen StringBuffer ein
    @Throws(SAXException::class)
    override fun characters(buf: CharArray, offset: Int, len: Int) {
        var s = String(buf, offset, len)
        if (fB2ParserObject.isCode) {
            Log.d("PARSER", s)
        }
        if (!fB2ParserObject.isBinary) {
            if (s.contains("<") || s.contains("&") || s.contains("\"") || s.contains(">")) {
                s = s.replace("&".toRegex(), "&amp;")
                s = s.replace("\"".toRegex(), "&quot;")
                s = s.replace("<".toRegex(), "&lt;")
                s = s.replace(">".toRegex(), "&gt;")
            }
        }
        if (fB2ParserObject.myParseText) fB2ParserObject.myText.append(s)
        if (fB2ParserObject.isAnnotation) {
            fB2ParserObject.fb2scheme.description.titleInfo.annotation.append(s)
        } else if (fB2ParserObject.isCoverpage) {
            fB2ParserObject.fb2scheme.description.titleInfo.coverpage.append(s)
        } else if (fB2ParserObject.isHistory) {
            fB2ParserObject.fb2scheme.description.documentInfo.history.append(s)
        } else if (fB2ParserObject.isSection && !fB2ParserObject.onlyscheme) {
            if ( fB2ParserObject.mySection != null)
                fB2ParserObject.mySection!!.text.append(s)
        }
    }

    init {
        fB2ParserObject.clear()
    }
}