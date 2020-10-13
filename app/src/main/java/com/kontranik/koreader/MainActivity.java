package com.kontranik.koreader;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.kontranik.koreader.pagesplitter.PageSplitterActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.github.florent37.runtimepermission.RuntimePermission.askPermission;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    ListView listView;
    FileListAdapter fileListAdapter;
    List<FileItem> fileItemList = new ArrayList<>();

    private List<String> rootPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView_files);

        fileListAdapter = new FileListAdapter(this, R.layout.filelist_item, fileItemList);
        listView.setAdapter(fileListAdapter);

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                FileItem selectedFileItem = (FileItem) parent.getItemAtPosition(position);

                if ( selectedFileItem.isDir() ) {
                    if ( rootPaths.contains(selectedFileItem.getPath()) )
                        getStorageList();
                    else
                        getFileList(selectedFileItem);
                } else {
                    openReader(selectedFileItem);
                }
            }
        };
        listView.setOnItemClickListener(itemListener);

        checkPermissions();
    }

    void openReader(FileItem fileItem) {
        openReader(fileItem.getPath());
    }

    void openReader(String path) {
        Intent intent = new Intent(this, PageSplitterActivity.class);
        intent.putExtra(ReaderActivity.INTENT_PATH, path );
        startActivity(intent);
    }


    void getFileList(FileItem fileItem) {
        fileItemList.clear();
        fileItemList.addAll(FileHelper.getFileList(fileItem.getPath()));
        fileListAdapter.notifyDataSetInvalidated();
    }

    void getStorageList() {
        fileItemList.clear();
        rootPaths.clear();

        fileItemList.addAll(FileHelper.getStorageList());
        for(FileItem fileItem: fileItemList) {
            String parent = new File(fileItem.getPath()).getParent();
            if ( ! rootPaths.contains(parent) ) rootPaths.add(parent);
        }

        fileListAdapter.notifyDataSetInvalidated();
    }



    private void checkPermissions() {
        askPermission(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)

                .onAccepted((result) -> {
                    //all permissions already granted or just granted
                    //openReader( "/storage/emulated/0/Books/test.epub");
                    openReader("/mnt/sdcard/Download/test.epub");
                    getStorageList();

                })
                .onDenied((result) -> {
                    Snackbar.make(listView, getString(R.string.permissions_needed), Snackbar.LENGTH_SHORT).show();
                    //permission denied, but you can ask again, eg:

                    new AlertDialog.Builder(this)
                            .setMessage(this.getString(R.string.give_permission_storage))
                            .setPositiveButton(this.getString(R.string.okay_string), (dialog, which) -> result.askAgain()) // ask again
                            .setNegativeButton(this.getString(R.string.no_string), (dialog, which) -> dialog.dismiss())
                            .show();

                })
                .onForeverDenied((result) -> {

                    Snackbar.make(listView, getString(R.string.permissions_needed), Snackbar.LENGTH_SHORT)
                            .setAction(this.getString(R.string.go_to_settings), view -> result.goToSettings()).show();

                })
                .ask();
    }

}
