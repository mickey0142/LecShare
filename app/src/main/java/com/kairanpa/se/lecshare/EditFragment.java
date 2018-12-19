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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import adapter.FileListAdapter;
import model.LecNote;
import model.User;

import static java.lang.Math.min;

public class EditFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    StorageReference storageRef = fbStorage.getReference();
    ArrayList<String> filePath = new ArrayList<>();
    ArrayList<String> fileName = new ArrayList<>();
    ArrayList<String> oldFileName = new ArrayList<>();
    User user;
    LecNote lecNote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);// well this solved null when rotate somehow?
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        lecNote = (LecNote) bundle.getSerializable("LecNote object");
        ArrayList<String> temp = lecNote.getFilesName();
        for (int i = 0; i < temp.size(); i++)
        {
            oldFileName.add(temp.get(i));
            fileName.add(temp.get(i));
            filePath.add("nothing");
        }
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("test", "savedInstanceState : " + savedInstanceState);
        if (savedInstanceState != null)
        {
            filePath = (ArrayList<String>) savedInstanceState.getSerializable("filePath");
            fileName = (ArrayList<String>) savedInstanceState.getSerializable("nameArray");
            oldFileName = (ArrayList<String>) savedInstanceState.getSerializable("oldNameArray");
            FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, fileName, filePath);
            ListView fileList = getView().findViewById(R.id.edit_file_list);
            fileList.setAdapter(nameAdapter);
            nameAdapter.notifyDataSetChanged();
            Log.d("test", "restore save state");
        }

        checkAuthen();
        initUploadButton();
        initChooseFileButton();
        initBackButton();
        initToolbar();
        initDeleteButton();
        initSetText();
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
            ListView fileList = getView().findViewById(R.id.edit_file_list);
            fileList.setAdapter(nameAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("nameArray", fileName);
        outState.putSerializable("filePath", filePath);
        outState.putSerializable("oldNameArray", oldFileName);
    }

    public void uploadLecNote(final LecNote lecNote)
    {
        final Button uploadButton = getView().findViewById(R.id.edit_edit_button);
        final Button _deleteButton = getView().findViewById(R.id.edit_delete_button);
        final ProgressBar progressBar = getView().findViewById(R.id.edit_progress_bar);
        fbStore.collection("LecNote").document(lecNote.getDocumentId()).set(lecNote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        uploadButton.setEnabled(true);
                        _deleteButton.setEnabled(true);
                        Log.d("test", "update lecNote to firebase success");
                        Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
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
                uploadButton.setEnabled(true);
                _deleteButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Log.d("test", "update lecNote to firebase failed. Error : " + e.getMessage());
                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initUploadButton()
    {
        final Button _deleteButton = getView().findViewById(R.id.edit_delete_button);
        final Button uploadButton = getView().findViewById(R.id.edit_edit_button);
        Log.d("test", "uploadButton : " + uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadButton.setEnabled(false);
                _deleteButton.setEnabled(false);
                final EditText titleBox = getView().findViewById(R.id.edit_title);
                final EditText subjectBox = getView().findViewById(R.id.edit_subject);
                final EditText descriptionBox = getView().findViewById(R.id.edit_description);
                String title = titleBox.getText().toString();
                String subject = subjectBox.getText().toString();
                String description = descriptionBox.getText().toString();
                if (title.equals("") || subject.equals("") || description.equals(""))
                {
                    Toast.makeText(getContext(), "some field is empty", Toast.LENGTH_SHORT).show();
                    uploadButton.setEnabled(true);
                    _deleteButton.setEnabled(true);
                    return;
                }
                final ProgressBar progressBar = getView().findViewById(R.id.edit_progress_bar);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "uploading... please wait", Toast.LENGTH_SHORT).show();
                lecNote.setDescription(description);
                lecNote.setTitle(title);
                lecNote.setSubject(subject);
                lecNote.setUploadTimeStamp();
                lecNote.setOwner(user.getUsername());
                lecNote.setOwnerId(user.getDocumentId());
                lecNote.setFilesName(fileName);

                boolean newPicture = false;
                Log.d("test", "start");
                if (oldFileName.size() != fileName.size())
                {
                    for (int i = 0; i < oldFileName.size(); i++)
                    {
                        boolean contain = false;
                        for(int j = 0; j < fileName.size(); j++)
                        {
                            if (oldFileName.get(i).equals(fileName.get(j)))
                            {
                                contain = true;
                                Log.d("test", "new picture is true");
                            }
                        }
                        if (!contain)
                        {
                            newPicture = true;
                            break;
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < oldFileName.size(); i++)
                    {
                        if (!oldFileName.get(i).equals(fileName.get(i)))
                        {
                            Log.d("test", "new picture is true 2");
                            newPicture = true;
                            break;
                        }
                    }
                    Log.d("test", "new picture is false");
                }
                if (fileName.size() == 0 || !newPicture)
                {
                    Log.d("test", "upload lecnote only");
                    uploadLecNote(lecNote);
                    return;
                }
                Log.d("test", "start upload");
                Uri file;
                StorageReference fileRef;
                UploadTask uploadTask;
                int minNumber = 0;
                for (int i = 0; i < fileName.size(); i++)
                {
                    Log.d("test", "fileName : " + fileName.get(i) + " filePath : " +  filePath.get(i));
                    if (filePath.get(i).equals("nothing"))
                    {
                        Log.d("test", "skip loop");
                        minNumber += 1;
                        continue;
                    }
                    file = Uri.fromFile(new File(filePath.get(i)));
                    fileRef = storageRef.child(file.getLastPathSegment());
                    uploadTask = fileRef.putFile(file);
                    final boolean check;
                    if (i == minNumber)
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
                            _deleteButton.setEnabled(true);
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
        ImageView chooseFileButton = getView().findViewById(R.id.edit_choose_file_button);
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    public void initBackButton()
    {
        ImageView backToSearchButton = getView().findViewById(R.id.edit_back_button);
        backToSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.edit_toolbar);
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

    void initDeleteButton()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.edit_progress_bar);
        final Button _deleteButton = getView().findViewById(R.id.edit_delete_button);
        _deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                _deleteButton.setEnabled(false);
                fbStore.collection("LecNote").document(lecNote.getDocumentId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                _deleteButton.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("User object", user);
                                Fragment homeFragment = new HomeFragment();
                                homeFragment.setArguments(bundle);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.replace(R.id.main_view, homeFragment).commit();
                                Toast.makeText(getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        _deleteButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("test","delete error " + e.getMessage());
                    }
                });
            }
        });
    }

    void initSetText()
    {
        final EditText titleBox = getView().findViewById(R.id.edit_title);
        final EditText subjectBox = getView().findViewById(R.id.edit_subject);
        final EditText descriptionBox = getView().findViewById(R.id.edit_description);
        titleBox.setText(lecNote.getTitle());
        subjectBox.setText(lecNote.getSubject());
        descriptionBox.setText(lecNote.getDescription());
        FileListAdapter nameAdapter = new FileListAdapter(getActivity(), R.layout.fragment_file_list_item, fileName, filePath);
        ListView fileList = getView().findViewById(R.id.edit_file_list);
        fileList.setAdapter(nameAdapter);
    }
}
