package com.kontranik.koreader.parser

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory


class FB2Parser : DefaultHandler() {
    private var textBuffer: StringBuffer? = null

    // SAX DefaultHandler Methoden
    @Throws(SAXException::class)
    override fun startDocument() {
        ausgabe("auf geht's!" + neueZeile)
    }

    @Throws(SAXException::class)
    override fun endDocument() {
        ausgabe("finito!" + neueZeile)
    }

    // Starttag auslesen
    @Throws(SAXException::class)
    override fun startElement(namespaceURI: String, localName: String,
                              qName: String, attrs: Attributes) {
        textPuffer()
        val eName = if ("" == localName) qName else localName
        ausgabe("<$eName")

        // Erfassen der Attribute in den Starttags
        if (attrs != null) {
            for (i in 0 until attrs.length) {
                var aName = attrs.getLocalName(i)
                if ("" == aName) aName = attrs.getQName(i)
                ausgabe(" " + aName + "=\"" + attrs.getValue(i) + "\"")
            }
        }
        ausgabe(">")
    }

    // Schlusstags auslesen
    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
        textPuffer()
        val eName = if ("" == localName) qName else localName
        ausgabe("</$eName>")
    }

    // Erzeugt einen String aus den Char-Arrays und liest
    // diesen in einen StringBuffer ein
    @Throws(SAXException::class)
    override fun characters(buf: CharArray, offset: Int, len: Int) {
        val s = String(buf, offset, len)
        if (textBuffer == null) textBuffer = StringBuffer(s) else textBuffer!!.append(s)
    }

    /** ************** Hilfsmethoden *******************  */ // Wandelt den StringBuffer in einen String und
    // &uuml;bergibt ihn zur Ausgabe
    // "xxx" verdeutlicht die Arbeitsweise
    @Throws(SAXException::class)
    private fun textPuffer() {
        if (textBuffer == null) return
        ausgabe("xxx" + textBuffer.toString())
        textBuffer = null
    }

    // Ausgabe des Strings
    // "+++" verdeutlicht die Arbeitsweise
    @Throws(SAXException::class)
    private fun ausgabe(s: String) {
        try {
            if (out == null) out = OutputStreamWriter(System.out, "UTF8")
            out!!.write("$s+++")
            out!!.flush()
        } catch (ex: IOException) {
            throw SAXException("Ein-/Ausgabefehler", ex)
        }
    }

    companion object {
        val neueZeile = System.getProperty("line.separator")
        private var out: Writer? = null
        @JvmStatic
        fun main(argv: Array<String>) {

            // SAX-EventHandler erstellen
            val handler: DefaultHandler = FB2Parser()

            // Inhalt mit dem Default-Parser parsen
            val saxParser: SAXParser
            try {
                saxParser = SAXParserFactory.newInstance().newSAXParser()
                saxParser.parse(File("xml_file.xml"), handler)
            } catch (pe: ParserConfigurationException) {
                pe.printStackTrace()
            } catch (se: SAXException) {
                se.printStackTrace()
            } catch (ie: IOException) {
                ie.printStackTrace()
            }
        }
    }
}