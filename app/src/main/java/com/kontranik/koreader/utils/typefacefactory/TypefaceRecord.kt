package com.kontranik.koreader.utils.typefacefactory

import android.graphics.Typeface
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.R
import java.io.File
import java.util.*


/**
 * Stores information about a [Typeface].
 */
data class TypefaceRecord(
        val name: String,
        val file: File? = null,
        var otherNames: Set<String?>? = null) {
    constructor(name: String, filePath: String?) : this(name, filePath?.let { File(it) })

    fun getTypeface(style: Int = Typeface.NORMAL): Typeface {
        if (file != null) return Typeface.createFromFile(file)
        return fonts[name] ?: Typeface.create(name, style)
    }

    override fun toString(): String {
        return Formatter().format(
                "TypefaceRecord{name=%s,typeface=%s,otherNames=%s}",
                name, otherNames)
                .toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypefaceRecord

        if (name != other.name) return false
        if (file != other.file) return false
        if (otherNames != other.otherNames) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (file?.hashCode() ?: 0)
        result = 31 * result + (otherNames?.hashCode() ?: 0)
        return result
    }

    companion object {
        /**
         * The TypefaceRecord for [Typeface.DEFAULT]. This object's
         * [TypefaceRecord.getOtherNames] may not be complete.
         */

        const val SANSSERIF = "sans-serif"
        const val SERIF = "serif"
        const val MONO = "monospace"

        val DEFAULT = TypefaceRecord(SANSSERIF)

        val fonts = mapOf(
            "amazon ember" to KoReaderApplication.getContext().resources.getFont(R.font.amazon_ember_regular),
            "amazon ember bold" to KoReaderApplication.getContext().resources.getFont(R.font.amazon_ember_bold),
            "amazon ember italic" to KoReaderApplication.getContext().resources.getFont(R.font.amazon_ember_italic),
            "amazon ember bold italic" to KoReaderApplication.getContext().resources.getFont(R.font.amazon_ember_bold_italic),
            "amazon ember mono" to KoReaderApplication.getContext().resources.getFont(R.font.amazon_ember_mono_reg),
            "bookerly regular" to KoReaderApplication.getContext().resources.getFont(R.font.bookerly_regular),
            "bookerly bold" to KoReaderApplication.getContext().resources.getFont(R.font.bookerly_bold),
            "bookerly italic" to KoReaderApplication.getContext().resources.getFont(R.font.bookerly_italic),
            "bookerly bold italic" to KoReaderApplication.getContext().resources.getFont(R.font.bookerly_bold_italic),
        )
    }
}