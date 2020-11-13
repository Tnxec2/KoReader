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
        val DEFAULT = TypefaceRecord(SANSSERIF, null, emptySet())
    }
}