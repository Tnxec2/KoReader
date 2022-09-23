package com.kontranik.koreader.parser.fb2reader.parser

import com.kontranik.koreader.model.BookPageScheme
import com.kontranik.koreader.parser.fb2reader.parser.model.*
import org.xml.sax.Attributes
import kotlin.math.min

/**
 * FB2StartElement
 */
object FB2StartElement {
    @Throws(Exception::class)
    fun startElement(
        eName: String,
        fel: FB2Elements,
        attrs: Attributes,
        `object`: FB2ParserObject
    ) {
        when (fel) {
            FB2Elements.SECTION, FB2Elements.BODY -> gotNewSection(eName, fel, attrs, `object`)
            FB2Elements.BINARY -> startBinarys(`object`, attrs)
            FB2Elements.description -> `object`.isDescription = true
            else -> {
                if (`object`.isDescription) startElementDescription(fel, attrs, `object`)
                parseStartElement(fel, attrs, `object`)
            }
        }
    }

    @Throws(Exception::class)
    private fun parseStartElement(fel: FB2Elements, attrs: Attributes, fB2ParserObject: FB2ParserObject) {
        if (fel == FB2Elements.TITLE) {
            fB2ParserObject.myParseText = true
        }
        if (!fB2ParserObject.isSection
            && !fB2ParserObject.isAnnotation
            && !fB2ParserObject.isCoverpage
            && !fB2ParserObject.isHistory
        ) return
        if (fB2ParserObject.isSection && fB2ParserObject.onlyscheme) return
        var result = ""
        val deep = if (fB2ParserObject.isSection && fB2ParserObject.mySection?.deep != null) fB2ParserObject.mySection!!.deep!! else 0
        if (fB2ParserObject.isSection) {
            if (fB2ParserObject.mySection!!.text.length > BookPageScheme.CHAR_PER_PAGE * BookPageScheme.MAX_PAGE_PER_SECTION) {
                gotNewSection(fB2ParserObject)
            }
        }
        var href: String? = null
        for (a in 0 until attrs.length) {
            val aName = attrs.getQName(a)
            if (aName.contains(":href")) {
                href = attrs.getValue(aName)
                if (href.startsWith("#")) href = href.substring(1)
                break
            }
        }
        when (fel) {
            FB2Elements.EMPTYLINE -> {
            }
            FB2Elements.P, FB2Elements.STANZA -> if (!fB2ParserObject.isTitle) result = "<p>"
            FB2Elements.CITE -> result = "<cite>"
            FB2Elements.TITLE -> {
                fB2ParserObject.isTitle = true
                val d = min(deep, FB2ParserObject.maxHeader)
                result = "<H" + (d + 1) + ">"
            }
            FB2Elements.SUBTITLE -> {
                val s = min(deep + 1, FB2ParserObject.maxHeader)
                result = "<H" + (s + 1) + ">"
            }
            FB2Elements.STRONG -> result = "<strong>"
            FB2Elements.EMPHASIS -> result = "<em>"
            FB2Elements.STRIKETHROUGH -> result = "<strike>"
            FB2Elements.SUB -> result = "<sub>"
            FB2Elements.SUP -> result = "<sup>"
            FB2Elements.CODE -> {
                fB2ParserObject.isCode = true
                result = "<pre><code>"
            }
            FB2Elements.EPIGRAPH -> result = "<blockquote>"
            FB2Elements.A -> if (href != null) {
                var cl = ""
                var linktype = ""
                fB2ParserObject.isSupNote = false
                if ("note" == attrs.getValue("type")) {
                    cl = "<sup>"
                    linktype = "class=\"note\""
                    fB2ParserObject.isSupNote = true
                }
                result = " <a href=\"$href\" $linktype >$cl"
            }
            FB2Elements.IMAGE -> if (href != null) result = "<img src=\"$href\">"
            FB2Elements.TABLE -> result = "<p><tt>"
            FB2Elements.TH -> result = "<strong>"
            else -> {
            }
        }
        if (fB2ParserObject.isAnnotation) {
            fB2ParserObject.fb2scheme.description.titleInfo.annotation.append(result)
        } else if (fB2ParserObject.isCoverpage) {
            fB2ParserObject.fb2scheme.description.titleInfo.coverpage.append(result)
            if (fel == FB2Elements.IMAGE && href != null) {
                fB2ParserObject.fb2scheme.description.titleInfo.coverImageSrc = href
            }
        } else if (fB2ParserObject.isHistory) {
            fB2ParserObject.fb2scheme.description.documentInfo.history.append(result)
        } else if (fB2ParserObject.isSection) {
            fB2ParserObject.mySection!!.text.append(result)
        }
    }

    @Throws(Exception::class)
    private fun gotNewSection(fB2ParserObject: FB2ParserObject) {
        if (fB2ParserObject.mySection != null) {
            fB2ParserObject.fileHelper.writeSection(
                fB2ParserObject.mySection,
                fB2ParserObject.fb2scheme
            )
            fB2ParserObject.isSection = true
            val parentid =
                if (fB2ParserObject.mySection != null) fB2ParserObject.mySection!!.orderid else null
            fB2ParserObject.mySection = FB2Section(
                fB2ParserObject.sectionid,
                fB2ParserObject.mySection!!.id,
                fB2ParserObject.mySection!!.typ,
                fB2ParserObject.sectionDeep,
                parentid,
                isNote = fB2ParserObject.isNotes
            )
            fB2ParserObject.fb2scheme.sections.add(fB2ParserObject.mySection!!)
            // System.out.printf("New Section Id: " + object.sectionid + ", deep: " + object.sectionDeep + ", Name: " + el.elName + "\n");
            fB2ParserObject.sectionid++
            fB2ParserObject.sectionDeep++
        }
    }

    @Throws(Exception::class)
    private fun gotNewSection(
        eName: String,
        el: FB2Elements,
        attrs: Attributes,
        fB2ParserObject: FB2ParserObject
    ) {
        if (!fB2ParserObject.onlyscheme && fB2ParserObject.isSection) {
            fB2ParserObject.fileHelper.writeSection(
                fB2ParserObject.mySection,
                fB2ParserObject.fb2scheme
            )
        }
        val aName = attrs.getValue("name")
        if (el == FB2Elements.BODY) fB2ParserObject.isNotes = ("notes" == aName)
        if (el == FB2Elements.BODY) fB2ParserObject.isComments = ("comments" == aName)

        fB2ParserObject.isSection = true
        val parentid = if (fB2ParserObject.mySection != null) fB2ParserObject.mySection!!.orderid else null
        fB2ParserObject.mySection = FB2Section(
            fB2ParserObject.sectionid,
            attrs.getValue("id"),
            el,
            fB2ParserObject.sectionDeep,
            parentid,
            isNote = fB2ParserObject.isNotes,
            isComment = fB2ParserObject.isComments
        )
        fB2ParserObject.fb2scheme.sections.add(fB2ParserObject.mySection!!)
        // System.out.printf("New Section Id: " + object.sectionid + ", deep: " + object.sectionDeep + ", Name: " + el.elName + "\n");
        fB2ParserObject.sectionid++
        fB2ParserObject.sectionDeep++
    }

    private fun startElementDescription(
        el: FB2Elements,
        attrs: Attributes,
        fB2ParserObject: FB2ParserObject
    ) {
        when (el) {
            FB2Elements.TITLEINFO -> fB2ParserObject.isDescriptionTitleInfo = true
            FB2Elements.DOCUMENTINFO -> fB2ParserObject.isDescriptionDocumentInfo = true
            FB2Elements.PUBLISHINFO -> fB2ParserObject.isDescriptionPublishInfo = true
            FB2Elements.CUSTOMINFO -> fB2ParserObject.isDescriptionCustomInfo = true
            FB2Elements.AUTHOR -> {
                if (fB2ParserObject.isDescriptionTitleInfo) fB2ParserObject.fb2scheme.description.titleInfo.authors.add(
                    Author()
                )
                if (fB2ParserObject.isDescriptionDocumentInfo) fB2ParserObject.fb2scheme.description.documentInfo.authors.add(
                    Author()
                )
                fB2ParserObject.isAuthor = true
            }
            FB2Elements.TRANSLATOR -> {
                if (fB2ParserObject.isDescriptionTitleInfo)
                    fB2ParserObject.fb2scheme.description.titleInfo.translators.add(
                        Author()
                    )
                fB2ParserObject.isTranslator = true
            }
            FB2Elements.BOOKTITLE,
            FB2Elements.FIRSTNAME,
            FB2Elements.MIDDLENAME,
            FB2Elements.LASTNAME,
            FB2Elements.NICKNAME,
            FB2Elements.HOMEPAGE,
            FB2Elements.EMAIL,
            FB2Elements.GENRE,
            FB2Elements.BOOKNAME,
            FB2Elements.PUBLISHER,
            FB2Elements.city,
            FB2Elements.YEAR,
            FB2Elements.ISBN,
            FB2Elements.KEYWORDS,
            FB2Elements.VERSION -> fB2ParserObject.myParseText = true
            FB2Elements.SEQUENCE -> if (fB2ParserObject.isDescriptionTitleInfo) {
                fB2ParserObject.fb2scheme.description.titleInfo.sequenceName =
                    attrs.getValue("name")
                fB2ParserObject.fb2scheme.description.titleInfo.sequenceNumber =
                    attrs.getValue("number")
            }
            FB2Elements.DATE -> if (fB2ParserObject.isDescriptionTitleInfo) {
                fB2ParserObject.fb2scheme.description.titleInfo.date = attrs.getValue("value")
            }
            FB2Elements.ANNOTATION -> fB2ParserObject.isAnnotation = true
            FB2Elements.COVERPAGE -> fB2ParserObject.isCoverpage = true
            FB2Elements.HISTORY -> fB2ParserObject.isHistory = true
            else -> {
            }
        }
    }

    @Throws(Exception::class)
    private fun startBinarys(`object`: FB2ParserObject, attrs: Attributes) {
        if (`object`.isSection) {
            `object`.isSection = false
        }
        `object`.isBinary = true
        val id = attrs.getValue("id")
        if ( !`object`.onlyscheme
            || `object`.fb2scheme.cover == null
            && `object`.fb2scheme.description.titleInfo.coverImageSrc == id) {
            `object`.myBinaryData = BinaryData(id, attrs.getValue("content-type"))
            `object`.myParseText = true
        }
    }
}