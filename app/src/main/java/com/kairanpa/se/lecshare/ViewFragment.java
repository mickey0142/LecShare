package com.kairanpa.se.lecshare;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;

import model.LecNote;

public class ViewFragment extends Fragment{

    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    StorageReference storageRef = fbStorage.getReference();
    LecNote lecNote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lecNote = (LecNote) bundle.getSerializable("LecNote object");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView titleBox = getView().findViewById(R.id.view_lec_note_title);
        TextView descriptionBox = getView().findViewById(R.id.view_lec_note_description);
        titleBox.setText(lecNote.getTitle());
        descriptionBox.setText(lecNote.getDescription());

        Button backButton = getView().findViewById(R.id.view_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new SearchFragment())
                        .commit();
            }
        });

        ArrayList<String> fileName = lecNote.getFilesName();
        FileListDownloadAdapter nameAdapter = new FileListDownloadAdapter(getActivity(), R.layout.fragment_view, fileName);
        ListView fileList = getView().findViewById(R.id.view_file_list);
        fileList.setAdapter(nameAdapter);

        /*fbStore.collection("LecNote").whereEqualTo("title", "test2").get()
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
                        StorageReference storageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com").child("26044_nutty_CNEIFINAL.pdf");
                        Log.d("test", "url is : " + storageRef.getDownloadUrl());

                        try
                        {
                            // add asking for permission somewhere around here
                            File directory = new File(Environment.getExternalStorageDirectory(), "abc");
                            if(!directory.exists())
                            {
                                directory.mkdir();
                            }
                            final File file = new File(directory, "gjh.pdf");
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
        });*/
    }
}
