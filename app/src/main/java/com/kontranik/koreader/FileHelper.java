package com.kontranik.koreader;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static List<FileItem> getStorageList() {
        List<FileItem> result = new ArrayList<>();

        File externalStorageDirectory = Environment.getExternalStorageDirectory();

        result.add(new FileItem(ImageEnum.SD, externalStorageDirectory.getName(), externalStorageDirectory.getPath(), true, true ) );

        if ( externalStorageDirectory.getParent() != null) {
            File[] files = new File(externalStorageDirectory.getParent()).listFiles();
            File[] temp = null;
            if (files != null && files.length > 0) {

                for (File file : files) {
                    if (file.isDirectory() && file.canRead())
                        temp = file.listFiles();
                        if (temp != null && temp.length > 0)
                            result.add(new FileItem(ImageEnum.SD, file.getName(), file.getPath(), true, true));
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
                                    result.add(new FileItem(ImageEnum.SD, file.getName(), file.getPath(), true, true));
                            }
                        }
                    }
                }
            }
        }
        return  result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<FileItem> getFileList(String path) {
        List<FileItem> result = new ArrayList<>();
        File dir = new File(path);
        if ( ! dir.isDirectory() ) return result;

        result.add(new FileItem( ImageEnum.Parent, "..", dir.getParent(), true, false));

        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return !file.isHidden() && !name.startsWith(".") && ( file.isDirectory() || name.toLowerCase().endsWith(".epub") );
            }
        });

        if ( files != null) {
            for ( File f: files) {
                if ( f.isDirectory() ) {
                    result.add(new FileItem(ImageEnum.Dir, f.getName(), f.getPath(), f.isDirectory(), false));
                } else if ( f.getName().toLowerCase().endsWith(".epub")){
                    result.add(new FileItem( ImageEnum.Epub, f.getName(), f.getPath(), f.isDirectory(), false));
                } else {
                    result.add(new FileItem( ImageEnum.Ebook, f.getName(), f.getPath(), f.isDirectory(), false));
                }
            }
        }
        result.sort(new FileItemNameComparator());
        return result;
    }

   /* void getStorageList() {
        List<StorageUtils.StorageInfo> storageList = StorageUtils.getStorageList();

        if ( !storageList.isEmpty() ) {
            fileItemList.clear();
            pathStorage = new File(storageList.get(0).path).getParent();
            listView.setEnabled(true);
            for (StorageUtils.StorageInfo storageInfo : storageList) {
                fileItemList.add(new FileItem( getDrawable(R.drawable.ic_sd_card_black_24dp), storageInfo.getDisplayName(), storageInfo.path, true, true));
            }
            fileListAdapter.notifyDataSetInvalidated();
        } else {
            Snackbar.make(listView, getString(R.string.no_storage_found), Snackbar.LENGTH_LONG).show();
        }
    }*/
}
