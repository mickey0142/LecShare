package com.kairanpa.se.lecshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import adapter.FileListAdapter;
import model.LecNote;
import model.User;

public class UploadFragment extends Fragment{

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    StorageReference storageRef = fbStorage.getReference();
    ArrayList<String> filePath = new ArrayList<>();
    ArrayList<String> fileName = new ArrayList<>();
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);// well this solved null when rotate somehow?
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        Log.d("test", "user : " + user);
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
            FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, fileName, filePath);
            ListView fileList = getView().findViewById(R.id.upload_file_list);
            fileList.setAdapter(nameAdapter);
            nameAdapter.notifyDataSetChanged();
            Log.d("test", "restore save state");
        }

        checkAuthen();
        initUploadButton();
        initChooseFileButton();
        initBackButton();
        initToolbar();
    }

    void checkAuthen()
    {
        if (fbAuth.getCurrentUser() == null)
        {
            Log.d("test", "not logged in return to login page");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_view, new LoginFragment())
                    .commit();
        }
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
            FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, fileName, filePath);
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

    public void uploadLecNote(final LecNote lecNote)
    {
        final Button uploadButton = getView().findViewById(R.id.upload_upload_button);
        final ProgressBar progressBar = getView().findViewById(R.id.upload_progress_bar);
        fbStore.collection("LecNote").add(lecNote)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        fbStore.collection("LecNote").document(documentReference.getId())
                                .update("documentId", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        uploadButton.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        Log.d("test", "add lecNote to firebase success");
                                        Toast.makeText(getContext(), "add success", Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("User object", user);
                                        Fragment homeFragment = new HomeFragment();
                                        homeFragment.setArguments(bundle);
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                        ft.replace(R.id.main_view, homeFragment).commit();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                uploadButton.setEnabled(true);
                                Log.d("test", "add documentId to firebase failed. Error : " + e.getMessage());
                                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.d("test", "add lecNote to firebase failed. Error : " + e.getMessage());
                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initUploadButton()
    {
        final Button uploadButton = getView().findViewById(R.id.upload_upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText titleBox = getView().findViewById(R.id.upload_title);
                final EditText subjectBox = getView().findViewById(R.id.upload_subject);
                final EditText descriptionBox = getView().findViewById(R.id.upload_description);
                String title = titleBox.getText().toString();
                String subject = subjectBox.getText().toString();
                String description = descriptionBox.getText().toString();
                if (title.equals("") || subject.equals("") || description.equals(""))
                {
                    Toast.makeText(getContext(), "some field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadButton.setEnabled(false);
                final ProgressBar progressBar = getView().findViewById(R.id.upload_progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "uploading... please wait", Toast.LENGTH_SHORT).show();
                final LecNote lecNote = new LecNote();
                lecNote.setDescription(description);
                lecNote.setTitle(title);
                lecNote.setSubject(subject);
                lecNote.setUploadTimeStamp();
                lecNote.setOwner(user.getUsername());
                lecNote.setOwnerId(user.getDocumentId());

                if (fileName.size() == 0)
                {
                    uploadLecNote(lecNote);
                    return;
                }
                Uri file;
                StorageReference fileRef;
                UploadTask uploadTask;
                for (int i = 0; i < fileName.size(); i++)
                {
                    file = Uri.fromFile(new File(filePath.get(i)));
                    fileRef = storageRef.child(file.getLastPathSegment());
                    uploadTask = fileRef.putFile(file);
                    lecNote.addFileName(fileName.get(i));
                    final boolean check;
                    if (i == 0)
                    {
                        check = true;
                    }
                    else
                    {
                        check = false;
                    }
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            Log.d("test", "upload error : " + e.getMessage());
                            Toast.makeText(getContext(), "upload error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("test", "upload file success");
                            if(check)
                            {
                                Log.d("test", "starting upload lecnote");
                                uploadLecNote(lecNote);
                            }
                        }
                    });
                }
            }
        });
    }

    public void initChooseFileButton()
    {
        ImageView chooseFileButton = getView().findViewById(R.id.upload_choose_file_button);
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("*/*");
//                String[] mimeType = {"image/*"};
//                chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, 1);
            }
        });
    }

    public void initBackButton()
    {
        ImageView backToSearchButton = getView().findViewById(R.id.upload_back_button);
        backToSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.upload_toolbar);
        mTool.inflateMenu(R.menu.fragment_menu);
        mTool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Log.d("test", "itemId : " + itemId);
                if (itemId == R.id.menu_home)
                {
                    Log.d("test", "press home");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, homeFragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_profile)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    bundle.putSerializable("Target object", user);
                    Fragment fragment = new ProfileFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_upload)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment uploadFragment = new UploadFragment();
                    uploadFragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, uploadFragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_search_user)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment fragment = new SearchUserFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                }
                else if (itemId == R.id.menu_logout)
                {
                    Log.d("test", "press logout");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want log out ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("test", "log out");
                            fbAuth.signOut();
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_view, new LoginFragment())
                                    .commit();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("test", "log out cancel");
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });
    }
}
