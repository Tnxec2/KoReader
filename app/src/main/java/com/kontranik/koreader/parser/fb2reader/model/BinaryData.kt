/*
 * JBookReader - Java FictionBook Reader
 * Copyright (C) 2006 Dmitry Baryshkov
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.kontranik.koreader.parser.fb2reader.model

import kotlin.experimental.and


class BinaryData {
    var name: String? = null
    var contentType: String? = null
    var contentsArray = ByteArray(1)
    var contentsLength = 0

    constructor(name: String?, contentType: String?) {
        this.name = name
        this.contentType = contentType
    }

    constructor() {}

    /**
     * Returns base64-encoding fot `bits`
     * @param bits bits to get base64 for
     * @return base64-encoding fot `bits`
     */
    private fun getBase64Char(bits: Long): Char {
        return if (bits < 0 || bits > 63) {
            throw IllegalArgumentException("Bad character number during encoding: $bits")
        } else if (bits < 26) {
            ('A'.toLong() + bits).toChar()
        } else if (bits < 52) {
            ('a'.toLong() + bits - 26).toChar()
        } else if (bits < 62) {
            ('0'.toLong() + bits - 52).toChar()
        } else if (bits == 62L) {
            '+'
        } else { // 63
            '/'
        }
    }

    // 72 /4 * 3
    val base64Encoded: CharSequence
        get() {
            val result = StringBuilder()
            var bitbuffer = 0
            for (i in 0 until contentsLength) {
                bitbuffer = bitbuffer shl 8 or (contentsArray[i].toInt() and 0xff)
                when (i % 3) {
                    0 -> result.append(getBase64Char((bitbuffer ushr 2 and 0x3F).toLong()))
                    1 -> result.append(getBase64Char((bitbuffer ushr 4 and 0x3F).toLong()))
                    2 -> {
                        result.append(getBase64Char((bitbuffer ushr 6 and 0x3F).toLong()))
                        result.append(getBase64Char((bitbuffer ushr 0 and 0x3F).toLong()))
                    }
                }
                if ((i + 1) % 54 == 0) { // 72 /4 * 3
                    result.append("\n")
                }
            }
            if (contentsLength % 3 == 1) {
                bitbuffer = bitbuffer shl 8
                result.append(getBase64Char((bitbuffer ushr 4 and 0x3F.toLong().toInt()).toLong()))
                result.append("==")
            } else if (contentsLength % 3 == 2) {
                bitbuffer = bitbuffer shl 8
                result.append(getBase64Char((bitbuffer ushr 6 and 0x3F.toLong().toInt()).toLong()))
                result.append("=")
            }
            if (contentsLength % 54 != 0) {
                result.append("\n")
            }
            return result
        }

    fun setBase64Encoded(base64Encoded: CharArray) {
        contentsArray = ByteArray(base64Encoded.size / 4 * 3)
        contentsLength = 0
        var bitBuffer = 0
        var bitBufferBytes = 0
        var byteBufferPad = 0
        for (curch in base64Encoded) {
            if (curch == '\n' || curch == '\r' || curch == ' ' || curch == '\t') {
                continue
            }
            bitBuffer = bitBuffer shl 6
            if (curch >= 'A' && curch <= 'Z') {
                bitBuffer += curch - 'A'
            } else if (curch >= 'a' && curch <= 'z') {
                bitBuffer += curch - 'a' + 26
            } else if (curch >= '0' && curch <= '9') {
                bitBuffer += curch - '0' + 52
            } else if (curch == '+') {
                bitBuffer += 62
            } else if (curch == '/') {
                bitBuffer += 63
            } else if (curch == '=') {
                byteBufferPad++
            } else {
                throw IllegalArgumentException("Bad character value: '$curch'")
            }
            bitBufferBytes++
            if (bitBufferBytes == 4) {
                contentsArray[contentsLength++] = (bitBuffer shr 16).toByte()
                if (byteBufferPad < 2) {
                    contentsArray[contentsLength++] = (bitBuffer shr 8).toByte()
                }
                if (byteBufferPad < 1) {
                    contentsArray[contentsLength++] = (bitBuffer shr 0).toByte()
                }
                bitBufferBytes = 0
                bitBuffer = 0
            }
        }
    }

    fun setContents(contents: ByteArray, length: Int) {
        if (contentsLength < length) {
            contentsArray = ByteArray(length)
        }
        contentsLength = length
        for (i in 0 until length) {
            contentsArray[i] = contents[i]
        }
    }
}