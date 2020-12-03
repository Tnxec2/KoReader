package com.kontranik.koreader.parser.fb2reader.model;

public enum  FB2Elements {
    a("a"),
    annotation("annotation"),
    author("author"),
    binary("binary"),
    body("body"),
    bookname("book-name"),
    booktitle("book-title"),
    cite("cite"),
    city("city"),
    code("code"),
    coverpage("coverpage"),
    custominfo("custom-info"),
    date("date"),
    description("description"),
    documentinfo("document-info"),
    email("email"),
    emphasis("emphasis"),
    emptyline("empty-line"),
    epigraph("epigraph"),
    fictionbook("FictionBook"),
    firstname("first-name"),
    genre("genre"),
    history("history"),
    homepage("home-page"),
    id("id"),
    image("image"),
    isbn("isbn"),
    keywords("keywords"),
    lang("lang"),
    lastname("last-name"),
    middlename("middle-name"),
    nickname("nickname"),
    p("p"),
    poem("poem"),
    programused("program-used"),
    publisher("publisher"),
    publishinfo("publish-info"),
    section("section"),
    sequence("sequence"),
    srclang("src-lang"),
    srcocr("src-ocr"),
    srcurl("src-url"),
    stanza("stanza"),
    strikethrough("strikethrough"),
    strong("strong"),
    style("style"),
    stylesheet("stylesheet"),
    sub("sub"),
    subtitle("subtitle"),
    sup("sup"),
    textauthor("text-author"),
    title("title"),
    titleinfo("title-info"),
    translator("translator"),
    v("v"),
    version("version"),
    year("year");

    public String elName;
    FB2Elements(String name) {
        this.elName = name;
    }

    public static FB2Elements fromString(String name) {
        for (FB2Elements b : FB2Elements.values()) {
            if (b.elName.equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }
}
