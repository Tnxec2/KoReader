package com.kontranik.koreader.parser.fb2reader.model

enum class FB2Elements(var elName: String) {
    A("a"),
    ANNOTATION("annotation"),
    AUTHOR("author"),
    BINARY("binary"),
    BODY("body"),
    BOOKNAME("book-name"),
    BOOKTITLE("book-title"),
    CITE("cite"), city("city"),
    CODE("code"),
    COVERPAGE("coverpage"),
    CUSTOMINFO("custom-info"),
    DATE("date"), description("description"),
    DOCUMENTINFO("document-info"),
    EMAIL("email"),
    EMPHASIS("emphasis"),
    EMPTYLINE("empty-line"),
    EPIGRAPH("epigraph"),
    FICTIONBOOK("FictionBook"),
    FIRSTNAME("first-name"),
    GENRE("genre"),
    HISTORY("history"),
    HOMEPAGE("home-page"),
    ID("id"),
    IMAGE("image"),
    ISBN("isbn"),
    KEYWORDS("keywords"),
    LANG("lang"),
    LASTNAME("last-name"),
    MIDDLENAME("middle-name"),
    NICKNAME("nickname"),
    P("p"), poem("poem"),
    PROGRAMUSED("program-used"),
    PUBLISHER("publisher"),
    PUBLISHINFO("publish-info"),
    SECTION("section"),
    SEQUENCE("sequence"),
    SRCLANG("src-lang"),
    SRCOCR("src-ocr"),
    SRCURL("src-url"),
    STANZA("stanza"),
    STRIKETHROUGH("strikethrough"),
    STRONG("strong"),
    STYLE("style"),
    STYLESHEET("stylesheet"),
    SUB("sub"),
    SUBTITLE("subtitle"),
    SUP("sup"),
    TEXTAUTHOR("text-author"),
    TITLE("title"),
    TITLEINFO("title-info"),
    TRANSLATOR("translator"),
    V("v"),
    VERSION("version"),
    YEAR("year");

    companion object {
        fun fromString(name: String?): FB2Elements? {
            for (b in values()) {
                if (b.elName.equals(name, ignoreCase = true)) {
                    return b
                }
            }
            return null
        }
    }
}
