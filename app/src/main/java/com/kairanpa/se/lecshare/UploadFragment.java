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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
    ArrayList<String> fileName = new ArrayList<>();
    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);// well this solved null when rotate somehow?
        progressBar = null;
    }

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
            fileName = (ArrayList<String>) savedInstanceState.getSerializable("nameArray");
            FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, fileName);
            ListView fileList = getView().findViewById(R.id.upload_file_list);
            fileList.setAdapter(nameAdapter);
            nameAdapter.notifyDataSetChanged();
            Log.d("test", "restore save state");
        }

        Button uploadButton = getView().findViewById(R.id.upload_upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "uploading... please wait", Toast.LENGTH_SHORT).show();
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
                final ArrayList<Integer> allProgress = new ArrayList<>();
                for(int i = 0; i < fileName.size(); i++)
                {
                    allProgress.add(0);
                }
                for (int i = 0; i < fileName.size(); i++)
                {
                    file = Uri.fromFile(new File(filePath.get(i)));
                    fileRef = storageRef.child(file.getLastPathSegment());
                    uploadTask = fileRef.putFile(file);
                    lecNote.addFileName(fileName.get(i));
                    final int temp = i;

                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.d("test", "upload progress for " + fileName.get(temp) + " is : " + progress + " %");
                            if (progressBar == null)
                            {
                                progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
                                LinearLayout linearLayout = getView().findViewById(R.id.upload_linear_layout);
                                linearLayout.addView(progressBar);
                            }
                            else
                            {
                                int progressInt = (int) progress;
                                allProgress.set(temp, progressInt);
                                int sumProgress = 0;
                                for (int j = 0; j < allProgress.size(); j++)
                                {
                                    sumProgress += allProgress.get(j);
                                }
                                sumProgress /= allProgress.size();
                                Log.d("test", "sum progress : " + sumProgress);
                                progressBar.setProgress(sumProgress);
                            }
                        }
                    });

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
                            if(temp == filePath.size())
                            {
                                filePath.clear();
                                fileName.clear();
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

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

            String fileNameStr = "no file chosen";
            if (path.lastIndexOf('/') != -1)
            {
                fileNameStr = path.substring(path.lastIndexOf('/')+1);
                fileName.add(fileNameStr);
            }
            Log.d("test", "fileName Size : " + fileName.size());
            for (int i = 0; i < fileName.size(); i++)
            {
                Log.d("test", "fileName : " + fileName.get(i));
            }
            FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, fileName);
            ListView fileList = getView().findViewById(R.id.upload_file_list);
            fileList.setAdapter(nameAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("nameArray", fileName);
        outState.putSerializable("filePath", filePath);
    }

}
