package com.kontranik.koreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FileListAdapter extends ArrayAdapter<FileItem> {

    private LayoutInflater inflater;
    private int layout;
    private List<FileItem> fileitems;

    public FileListAdapter(Context context, int resource, List<FileItem> states) {
        super(context, resource, states);
        this.fileitems = states;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.fileimage);
        TextView nameView = (TextView) view.findViewById(R.id.filename);
        TextView pathView = (TextView) view.findViewById(R.id.filepath);

        FileItem fileItem = fileitems.get(position);

        if ( fileItem.getImage() != null) {
            Bitmap bmp = ImageUtils.getBitmap(getContext(), fileItem.getImage(), fileItem.getPath() );
            if ( bmp != null) {
                imageView.setImageBitmap(bmp);
            }
        }
        nameView.setText(fileItem.getName());
        pathView.setText(fileItem.getPath());

        return view;
    }
}