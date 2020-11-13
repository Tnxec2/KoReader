package com.kontranik.koreader.utils;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHelper {

    public static final String BACKDIR = "..";

    public static String getNameNoExt(String name) {
        int i = name.lastIndexOf('.');
        if (i > 0)
            name = name.substring(0, i);
        return name;
    }

    public static String getExt(String name) { // FilenameUtils.getExtension(n)
        int i = name.lastIndexOf('.');
        if (i > 0)
            return name.substring(i + 1);
        return "";
    }

    public static List<FileItem> getStorageList() {
        List<FileItem> result = new ArrayList<>();

        File externalStorageDirectory = Environment.getExternalStorageDirectory();

        result.add(new FileItem(
                ImageEnum.SD,
                externalStorageDirectory.getName(),
                externalStorageDirectory.getPath(),
                true,
                true, null ) );

        if ( externalStorageDirectory.getParent() != null) {
            File[] files = new File(externalStorageDirectory.getParent()).listFiles();
            File[] temp = null;
            if (files != null && files.length > 0) {

                for (File file : files) {
                    if (file.isDirectory() && file.canRead())
                        temp = file.listFiles();
                        if (temp != null && temp.length > 0)
                            result.add(new FileItem(
                                    ImageEnum.SD, file.getName(), file.getPath(), true, true, null));
                }
            } else {
                String p = new File(externalStorageDirectory.getParent()).getParent();
                if ( p != null) {
                    files = new File(p).listFiles();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            if (file.isDirectory() && file.canRead()) {
                                temp = file.listFiles();
                                if (temp != null && temp.length > 0)
                                    result.add(
                                            new FileItem(ImageEnum.SD, file.getName(), file.getPath(), true, true, null));
                            }
                        }
                    }
                }
            }
        }
        return  result;
    }

    public static List<FileItem> getFileList(String path) {
        List<FileItem> result = new ArrayList<>();
        File dir = new File(path);
        if ( ! dir.isDirectory() ) return result;

        result.add(new FileItem( ImageEnum.Parent, BACKDIR, dir.getParent(), true, false, null));

        File[] dirs = dir.listFiles(new FilenameFilter() {
            public boolean accept(File current, String name) {
                File f = new File(current, name);
                return !f.isHidden() && !name.startsWith(".") && f.isDirectory();
            }
        });

        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File current, String name) {
                File f = new File(current, name);
                return !f.isHidden() && !name.startsWith(".")
                        && (
                                name.toLowerCase().endsWith(".epub")
                                // || name.toLowerCase().endsWith(".fb2")
                );
            }
        });

        if ( dirs != null && dirs.length > 0) {
            Arrays.sort(dirs);

            for ( File f: dirs) {
                if ( f.isDirectory() ) {
                    result.add(new FileItem(ImageEnum.Dir, f.getName(), f.getPath(), f.isDirectory(), false, null));
                }
            }
        }

        if ( files != null && files.length > 0) {
            Arrays.sort(files);

            for ( File f: files) {
                if ( f.getName().toLowerCase().endsWith(".epub")){
                    result.add(new FileItem( ImageEnum.Epub, f.getName(), f.getPath(), f.isDirectory(), false, null));
//                } else if ( f.getName().toLowerCase().endsWith(".fb2")){
//                    result.add(new FileItem( ImageEnum.Fb2, f.getName(), f.getPath(), f.isDirectory(), false));
                } else {
                   // result.add(new FileItem( ImageEnum.Ebook, f.getName(), f.getPath(), f.isDirectory(), false));
                }
            }
        }

        return result;
    }


}
