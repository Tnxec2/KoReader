package com.kontranik.koreader.parser.fb2reader

import com.kontranik.koreader.parser.fb2reader.model.FB2Elements
import com.kontranik.koreader.parser.fb2reader.model.FB2ParserObject
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.min

object FB2EndElement {
    @Throws(Exception::class)
    fun endElement(eName: String?, fel: FB2Elements, fB2ParserObject: FB2ParserObject) {
        when (fel) {
            FB2Elements.AUTHOR -> fB2ParserObject.isAuthor = false
            FB2Elements.TRANSLATOR -> fB2ParserObject.isTranslator = false
            FB2Elements.SECTION -> if (fB2ParserObject.sectionDeep > 0) {
                fB2ParserObject.sectionDeep--
            }
            FB2Elements.BODY -> {
                if (!fB2ParserObject.onlyscheme && fB2ParserObject.isSection) {
                    fB2ParserObject.fileHelper.writeSection(
                        fB2ParserObject.mySection,
                        fB2ParserObject.fb2scheme
                    )
                    fB2ParserObject.mySection = null
                }
            }
            FB2Elements.BINARY -> endElementBinary(fB2ParserObject)
            FB2Elements.description -> fB2ParserObject.isDescription = false
            FB2Elements.TITLEINFO -> fB2ParserObject.isDescriptionTitleInfo = false
            FB2Elements.DOCUMENTINFO -> fB2ParserObject.isDescriptionDocumentInfo = false
            FB2Elements.PUBLISHINFO -> fB2ParserObject.isDescriptionPublishInfo = false
            FB2Elements.CUSTOMINFO -> fB2ParserObject.isDescriptionCustomInfo = false
            else -> {
                if (fB2ParserObject.isDescription) endElementDescription(fel, fB2ParserObject)
                parseEndElementInSection(fel, fB2ParserObject)
            }
        }

        // System.out.println("Ende Element: " + eName);
    }

    @Throws(Exception::class)
    private fun endElementBinary(`object`: FB2ParserObject) {
        if ( !`object`.onlyscheme
            || `object`.fb2scheme.cover == null
            && `object`.myBinaryData != null
            && `object`.fb2scheme.description.titleInfo.coverImageSrc == `object`.myBinaryData!!.name) {
            `object`.myBinaryData!!.setBase64Encoded(`object`.myText.toString().toCharArray())
            if (!`object`.onlyscheme) `object`.fileHelper.writeBinary(`object`.myBinaryData)
            val coversrc = `object`.fb2scheme.description.titleInfo.coverImageSrc
            if (coversrc != null) {
                if (`object`.myBinaryData!!.name == coversrc) {
                    `object`.fb2scheme.cover = `object`.myBinaryData
                }
            }
            `object`.myBinaryData = null
            `object`.clearMyText()
        }
    }

    private fun parseEndElementInSection(fel: FB2Elements, fB2ParserObject: FB2ParserObject) {
        if (fel == FB2Elements.TITLE) {
            if (fB2ParserObject.mySection != null
                && fB2ParserObject.mySection!!.title == null) {
                fB2ParserObject.mySection!!.title =
                    fB2ParserObject.myText.toString().trim { it <= ' ' }
            }
            fB2ParserObject.clearMyText()
        }
        if (!fB2ParserObject.isSection
            && !fB2ParserObject.isAnnotation
            && !fB2ParserObject.isCoverpage
            && !fB2ParserObject.isHistory) return
        if (fB2ParserObject.isSection && fB2ParserObject.onlyscheme) return
        var result = ""
        val deep = if (fB2ParserObject.isSection) fB2ParserObject.mySection!!.deep!! else 0
        when (fel) {
            FB2Elements.EMPTYLINE, FB2Elements.V -> result = "<br/>"
            FB2Elements.P, FB2Elements.STANZA -> result =
                if (!fB2ParserObject.isTitle) "</p>" else "<br/>"
            FB2Elements.CITE -> result = "</cite>"
            FB2Elements.TITLE -> {
                fB2ParserObject.isTitle = false
                val d1 = min(deep, FB2ParserObject.maxHeader)
                result = "</H" + (d1 + 1) + ">"
            }
            FB2Elements.SUBTITLE -> {
                val d2 = min(deep + 1, FB2ParserObject.maxHeader)
                result = "</H" + (d2 + 1) + ">"
            }
            FB2Elements.STRONG -> result = "</strong>"
            FB2Elements.EMPHASIS -> result = "</em>"
            FB2Elements.STRIKETHROUGH -> result = "</strike>"
            FB2Elements.SUB -> result = "</sub>"
            FB2Elements.SUP -> result = "</sup>"
            FB2Elements.CODE -> {
                fB2ParserObject.isCode = false
                result = "</code></pre>"
            }
            FB2Elements.EPIGRAPH -> result = "</blockquote>"
            FB2Elements.A -> if (fB2ParserObject.isSupNote) {
                result = "</sup></a>"
                fB2ParserObject.isSupNote = false
            } else {
                result = "</a>"
            }
            FB2Elements.IMAGE -> {
            }
            else -> Logger.getLogger("FB2ENDELEMENT")
                .log(Level.INFO, "Other EndElement: " + fel.elName)
        }
        when {
            fB2ParserObject.isAnnotation -> {
                fB2ParserObject.fb2scheme.description.titleInfo.annotation.append(result)
            }
            fB2ParserObject.isCoverpage -> {
                fB2ParserObject.fb2scheme.description.titleInfo.coverpage.append(result)
            }
            fB2ParserObject.isHistory -> {
                fB2ParserObject.fb2scheme.description.documentInfo.history.append(result)
            }
            fB2ParserObject.isSection -> {
                fB2ParserObject.mySection!!.text.append(result)
            }
            else -> {
                Logger.getLogger("FB2ENDELEMENT")
                    .log(
                        Level.INFO,
                        "FEL: " + fel.elName + ", Other FB2ParserObject: " + fB2ParserObject.toString()
                    )
            }
        }
    }

    private fun endElementDescription(el: FB2Elements, `object`: FB2ParserObject) {
        val aIndex: Int
        when (el) {
            FB2Elements.GENRE -> {
                if (`object`.isDescriptionTitleInfo) `object`.fb2scheme.description.titleInfo.genre.add(
                    `object`.myText.toString().trim { it <= ' ' })
                `object`.clearMyText()
            }
            FB2Elements.BOOKTITLE -> {
                if (`object`.isDescriptionTitleInfo) `object`.fb2scheme.description.titleInfo.booktitle =
                    `object`.myText.toString().trim { it <= ' ' }
                `object`.clearMyText()
            }
            FB2Elements.FIRSTNAME -> {
                if (`object`.isDescriptionTitleInfo) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.titleInfo.authors.size - 1
                        `object`.fb2scheme.description.titleInfo.authors[aIndex].firstname =
                            `object`.myText.toString().trim { it <= ' ' }
                    } else if (`object`.isTranslator) {
                        aIndex = `object`.fb2scheme.description.titleInfo.translators.size - 1
                        `object`.fb2scheme.description.titleInfo.translators[aIndex].firstname =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                } else if (`object`.isDescriptionDocumentInfo) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.documentInfo.authors.size - 1
                        `object`.fb2scheme.description.documentInfo.authors[aIndex].firstname =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                }
                `object`.clearMyText()
            }
            FB2Elements.MIDDLENAME -> {
                if (`object`.isDescriptionTitleInfo && `object`.isAuthor) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.titleInfo.authors.size - 1
                        `object`.fb2scheme.description.titleInfo.authors[aIndex].middlename =
                            `object`.myText.toString().trim { it <= ' ' }
                    } else if (`object`.isTranslator) {
                        aIndex = `object`.fb2scheme.description.titleInfo.translators.size - 1
                        `object`.fb2scheme.description.titleInfo.translators[aIndex].middlename =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                } else if (`object`.isDescriptionDocumentInfo) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.documentInfo.authors.size - 1
                        `object`.fb2scheme.description.documentInfo.authors[aIndex].middlename =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                }
                `object`.clearMyText()
            }
            FB2Elements.LASTNAME -> {
                if (`object`.isDescriptionTitleInfo && `object`.isAuthor) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.titleInfo.authors.size - 1
                        `object`.fb2scheme.description.titleInfo.authors[aIndex].lastname =
                            `object`.myText.toString().trim { it <= ' ' }
                    } else if (`object`.isTranslator) {
                        aIndex = `object`.fb2scheme.description.titleInfo.translators.size - 1
                        `object`.fb2scheme.description.titleInfo.translators[aIndex].lastname =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                } else if (`object`.isDescriptionDocumentInfo) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.documentInfo.authors.size - 1
                        `object`.fb2scheme.description.documentInfo.authors[aIndex].middlename =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                }
                `object`.clearMyText()
            }
            FB2Elements.NICKNAME -> {
                if (`object`.isDescriptionTitleInfo && `object`.isAuthor) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.titleInfo.authors.size - 1
                        `object`.fb2scheme.description.titleInfo.authors[aIndex].nickname =
                            `object`.myText.toString().trim { it <= ' ' }
                    } else if (`object`.isTranslator) {
                        aIndex = `object`.fb2scheme.description.titleInfo.translators.size - 1
                        `object`.fb2scheme.description.titleInfo.translators[aIndex].nickname =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                } else if (`object`.isDescriptionDocumentInfo) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.documentInfo.authors.size - 1
                        `object`.fb2scheme.description.documentInfo.authors[aIndex].nickname =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                }
                `object`.clearMyText()
            }
            FB2Elements.HOMEPAGE -> {
                if (`object`.isDescriptionTitleInfo && `object`.isAuthor) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.titleInfo.authors.size - 1
                        `object`.fb2scheme.description.titleInfo.authors[aIndex].homepage =
                            `object`.myText.toString().trim { it <= ' ' }
                    } else if (`object`.isTranslator) {
                        aIndex = `object`.fb2scheme.description.titleInfo.translators.size - 1
                        `object`.fb2scheme.description.titleInfo.translators[aIndex].homepage =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                } else if (`object`.isDescriptionDocumentInfo) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.documentInfo.authors.size - 1
                        `object`.fb2scheme.description.documentInfo.authors[aIndex].homepage =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                }
                `object`.clearMyText()
            }
            FB2Elements.EMAIL -> {
                if (`object`.isDescriptionTitleInfo && `object`.isAuthor) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.titleInfo.authors.size - 1
                        `object`.fb2scheme.description.titleInfo.authors[aIndex].email =
                            `object`.myText.toString().trim { it <= ' ' }
                    } else if (`object`.isTranslator) {
                        aIndex = `object`.fb2scheme.description.titleInfo.translators.size - 1
                        `object`.fb2scheme.description.titleInfo.translators[aIndex].email =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                } else if (`object`.isDescriptionDocumentInfo) {
                    if (`object`.isAuthor) {
                        aIndex = `object`.fb2scheme.description.documentInfo.authors.size - 1
                        `object`.fb2scheme.description.documentInfo.authors[aIndex].email =
                            `object`.myText.toString().trim { it <= ' ' }
                    }
                }
                `object`.clearMyText()
            }
            FB2Elements.COVERPAGE -> `object`.isCoverpage = false
            FB2Elements.ANNOTATION -> `object`.isAnnotation = false
            FB2Elements.KEYWORDS -> {
                val keywords = `object`.myText.toString().trim { it <= ' ' }.split(",".toRegex())
                    .toTypedArray()
                for (k in keywords) {
                    `object`.fb2scheme.description.titleInfo.keywords.add(k.trim { it <= ' ' })
                }
                `object`.clearMyText()
            }
            FB2Elements.VERSION -> {
                if (`object`.isDescriptionDocumentInfo) `object`.fb2scheme.description.documentInfo.version =
                    `object`.myText.toString().trim { it <= ' ' }
                `object`.clearMyText()
            }
            FB2Elements.HISTORY -> `object`.isHistory = false
            FB2Elements.BOOKNAME -> {
                if (`object`.isDescriptionPublishInfo) `object`.fb2scheme.description.publishInfo.bookname =
                    `object`.myText.toString().trim { it <= ' ' }
                `object`.clearMyText()
            }
            FB2Elements.PUBLISHER -> {
                if (`object`.isDescriptionPublishInfo) `object`.fb2scheme.description.publishInfo.publisher =
                    `object`.myText.toString().trim { it <= ' ' }
                `object`.clearMyText()
            }
            FB2Elements.city -> {
                if (`object`.isDescriptionPublishInfo) `object`.fb2scheme.description.publishInfo.city =
                    `object`.myText.toString().trim { it <= ' ' }
                `object`.clearMyText()
            }
            FB2Elements.YEAR -> {
                if (`object`.isDescriptionPublishInfo) `object`.fb2scheme.description.publishInfo.year =
                    `object`.myText.toString().trim { it <= ' ' }
                `object`.clearMyText()
            }
            FB2Elements.ISBN -> {
                if (`object`.isDescriptionPublishInfo) `object`.fb2scheme.description.publishInfo.isbn =
                    `object`.myText.toString().trim { it <= ' ' }
                `object`.clearMyText()
            }
            else -> {
            }
        }
    }
}