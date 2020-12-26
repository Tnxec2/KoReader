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
package com.kontranik.koreader.parser.fb2reader.model;


/**
 * This class represents a single binary data blob. It can be viewed
 * in two representations: as a Base64-encoded string, or as a byte
 * array with specified length.
 *
 * @author Dmitry Baryshkov (dbaryshkov@gmail.com)
 *
 */
public class BinaryData {
    /**
     * Binary name.
     */
    private String name;
    /**
     * Content-Type of the blob.
     */
    private String contentType;

    /**
     * Contents array.
     */
    private byte[] contentsArray = new byte[1];
    /**
     * The length of the contents.
     */
    private int contentsLength;


    public BinaryData(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public BinaryData() {

    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns base64-encoding fot <code>bits</code>
     * @param bits bits to get base64 for
     * @return base64-encoding fot <code>bits</code>
     */
    private char getBase64Char(long bits) {
        if (bits < 0 || bits > 63) {
            throw new IllegalArgumentException("Bad character number during encoding: " + bits);
        } else if (bits < 26) {
            return (char) ('A' + bits);
        } else if (bits < 52) {
            return (char) ('a' + bits - 26);
        } else if (bits < 62) {
            return (char) ('0' + bits - 52);
        } else if (bits == 62) {
            return '+';
        } else { // 63
            return '/' ;
        }
    }

    public CharSequence getBase64Encoded() {
        StringBuilder result = new StringBuilder();
        int bitbuffer = 0;
        for (int i = 0; i < this.contentsLength; i++) {
            bitbuffer = (bitbuffer << 8) | (this.contentsArray[i] & 0xff);

            switch (i%3) {
                case 0:
                    result.append(getBase64Char((bitbuffer >>> 2) & 0x3F));
                    break;
                case 1:
                    result.append(getBase64Char((bitbuffer >>> 4) & 0x3F));
                    break;
                case 2:
                    result.append(getBase64Char((bitbuffer >>> 6) & 0x3F));
                    result.append(getBase64Char((bitbuffer >>> 0) & 0x3F));
                    break;

            }

            if ((i + 1) % 54 == 0) { // 72 /4 * 3
                result.append("\n");
            }
        }

        if (this.contentsLength % 3 == 1) {
            bitbuffer <<= 8;
            result.append(getBase64Char((bitbuffer >>> 4) & 0x3F));
            result.append("==");
        } else if (this.contentsLength % 3 == 2) {
            bitbuffer <<= 8;
            result.append(getBase64Char((bitbuffer >>> 6) & 0x3F));
            result.append("=");
        }

        if (this.contentsLength % 54 != 0) {
            result.append("\n");
        }

        return result;
    }

    public void setBase64Encoded(char[] base64Encoded) {
        this.contentsArray = new byte[base64Encoded.length/4*3];
        this.contentsLength = 0;
        int bitBuffer = 0;
        int bitBufferBytes = 0;
        int byteBufferPad = 0;

        for (char curch : base64Encoded) {
            if (curch == '\n' || curch == '\r' || curch == ' ' || curch == '\t') {
                continue;
            }
            bitBuffer <<= 6;
            if (curch >= 'A' && curch <= 'Z') {
                bitBuffer += (curch - 'A');
            } else if (curch >= 'a' && curch <= 'z') {
                bitBuffer +=  (curch - 'a' + 26);
            } else if (curch >= '0' && curch <= '9') {
                bitBuffer += (curch - '0' + 52);
            } else if (curch == '+') {
                bitBuffer += 62;
            } else if (curch == '/') {
                bitBuffer += 63;
            } else if (curch == '=') {
                byteBufferPad ++;
            } else {
                throw new IllegalArgumentException("Bad character value: '" + curch + "'");
            }
            bitBufferBytes ++;
            if (bitBufferBytes == 4) {
                this.contentsArray[this.contentsLength ++] = (byte)(bitBuffer >> 16);
                if (byteBufferPad < 2) {
                    this.contentsArray[this.contentsLength ++] = (byte)(bitBuffer >> 8);
                }
                if (byteBufferPad < 1) {
                    this.contentsArray[this.contentsLength ++] = (byte)(bitBuffer >> 0);
                }
                bitBufferBytes = 0;
                bitBuffer = 0;
            }
        }
    }

    public void setContents(byte[] contents, int length) {
        if (this.contentsLength < length) {
            this.contentsArray = new byte[length];
        }
        this.contentsLength = length;
        for (int i = 0; i < length; i++) {
            this.contentsArray[i] = contents[i];
        }
    }

	/**
	 * @return the contentsArray
	 */
	public byte[] getContentsArray() {
		return contentsArray;
	}

	/**
	 * @param contentsArray the contentsArray to set
	 */
	public void setContentsArray(byte[] contentsArray) {
		this.contentsArray = contentsArray;
	}

	/**
	 * @return the contentsLength
	 */
	public int getContentsLength() {
		return contentsLength;
	}

	/**
	 * @param contentsLength the contentsLength to set
	 */
	public void setContentsLength(int contentsLength) {
		this.contentsLength = contentsLength;
	}



}
