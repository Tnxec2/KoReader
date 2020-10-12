package com.kontranik.koreader;

import java.io.FileInputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class EpubHelper {
    static EpubReader epubReader = new EpubReader();
    static FileInputStream fileInputStream = null;

    public static byte[] getCover(String path) {
        try {
            fileInputStream = new FileInputStream(path);
            Book book = epubReader.readEpub(fileInputStream);
            if ( book == null ) return null;
            Resource cover = book.getCoverImage();
            if ( cover == null ) return  null;
            return cover.getData();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
