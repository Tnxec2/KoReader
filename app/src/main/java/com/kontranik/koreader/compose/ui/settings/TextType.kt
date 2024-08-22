package com.kontranik.koreader.compose.ui.settings


enum class TextType {
    Bold,
    Italic,
    BoldItalic,
    Normal,
    Monospace;

    companion object {
        fun fromString(s: String): TextType {
            return when (s) {
                "monospace", "mono" -> {
                    Monospace
                }
                "bold" -> {
                    Bold
                }
                "italic" -> {
                    Italic
                }
                "bolditalic" -> {
                    BoldItalic
                }
                else -> {
                    Normal
                }
            }
        }
    }
}