package com.kairanpa.se.lecshare;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class FileListDownloadAdapter extends ArrayAdapter {

    Context context;
    ArrayList<String> fileList;

    public FileListDownloadAdapter(Context context, int  resource, ArrayList<String> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.fileList =  objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View fileItem = LayoutInflater.from(context).inflate(R.layout.fragment_file_download_list_item, parent, false);
        TextView fileName = fileItem.findViewById(R.id.file_download_list_item_name);
        ImageView downloadButton = fileItem.findViewById(R.id.file_list_item_download_button);
        final ProgressBar progressBar = fileItem.findViewById(R.id.file_list_item_progress_bar);
        final ImageView downloadCompleted = fileItem.findViewById(R.id.file_list_item_download_completed);
        final int pos = position;
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "fileList.get(pos) : " + fileList.get(pos));
                Toast.makeText(context, "downloading " + fileList.get(pos), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                downloadCompleted.setVisibility(View.INVISIBLE);
                try
                {
                    // add asking for permission somewhere around here
                    File directory = new File(Environment.getExternalStorageDirectory(), "LectureNote");
                    if(!directory.exists())
                    {
                        directory.mkdir();
                    }
                    final File file = new File(directory, fileList.get(pos));
                    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
                    StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com")
                            .child(fileList.get(pos));
                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("test", "download success");
                            Log.d("test", "create at : " + file.toString());
                            Toast.makeText(context, "download " + fileList.get(pos) + " completed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("test", "download failed");
                            Toast.makeText(context, "download error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            int progressInt = (int) progress;
                            progressBar.setProgress(progressInt);
                            if (progressInt == 100)
                            {
                                progressBar.setVisibility(View.INVISIBLE);
                                downloadCompleted.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    Log.d("test", "error from filelistdownloadadapter : " + e.getMessage());
                }
            }
        });
        String name = fileList.get(position);
        fileName.setText(name);

        return fileItem;
    }
}
