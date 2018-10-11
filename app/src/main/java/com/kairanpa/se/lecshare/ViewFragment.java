package com.kairanpa.se.lecshare;

import android.os.Bundle;
import android.os.Environment;
import android.content.Context.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import model.LecNote;

public class ViewFragment extends Fragment{

    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    StorageReference storageRef = fbStorage.getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fbStore.collection("LecNote").whereEqualTo("title", "test1").get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("test", document.getId() + " => " + document.getData());
                        LecNote lecNote = document.toObject(LecNote.class);
                        TextView titleBox = getView().findViewById(R.id.view_lec_note_title);
                        TextView descriptionBox = getView().findViewById(R.id.view_lec_note_description);
                        titleBox.setText(lecNote.getTitle());
                        descriptionBox.setText(lecNote.getDescription());
                        StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com").child("FB_IMG_1538452608818.jpg");
                        Log.d("test", "url is : " + storageRef.getDownloadUrl());

                        try
                        {
                            // add asking for permission somewhere around here
                            File directory = new File(Environment.getExternalStorageDirectory(), "abc");
                            if(!directory.exists())
                            {
                                directory.mkdir();
                            }
                            final File file = new File(directory, "def.jpg");
                            storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("test", "download success");
                                    Log.d("test", "create at ? : " + file.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("test", "download failed");
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            Log.d("test", "error from viewFragment : " + e.getMessage());
                        }
                    }
                }
            }
        });
    }
}
