package com.kontranik.koreader.parser.fb2reader;

import com.kontranik.koreader.parser.fb2reader.model.Author;
import com.kontranik.koreader.parser.fb2reader.model.BinaryData;
import com.kontranik.koreader.parser.fb2reader.model.FB2Elements;
import com.kontranik.koreader.parser.fb2reader.model.FB2ParserObject;
import com.kontranik.koreader.parser.fb2reader.model.FB2Section;

import org.xml.sax.*;

/**
 * FB2StartElement
 */
public class FB2StartElement {

    public static void startElement(
        String eName, 
        FB2Elements fel, 
        Attributes attrs, 
        FB2ParserObject object) throws Exception {
        
        switch (fel) {
            case section:
            case body:
                gotNewSection(eName, fel, attrs, object);
                break;
            case binary:
                startBinarys(object, attrs);
                break;
            case description:
                object.isDescription =true;
                break;
            default:
                if( object.isDescription ) startElementDescription(fel, attrs, object);
                parseStartElement(fel, attrs, object);
                break;
        }
    }

    private static void parseStartElement(FB2Elements fel, Attributes attrs, FB2ParserObject object) {
        if ( fel.equals(FB2Elements.title) ) {
            object.myParseText = true;
        }

        if ( ! object.isSection 
            && ! object.isAnnotation 
            && ! object.isCoverpage 
            && ! object.isHistory) return;

        if ( object.isSection && object.onlyscheme) return;

        String result = "";
        int deep = object.isSection ? object.mySection.deep : 0;

        String href = null;
        for ( int a = 0; a < attrs.getLength(); a++ ) {
            String aName = attrs.getQName(a);
            if ( aName.contains(":href")) { 
                href = attrs.getValue(aName); 
                if ( href.startsWith("#")) href = href.substring(1);
                break; 
            }
        }
        switch (fel) {
            case emptyline:
                //result = "<br/>";
                break;
            case p:
            case stanza:
                if ( ! object.isTitle)
                    result = "<p>";
                break;
            case cite:
                result = "<cite>";
                break;
            case title:
                object.isTitle = true;
                int d = Math.min(deep, FB2ParserObject.maxHeader);
                result = "<H" + ( d + 1) + ">";
                break;
            case subtitle:
                int s = Math.min(deep+1, FB2ParserObject.maxHeader);
                result = "<H" + (s + 1) + ">";
                break;
            case strong:
                result = "<strong>";
                break;
            case emphasis:
                result = "<em>";
                break;
            case strikethrough:
                result = "<strike>";
                break;
            case sub:
                result = "<sub>";
                break;
            case sup:
                result = "<sup>";
                break;
            case code:
                object.isCode = true;
                result = "<pre><code>";
                break;
            case epigraph:
                result = "<blockquote>";
                break;
            case a:
                if ( href != null) {
                    String cl = "";
                    String linktype = "";
                    object.isSupNote = false;
                    if ( "note".equals(attrs.getValue("type")) ) {
                        cl = "<sup>";
                        linktype = "class=\"note\"";
                        object.isSupNote = true;
                    }
                    result = " <a href=\"" + href + "\" " +  linktype + " >" + cl;
                } 
                break;
            case image:
                if ( href != null) result = "<img src=\"" + href + "\">";
                break;
            default:
                break;
        }
        if ( object.isAnnotation ) {
            object.fb2scheme.description.titleInfo.annotation.append(result);
        } else if ( object.isCoverpage ) {
            object.fb2scheme.description.titleInfo.coverpage.append(result);
            if ( fel.equals(FB2Elements.image) && href != null) {
                object.fb2scheme.description.titleInfo.coverImageSrc = href;
            }
        } else if ( object.isHistory ) {
            object.fb2scheme.description.documentInfo.history.append(result);
        } else if ( object.isSection ) {
            object.mySection.text.append(result);
        }
	}

	private static void gotNewSection(String eName, FB2Elements el, Attributes attrs, FB2ParserObject object) throws Exception {
        object.isSection = true;
        if ( attrs.getValue("notes") != null) {
            object.isNotes = true;
        } else {
            if ( ! object.onlyscheme && object.isSection ) object.fileHelper.writeSection(object.mySection);

            Integer parentid = object.mySection != null ? object.mySection.orderid : null;
            object.mySection = new FB2Section(object.sectionid, attrs.getValue("id"), el, object.sectionDeep, parentid);
            object.fb2scheme.sections.add(object.mySection);
            // System.out.printf("New Section Id: " + object.sectionid + ", deep: " + object.sectionDeep + ", Name: " + el.elName + "\n");
            object.sectionid++;
            object.sectionDeep++;
        }
    }

    private static void startElementDescription(FB2Elements el, Attributes attrs, FB2ParserObject object) {
        switch (el) {
            case titleinfo:
                object.isDescriptionTitleInfo = true;
                break;
            case documentinfo:
            object.isDescriptionDocumentInfo = true;
                break;
            case publishinfo:
            object.isDescriptionPublishInfo = true;
                break;
            case custominfo:
            object.isDescriptionCustomInfo = true;
                break;
            case author:
                if ( object.isDescriptionTitleInfo ) object.fb2scheme.description.titleInfo.authors.add(new Author());
                if ( object.isDescriptionDocumentInfo ) object.fb2scheme.description.documentInfo.authors.add(new Author());
                object.isAuthor = true;
                break;
            case translator:
                if ( object.isDescriptionTitleInfo ) object.fb2scheme.description.titleInfo.translators.add(new Author());
                object.isTranslator = true;
                break;
            case booktitle:
            case firstname:
            case middlename:
            case lastname:
            case nickname:
            case homepage:
            case email:
            case genre:
            case bookname:
            case publisher:
            case city:
            case year:
            case isbn:
            case keywords:
            case version:
                object.myParseText = true;
                break;
            case sequence:
                if ( object.isDescriptionTitleInfo ) {
                    object.fb2scheme.description.titleInfo.sequenceName = attrs.getValue("name");
                    object.fb2scheme.description.titleInfo.sequenceNumber =  attrs.getValue("number");
                }
                break;
            case date:
                if ( object.isDescriptionTitleInfo ) {
                    object.fb2scheme.description.titleInfo.date = attrs.getValue("value");
                }
                break;
            case annotation:
                object.isAnnotation = true;
                break;
            case coverpage:
                object.isCoverpage = true;
                break;
            case history:
                object.isHistory = true;
                break;
            default:
                break;
        }
    }

    private static void startBinarys(FB2ParserObject object, Attributes attrs) throws Exception {
        if ( object.isSection) {
            object.isSection = false;
        }
        object.isBinary = true;
        String id = attrs.getValue("id");
        if ( ! object.onlyscheme || (
            object.fb2scheme.cover == null && 
            object.fb2scheme.description.titleInfo.coverImageSrc.equals(id) ) 
            ) {
            object.myBinaryData = new BinaryData(id, attrs.getValue("content-type"));
            object.myParseText = true;
        }
    }

}