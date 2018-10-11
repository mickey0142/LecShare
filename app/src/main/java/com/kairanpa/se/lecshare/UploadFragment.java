package com.kairanpa.se.lecshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import model.LecNote;

public class UploadFragment extends Fragment{

    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    StorageReference storageRef = fbStorage.getReference();
    ArrayList<String> filePath = new ArrayList<>();
    ArrayList<String> fileURL = new ArrayList<>();
    ArrayList<String> nameArray = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("test", "savedInstanceState : " + savedInstanceState);
        if (savedInstanceState != null)
        {
            filePath = (ArrayList<String>) savedInstanceState.getSerializable("filePath");
            fileURL = (ArrayList<String>) savedInstanceState.getSerializable("fileURL");
            nameArray = (ArrayList<String>) savedInstanceState.getSerializable("nameArray");
            FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, nameArray);
            ListView filelist = getView().findViewById(R.id.upload_file_list);
            filelist.setAdapter(nameAdapter);
            nameAdapter.notifyDataSetChanged();
            Log.d("test", "restore save state");
        }

        Button uploadButton = getView().findViewById(R.id.upload_upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText titleBox = getView().findViewById(R.id.upload_title);
                final EditText descriptionBox = getView().findViewById(R.id.upload_description);
                String title = titleBox.getText().toString();
                String description = descriptionBox.getText().toString();
                final LecNote lecNote = new LecNote();
                lecNote.setDescription(description);
                lecNote.setTitle(title);

                Uri file;
                StorageReference fileRef;
                UploadTask uploadTask;
                for (int i = 0; i < filePath.size(); i++)
                {
                    file = Uri.fromFile(new File(filePath.get(i)));
                    fileRef = storageRef.child(file.getLastPathSegment());
                    uploadTask = fileRef.putFile(file);
                    fileURL.add(fileRef.getDownloadUrl().toString());
                    lecNote.addFileURL(fileURL.get(i));
                    final int temp = i;

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("test", "upload error : " + e.getMessage());
                            Toast.makeText(getContext(), "upload error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("test", "upload file success");
                            if (temp == 0)
                            {
                                fbStore.collection("LecNote").add(lecNote)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("test", "add lecNote to firebase success");
                                                Toast.makeText(getContext(), "add success", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("test", "add lecNote to firebase failed. Error : " + e.getMessage());
                                        Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            filePath.clear();
                            fileURL.clear();
                            nameArray.clear();
                            FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, nameArray);
                            ListView fileList = getView().findViewById(R.id.upload_file_list);
                            fileList.setAdapter(nameAdapter);
                            nameAdapter.notifyDataSetChanged();
                            // or maybe go to another layout here or maybe go to another layout when success
                            // or maybe need to check when all upload is completed to go to another layout
                        }
                    });
                }
            }
        });

        Button chooseFileButton = getView().findViewById(R.id.upload_choose_file_button);
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("application/pdf");
                String[] mimeType = {"image/*"};
                chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, 1);
            }
        });

        Button tempBack = getView().findViewById(R.id.temp_back_button);
        tempBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new ViewFragment())
                        .commit();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = "";
        if(requestCode == 1)
        {
            if (data == null)
            {
                return;
            }
            Uri uri = data.getData();
            path = GetFilePathFromDevice.getPath(getContext(), uri);
            filePath.add(path);
            Log.d("test", "path is : " + path);

            String fileName = "no file chosen";
            if (path.lastIndexOf('/') != -1)
            {
                fileName = path.substring(path.lastIndexOf('/')+1);
                nameArray.add(fileName);
            }
            Log.d("test", "nameArray Size : " + nameArray.size());
            for (int i = 0; i < nameArray.size(); i++)
            {
                Log.d("test", "nameArray : " + nameArray.get(i));
            }
            FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, nameArray);
            ListView fileList = getView().findViewById(R.id.upload_file_list);
            fileList.setAdapter(nameAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("nameArray", nameArray);
        outState.putSerializable("filePath", filePath);
        outState.putSerializable("fileURL", fileURL);
    }
}
