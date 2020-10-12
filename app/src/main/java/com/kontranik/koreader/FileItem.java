package com.kontranik.koreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.Objects;

public class FileItem {
    private ImageEnum image;
    private boolean isDir;
    private boolean isRoot;
    private String name;
    private String path;

    public FileItem(ImageEnum image, String name, String path, boolean isDir, boolean isRoot) {
        this.image = image;
        this.isDir = isDir;
        this.isRoot = isRoot;
        this.name = name;
        this.path = path;
    }

/*    public FileItem(Drawable drawable, String name, String path, boolean isDir, boolean isRoot) {
        Bitmap icon = ImageUtils.drawableToBitmap(drawable);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] byteArray = null;
        if ( icon != null) {
            icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }
        this.image = byteArray;
        this.name = name;
        this.path = path;
        this.isDir = isDir;
        this.isRoot = true;
    }*/

    public ImageEnum getImage() {
        return image;
    }

    public void setImage(ImageEnum image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDir() {
        return isDir;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileItem fileItem = (FileItem) o;
        return path.equals(fileItem.path);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }
}

class FileItemNameComparator implements Comparator<FileItem> {
    @Override
    public int compare(FileItem o1, FileItem o2) {
        return o1.getName().compareTo(o2.getName());
    }
}

enum  ImageEnum {
    Parent,
    SD,
    Dir,
    File,
    Ebook,
    Epub
        }