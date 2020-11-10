package com.kontranik.koreader.model

import android.util.Log
import org.jsoup.nodes.Element

enum class MyStyle {
    Cite, Poem, Section, Image, None, Paragraph, Title, Subtitle, EmptyLine, Elements, Epigraph, H1, H2, H3, H4, H5, H6, Bold, Italic, small, Other;

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

        fun parseAElement(abstractElement: AbstractElement): MyStyle {
            // TODO: ...

            return Paragraph
        }

        fun parseJsoupElement(jsoupElement: Element): MyStyle {
            val parents = jsoupElement.parents()
            val classes: MutableList<String> = mutableListOf()
            for ( i in parents.size-1 downTo 0) {
                val tag = parents[i].tagName()
                val cl = parents[i].classNames().map { it.toLowerCase() }
                classes.addAll(cl)
            }
            if ( classes.contains("title")) return Title
            if ( classes.contains("title1")) return H1
            if ( classes.contains("title2")) return H2
            if ( classes.contains("title3")) return H3
            if ( classes.contains("epigraph")) return Epigraph
            Log.d("MyStyle", "Element: " + jsoupElement.ownText())
            Log.d("MyStyle", "Classes: " + classes.joinToString(" "))
            return getFromString(jsoupElement.tagName())
        }
    }
}