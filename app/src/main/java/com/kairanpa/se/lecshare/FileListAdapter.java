package com.kairanpa.se.lecshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FileListAdapter extends ArrayAdapter {

    Context context;
    ArrayList<String> fileList;
    ArrayList<String> filePath;

    public FileListAdapter(Context context, int  resource, ArrayList<String> objects, ArrayList<String> filePath)
    {
        super(context, resource, objects);
        this.context = context;
        this.fileList =  objects;
        this.filePath = filePath;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View fileItem = LayoutInflater.from(context).inflate(R.layout.fragment_file_list_item, parent, false);
        TextView fileName = fileItem.findViewById(R.id.file_list_item_file_name);
        ImageView removeButton = fileItem.findViewById(R.id.file_list_item_delete_button);
        final int pos = position;
        final FileListAdapter temp = this;
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileList.remove(pos);
                filePath.remove(pos);
                temp.notifyDataSetChanged();
            }
        });
        String name = fileList.get(position);
        fileName.setText(name);

        return fileItem;
    }
}
