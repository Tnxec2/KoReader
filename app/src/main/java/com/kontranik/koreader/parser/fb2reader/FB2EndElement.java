package com.kontranik.koreader.parser.fb2reader;

import com.kontranik.koreader.parser.fb2reader.model.FB2Elements;
import com.kontranik.koreader.parser.fb2reader.model.FB2ParserObject;

public class FB2EndElement {

    public static void endElement(String eName, FB2Elements fel, FB2ParserObject object) throws Exception {
        
        switch (fel) {
            case author:
                object.isAuthor = false;
                break;
            case translator:
                object.isTranslator = false;
                break;
            case section:
                if ( object.sectionDeep > 0 ) object.sectionDeep--;
                break;
            case body:
                if ( ! object.onlyscheme && object.isSection )  {
                    object.fileHelper.writeSection(object.mySection);
                    object.isSection = false;
                }
                break;
            case binary:
                endElementBinary(object);
                break;
            case description:
                object.isDescription = false;
                break;
            case titleinfo:
                object.isDescriptionTitleInfo = false;
                break;
            case documentinfo:
                object.isDescriptionDocumentInfo = false;
                break;
            case publishinfo:
                object.isDescriptionPublishInfo = false;
                break;
            case custominfo:
                object.isDescriptionCustomInfo = false;
                break;
            default:
                if ( object.isDescription ) endElementDescription(fel, object);
                parseEndElementInSection(fel, object);
                break;
        }

        // System.out.println("Ende Element: " + eName);
    }

    private static void endElementBinary(FB2ParserObject object) throws Exception  {
        if ( ! object.onlyscheme ||  (
                    object.fb2scheme.cover == null && object.myBinaryData != null && 
                    object.fb2scheme.description.titleInfo.coverImageSrc.equals(object.myBinaryData.getName()) )
                ) {
                    object.myBinaryData.setBase64Encoded(object.myText.toString().toCharArray());
                    if ( ! object.onlyscheme ) object.fileHelper.writeBinary(object.myBinaryData);
                    String coversrc = object.fb2scheme.description.titleInfo.coverImageSrc;
                    if ( coversrc != null) {
                        if ( object.myBinaryData.getName().equals(coversrc)) {
                            object.fb2scheme.cover = object.myBinaryData;
                        }
                    }
                    object.myBinaryData = null;
                    object.clearMyText();
                }
    }

    private static void parseEndElementInSection(FB2Elements fel, FB2ParserObject object) {
        if ( fel.equals(FB2Elements.title)) {
            if (object.mySection != null && object.mySection.title == null) {
                object.mySection.title = object.myText.toString().trim();
            }
            object.clearMyText();
        }

        if ( ! object.isSection && ! object.isAnnotation && ! object.isCoverpage && ! object.isHistory) return;

        if ( object.isSection && object.onlyscheme) return;

        String result = "";
        int deep = object.isSection ? object.mySection.deep : 0;


        switch (fel) {
            case emptyline:
            case v:
                result = "<br/>";
                break;
            case p:
            case stanza:
                if ( ! object.isTitle)
                    result = "</p>";
                else result = "<br/>";
                break;
            case cite:
                result = "</cite>";
                break;
            case title:
                object.isTitle = false;
                int d1 = Math.min(deep, FB2ParserObject.maxHeader);
                result = "</H" + (d1 + 1) + ">";
                break;
            case subtitle:
                int d2 = Math.min(deep + 1, FB2ParserObject.maxHeader);
                result = "</H" + (d2 + 1) + ">";
                break;
            case strong:
                result = "</strong>";
                break;
            case emphasis:
                result = "</em>";
                break;
            case strikethrough:
                result = "</strike>";
                break;
            case sub:
                result = "</sub>";
                break;
            case sup:
                result = "</sup>";
                break;
            case code:
                object.isCode = false;
                result = "</code></pre>";
                break;
            case epigraph:
                result = "</blockquote>";
                break;
            case a:
                if ( object.isSupNote ) {
                    result = "</sup></a>";
                    object.isSupNote = false;
                } else {
                    result = "</a>";
                }
                break;
            case image:
                break;
            default:
                java.util.logging.Logger.getLogger("FB2ENDELEMENT").log(java.util.logging.Level.INFO, "Other EndElement: " + fel.elName);
                break;
        }
        if ( object.isAnnotation ) {
            object.fb2scheme.description.titleInfo.annotation.append(result);
        } else if ( object.isCoverpage ) {
            object.fb2scheme.description.titleInfo.coverpage.append(result);
        } else if ( object.isHistory ) {
            object.fb2scheme.description.documentInfo.history.append(result);
        } else if ( object.isSection ) {
            object.mySection.text.append(result);
        } else {
            java.util.logging.Logger.getLogger("FB2ENDELEMENT")
                    .log(java.util.logging.Level.INFO, "FEL: " + fel.elName + ", Other FB2ParserObject: " + object.toString());
        }
	}

    private static void endElementDescription(FB2Elements el, FB2ParserObject object) {
        int aIndex = 0;
            switch (el) {
                case genre: {
                    if ( object.isDescriptionTitleInfo ) object.fb2scheme.getDescription().titleInfo.genre.add(object.myText.toString().trim());
                    object.clearMyText();
                    break;
                }
                case booktitle: {
                    if ( object.isDescriptionTitleInfo )object.fb2scheme.getDescription().titleInfo.booktitle = object.myText.toString().trim();
                    object.clearMyText();
                    break;
                }
                case firstname: {
                    if ( object.isDescriptionTitleInfo ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.titleInfo.authors.size()-1;
                            object.fb2scheme.description.titleInfo.authors.get(aIndex).firstname = object.myText.toString().trim();
                        } else if ( object.isTranslator) {
                            aIndex = object.fb2scheme.description.titleInfo.translators.size()-1;
                            object.fb2scheme.description.titleInfo.translators.get(aIndex).firstname = object.myText.toString().trim();
                        }
                    } else if ( object.isDescriptionDocumentInfo ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.documentInfo.authors.size()-1;
                            object.fb2scheme.description.documentInfo.authors.get(aIndex).firstname = object.myText.toString().trim();
                        } 
                    }
                    object.clearMyText();
                    break; 
                }
                case middlename: {

                    if ( object.isDescriptionTitleInfo && object.isAuthor ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.titleInfo.authors.size()-1;
                            object.fb2scheme.description.titleInfo.authors.get(aIndex).middlename = object.myText.toString().trim();
                        } else if ( object.isTranslator) {
                            aIndex = object.fb2scheme.description.titleInfo.translators.size()-1;
                            object.fb2scheme.description.titleInfo.translators.get(aIndex).middlename = object.myText.toString().trim();
                        }
                    } else if ( object.isDescriptionDocumentInfo ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.documentInfo.authors.size()-1;
                            object.fb2scheme.description.documentInfo.authors.get(aIndex).middlename = object.myText.toString().trim();
                        } 
                    }
                    object.clearMyText();
                    break;
                }
                case lastname: {
                    if ( object.isDescriptionTitleInfo && object.isAuthor ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.titleInfo.authors.size()-1;
                            object.fb2scheme.description.titleInfo.authors.get(aIndex).lastname = object.myText.toString().trim();
                        } else if ( object.isTranslator) {
                            aIndex = object.fb2scheme.description.titleInfo.translators.size()-1;
                            object.fb2scheme.description.titleInfo.translators.get(aIndex).lastname = object.myText.toString().trim();
                        }
                    }else if ( object.isDescriptionDocumentInfo ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.documentInfo.authors.size()-1;
                            object.fb2scheme.description.documentInfo.authors.get(aIndex).middlename = object.myText.toString().trim();
                        } 
                    }
                    object.clearMyText();
                    break;
                }
                case nickname: {
                    if ( object.isDescriptionTitleInfo && object.isAuthor ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.titleInfo.authors.size()-1;
                            object.fb2scheme.description.titleInfo.authors.get(aIndex).nickname = object.myText.toString().trim();
                        } else if ( object.isTranslator) {
                            aIndex = object.fb2scheme.description.titleInfo.translators.size()-1;
                            object.fb2scheme.description.titleInfo.translators.get(aIndex).nickname = object.myText.toString().trim();
                        }
                    }else if ( object.isDescriptionDocumentInfo ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.documentInfo.authors.size()-1;
                            object.fb2scheme.description.documentInfo.authors.get(aIndex).nickname = object.myText.toString().trim();
                        } 
                    }
                    object.clearMyText();
                    break;
                }
                case homepage: {
                    if ( object.isDescriptionTitleInfo && object.isAuthor ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.titleInfo.authors.size()-1;
                            object.fb2scheme.description.titleInfo.authors.get(aIndex).homepage = object.myText.toString().trim();
                        } else if ( object.isTranslator) {
                            aIndex = object.fb2scheme.description.titleInfo.translators.size()-1;
                            object.fb2scheme.description.titleInfo.translators.get(aIndex).homepage = object.myText.toString().trim();
                        }
                    }else if ( object.isDescriptionDocumentInfo ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.documentInfo.authors.size()-1;
                            object.fb2scheme.description.documentInfo.authors.get(aIndex).homepage = object.myText.toString().trim();
                        } 
                    }
                    object.clearMyText();
                    break;
                }
                case email: {
                    if ( object.isDescriptionTitleInfo && object.isAuthor ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.titleInfo.authors.size()-1;
                            object.fb2scheme.description.titleInfo.authors.get(aIndex).email = object.myText.toString().trim();
                        } else if ( object.isTranslator) {
                            aIndex = object.fb2scheme.description.titleInfo.translators.size()-1;
                            object.fb2scheme.description.titleInfo.translators.get(aIndex).email = object.myText.toString().trim();
                        }
                    }else if ( object.isDescriptionDocumentInfo ) {
                        if ( object.isAuthor ) {
                            aIndex = object.fb2scheme.description.documentInfo.authors.size()-1;
                            object.fb2scheme.description.documentInfo.authors.get(aIndex).email = object.myText.toString().trim();
                        } 
                    }
                    object.clearMyText();
                    break;
                }
                case coverpage:
                    object.isCoverpage = false;
                    break;
                case annotation:
                    object.isAnnotation = false;
                    break;
                case keywords: {
                    String[] keywords = object.myText.toString().trim().split(",");
                    for (String k : keywords) {
                        object.fb2scheme.description.titleInfo.keywords.add(k.trim());
                    }
                    object.clearMyText();
                    break;
                }
                case version:
                    if ( object.isDescriptionDocumentInfo ) object.fb2scheme.description.documentInfo.version = object.myText.toString().trim();
                    object.clearMyText();
                    break;
                case history:
                object.isHistory = false;
                    break;
                case bookname:
                    if ( object.isDescriptionPublishInfo ) object.fb2scheme.description.publishInfo.bookname = object.myText.toString().trim();
                    object.clearMyText();
                    break;
                case publisher:
                    if ( object.isDescriptionPublishInfo ) object.fb2scheme.description.publishInfo.publisher = object.myText.toString().trim();
                    object.clearMyText();
                    break;
                case city:
                    if ( object.isDescriptionPublishInfo ) object.fb2scheme.description.publishInfo.city = object.myText.toString().trim();
                    object.clearMyText();
                    break;
                case year:
                    if ( object.isDescriptionPublishInfo ) object.fb2scheme.description.publishInfo.year = object.myText.toString().trim();
                    object.clearMyText();
                    break;
                case isbn:
                    if ( object.isDescriptionPublishInfo ) object.fb2scheme.description.publishInfo.isbn = object.myText.toString().trim();
                    object.clearMyText();
                    break;
                default:
                    break;
            }
	}

}
