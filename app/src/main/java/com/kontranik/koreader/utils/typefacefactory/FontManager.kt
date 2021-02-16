package com.kontranik.koreader.utils.typefacefactory

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*

object FontManager {
    internal val analyzer = TTFAnalyzer()

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
                var fontname = analyzer.getTtfFontName(file.absolutePath)
                Log.d("FontManager", "fontname: " + fontname)
                if (fontname == null) fontname = file.name.substringBeforeLast('.')
                if (!showNotoFonts && fontname.startsWith("noto", ignoreCase = true) ) continue
                fonts[fontname] = file
            }
        }
        return fonts
    }
}

// The class which loads the TTF file, parses it and returns the TTF font name
internal class TTFAnalyzer {
    // This function parses the TTF file and returns the font name specified in the file
    fun getTtfFontName(fontFilename: String?): String? {
        return try {
            // Parses the TTF file format.
            // See http://developer.apple.com/fonts/ttrefman/rm06/Chap6.html
            m_file = RandomAccessFile(fontFilename, "r")

            // Read the version first
            val version = readDword()

            // The version must be either 'true' (0x74727565) or 0x00010000
            // if (version != 0x74727565 && version != 0x00010000) return null
            // The version must be either 'true' (0x74727565) or 0x00010000 or 'OTTO' (0x4f54544f) for CFF style fonts.
            if ( version != 0x74727565 && version != 0x00010000 && version != 0x4f54544f) return null

            // The TTF file consist of several sections called "tables", and we need to know how many of them are there.
            val numTables = readWord()

            // Skip the rest in the header
            readWord() // skip searchRange
            readWord() // skip entrySelector
            readWord() // skip rangeShift

            // Now we can read the tables
            for (i in 0 until numTables) {
                // Read the table entry
                val tag = readDword()
                readDword() // skip checksum
                val offset = readDword()
                val length = readDword()

                // Now here' the trick. 'name' field actually contains the textual string name.
                // So the 'name' string in characters equals to 0x6E616D65
                if (tag == 0x6E616D65) {
                    // Here's the name section. Read it completely into the allocated buffer
                    val table = ByteArray(length)
                    m_file!!.seek(offset.toLong())
                    read(table)

                    // This is also a table. See http://developer.apple.com/fonts/ttrefman/rm06/Chap6name.html
                    // According to Table 36, the total number of table records is stored in the second word, at the offset 2.
                    // Getting the count and string offset - remembering it's big endian.
                    val count = getWord(table, 2)
                    val string_offset = getWord(table, 4)

                    val s = String(table)

                    // Record starts from offset 6
                    for (record in 0 until count) {
                        // Table 37 tells us that each record is 6 words -> 12 bytes, and that the nameID is 4th word so its offset is 6.
                        // We also need to account for the first 6 bytes of the header above (Table 36), so...
                        val nameid_offset = record * 12 + 6
                        val platformID = getWord(table, nameid_offset)
                        val nameid_value = getWord(table, nameid_offset + 6)

                        // Table 42 lists the valid name Identifiers. We're interested in 4 but not in Unicode encoding (for simplicity).
                        // The encoding is stored as PlatformID and we're interested in Mac encoding
                        if (nameid_value == 4 && platformID == 1) {
                            // We need the string offset and length, which are the word 6 and 5 respectively
                            val name_length = getWord(table, nameid_offset + 8)
                            var name_offset = getWord(table, nameid_offset + 10)

                            // The real name string offset is calculated by adding the string_offset
                            name_offset = name_offset + string_offset

                            // Make sure it is inside the array
                            if (name_offset >= 0 && name_offset + name_length < table.size) return String(table, name_offset, name_length)
                        }
                    }
                }
            }
            null
        } catch (e: FileNotFoundException) {
            // Permissions?
            null
        } catch (e: IOException) {
            // Most likely a corrupted font file
            null
        }
    }

    // Font file; must be seekable
    private var m_file: RandomAccessFile? = null

    // Helper I/O functions
    @Throws(IOException::class)
    private fun readByte(): Int {
        return m_file!!.read() and 0xFF
    }

    @Throws(IOException::class)
    private fun readWord(): Int {
        val b1 = readByte()
        val b2 = readByte()
        return b1 shl 8 or b2
    }

    @Throws(IOException::class)
    private fun readDword(): Int {
        val b1 = readByte()
        val b2 = readByte()
        val b3 = readByte()
        val b4 = readByte()
        return b1 shl 24 or (b2 shl 16) or (b3 shl 8) or b4
    }

    @Throws(IOException::class)
    private fun read(array: ByteArray) {
        if (m_file!!.read(array) != array.size) throw IOException()
    }

    // Helper
    private fun getWord(array: ByteArray, offset: Int): Int {
        val b1: Int = array[offset].toInt() and  0xFF
        val b2: Int = array[offset + 1].toInt() and 0xFF
        return b1 shl 8 or b2
    }
}