package com.kontranik.koreader.utils.typefacefactory

import android.graphics.Typeface
import java.io.File
import java.util.*


/**
 * Stores information about a [Typeface].
 */
class TypefaceRecord(
        val name: String,
        val file: File? = null,
        var otherNames: Set<String?>? = null) {

    fun getTypeface(): Typeface {
        if ( file != null ) return Typeface.createFromFile(file)
        else return Typeface.create(name, Typeface.NORMAL)
    }

    override fun toString(): String {
        return Formatter().format(
                "TypefaceRecord{name=%s,typeface=%s,otherNames=%s}",
                name, otherNames)
                .toString()
    }

    companion object {
        /**
         * The TypefaceRecord for [Typeface.DEFAULT]. This object's
         * [TypefaceRecord.getOtherNames] may not be complete.
         */

        const val SANSSERIF = "sans-serif"
        const val SERIF = "serif"
        const val MONO = "monospace"
        val DEFAULT = TypefaceRecord(SANSSERIF, null, emptySet())
    }
}