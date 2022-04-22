package com.kontranik.koreader.parser.fb2reader.parser.model

import com.kontranik.koreader.parser.fb2reader.parser.FileHelper

class FB2ParserObject(val fileHelper: FileHelper) {
    var fb2scheme = FB2Scheme()
    var mySection: FB2Section? = null
    var sectionid: Int = 0
    var sectionDeep: Int = 0
    var myBinaryData: BinaryData? = null
    var myParseText = false
    var myText: StringBuffer = StringBuffer()
    var lastElement: FB2Elements? = null
    var isDescription = false
    var isDescriptionCustomInfo = false
    var isDescriptionPublishInfo = false
    var isDescriptionDocumentInfo = false
    var isDescriptionTitleInfo = false
    var isCoverpage = false
    var isAnnotation = false
    var isAuthor = false
    var isTranslator = false
    var isHistory = false
    var isSection = false
    var isNotes = false
    var onlyscheme = false
    var isSupNote = false
    var isTitle = false
    var isBinary = false
    var isCode = false

    fun clearMyText() {
        myText = StringBuffer()
        myParseText = false
    }

    override fun toString(): String {
        return "FB2ParserObject{" +
                "mySection=" + mySection +
                ", sectionid=" + sectionid +
                ", sectionDeep=" + sectionDeep +
                ", myParseText=" + myParseText +
                ", myText=" + myText +
                ", isDescription=" + isDescription +
                ", isDescriptionCustomInfo=" + isDescriptionCustomInfo +
                ", isDescriptionPublishInfo=" + isDescriptionPublishInfo +
                ", isDescriptionDocumentInfo=" + isDescriptionDocumentInfo +
                ", isDescriptionTitleInfo=" + isDescriptionTitleInfo +
                ", isCoverpage=" + isCoverpage +
                ", isAnnotation=" + isAnnotation +
                ", isAuthor=" + isAuthor +
                ", isTranslator=" + isTranslator +
                ", isHistory=" + isHistory +
                ", isSection=" + isSection +
                ", isNotes=" + isNotes +
                ", onlyscheme=" + onlyscheme +
                ", isSupNote=" + isSupNote +
                ", isTitle=" + isTitle +
                ", isBinary=" + isBinary +
                ", isCode=" + isCode +
                '}'
    }

    fun clear() {
        mySection = null
        sectionid = 0
        sectionDeep = 0
        myParseText = false
        myText = StringBuffer()
        isDescription = false
        isDescriptionCustomInfo = false
        isDescriptionPublishInfo = false
        isDescriptionDocumentInfo = false
        isDescriptionTitleInfo = false
        isCoverpage = false
        isAnnotation = false
        isAuthor = false
        isTranslator = false
        isHistory = false
        isSection = false
        isNotes = false
        isSupNote = false
        isTitle = false
        isBinary = false
        isCode = false
    }

    companion object {
        const val maxHeader = 5 // 0 bis 5
        const val SectionReachedException = "SectionReachedException"
    }
}