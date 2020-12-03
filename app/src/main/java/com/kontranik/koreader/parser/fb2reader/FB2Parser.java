package com.kontranik.koreader.parser.fb2reader;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.*;

import com.kontranik.koreader.parser.fb2reader.model.FB2Elements;
import com.kontranik.koreader.parser.fb2reader.model.FB2ParserObject;
import com.kontranik.koreader.parser.fb2reader.model.FB2Scheme;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class FB2Parser extends DefaultHandler {

    // SAX-EventHandler erstellen
    // private DefaultHandler handler = this;

    // Inhalt mit dem Default-Parser parsen
    private SAXParser saxParser;


    FB2ParserObject object = new FB2ParserObject();

    private String filePath;

    public FB2Parser(String appDir, String filePath) {
        this.filePath = filePath;
        object.clear();
        object.fileHelper = new FileHelper(appDir);
    }

    public FB2Scheme parseBook() throws Exception {
        return parse(false);
    }

    public FB2Scheme parseScheme() throws Exception {
        return parse(true);
    }

    private FB2Scheme parse(boolean onlyscheme) throws Exception {
        object.onlyscheme = onlyscheme;

        if ( !onlyscheme) object.fileHelper.clearworkdir();

        if ( filePath.toLowerCase().endsWith(".zip")) {

            ZipFile zipFile = new ZipFile(filePath);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if( ! entry.isDirectory()){
                    if ( entry.getName().toLowerCase().endsWith(".fb2") ) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        saxParser = SAXParserFactory.newInstance().newSAXParser();
                        saxParser.parse(inputStream, this);
                        break;
                    }
                }
            }
            zipFile.close();
        } else if ( filePath.toLowerCase().endsWith(".fb2") ){
            saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(new File(filePath), this);
        }

        if ( ! object.onlyscheme ) object.fileHelper.writeSchema(object.fb2scheme);

        return object.fb2scheme;
    }

	// SAX DefaultHandler Methoden
    @Override
    public void startDocument() throws SAXException {
        
    }

    @Override
    public void endDocument() throws SAXException {
        
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes attrs) throws SAXException {

        String eName = ("".equals(localName)) ? qName : localName;

        FB2Elements fel = FB2Elements.fromString(eName.toLowerCase());

        if ( fel == null) {
            System.out.println(eName);
            return;
        } 

        try {
			FB2StartElement.startElement(eName, fel, attrs, object);
		} catch (Exception e) {
			throw new SAXException(e);
		}
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {

        String eName = ("".equals(localName)) ? qName : localName;

        FB2Elements fel = FB2Elements.fromString(eName.toLowerCase());

        if ( fel == null) {
            System.out.println("EndElement: " + eName);
            return;
        } 

        try {
			FB2EndElement.endElement(eName, fel, object);
		} catch (Exception e) {
            e.printStackTrace();
			throw new SAXException(e);
		}
    }

    // Erzeugt einen String aus den Char-Arrays und liest
    // diesen in einen StringBuffer ein
    public void characters(char[] buf, int offset, int len) throws SAXException {
        String s = new String(buf, offset, len);

        if ( object.myParseText ) object.myText.append(s);
        
        if ( object.isAnnotation ) {
            object.fb2scheme.description.titleInfo.annotation.append(s);
        } else if ( object.isCoverpage ) {
            object.fb2scheme.description.titleInfo.coverpage.append(s);
        } else if ( object.isHistory ) {
            object.fb2scheme.description.documentInfo.history.append(s);
        } else if ( object.isSection && ! object.onlyscheme ) {
            object.mySection.text.append(s);
        }
    }
}
