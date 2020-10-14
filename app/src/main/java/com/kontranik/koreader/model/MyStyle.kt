package com.kontranik.koreader.model

enum class MyStyle {
    None, Paragraph, Title, H1, H2, H3, H4, H5, H6, Bold, Italic, small, Other;

    companion object {
        fun getFromString(styleString: String): MyStyle {
            return when (styleString.trim { it <= ' ' }.toLowerCase()) {
                "body", "div", "br", "span" -> None
                "h1" -> H1
                "h2" -> H2
                "h3" -> H3
                "h4" -> H4
                "h5" -> H5
                "h6" -> H6
                "strong", "bold" -> Bold
                "em", "italic" -> Italic
                "p" -> Paragraph
                else -> Other
            }
        }
    }
}