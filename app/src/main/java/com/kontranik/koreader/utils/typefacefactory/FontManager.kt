package com.kontranik.koreader.utils.typefacefactory

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

object FontManager {
    //internal val analyzer = TTFAnalyzer()

    // This function enumerates all fonts on Android system and returns the HashMap with the font
    // absolute file name as key, and the font literal name (embedded into the font) as value.
    fun enumerateFonts(showSystemFonts: Boolean, showNotoFonts: Boolean): HashMap<String, File>? {
        val fontdirs: MutableList<String> = mutableListOf()
        if ( Environment.getExternalStorageDirectory() != null) {
            fontdirs.add(Environment.getExternalStorageDirectory().absolutePath + "/fonts")
            fontdirs.add(Environment.getExternalStorageDirectory().absolutePath + "/Fonts")
        }
        if ( showSystemFonts ) fontdirs.addAll(listOf("/system/fonts", "/system/font", "/data/fonts"))

        val fonts = HashMap<String, File>()

        for (fontDir in fontdirs) {
            val dir = File(fontDir)
            if (!dir.exists()) continue
            fonts.putAll(addFonts(dir, showSystemFonts, showNotoFonts))
        }
        return if (fonts.isEmpty()) null else fonts
    }

    private fun addFonts(dir: File, showSystemFonts: Boolean, showNotoFonts: Boolean): HashMap<String, File> {
        val fonts = HashMap<String, File>()
        val files = dir.listFiles() ?: return hashMapOf()
        for (file in files) {
            if ( file.isDirectory ) {
                fonts.putAll(addFonts(file, showSystemFonts, showNotoFonts))
            } else if ( file.canRead() ) {
                val fontname = file.name.substringBeforeLast('.')
                        .replace("_", " ")
                        .replace("-", " ")
                if (!showNotoFonts && fontname.startsWith("noto", ignoreCase = true) ) continue
                fonts[fontname] = file
            }
        }
        return fonts
    }

}