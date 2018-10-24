package com.kairanpa.se.lecshare;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import model.LecNote;
import model.User;

public class ViewFragment extends Fragment{

    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    LecNote lecNote;
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lecNote = (LecNote) bundle.getSerializable("LecNote object");
        user = (User) bundle.getSerializable("User object");
        Log.d("test", "user : " + user);
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
                getFragmentManager().popBackStack();
            }
        });

        ArrayList<String> fileName = lecNote.getFilesName();
        FileListDownloadAdapter nameAdapter = new FileListDownloadAdapter(getActivity(), R.layout.fragment_view, fileName);
        ListView fileList = getView().findViewById(R.id.view_file_list);
        fileList.setAdapter(nameAdapter);

        for (int i = 0; i < lecNote.getFilesName().size(); i++)
        {
            if (lecNote.getFilesName().get(i).endsWith(".pdf"))
            {
                Log.d("test", "it is pdf skipped loop");
                continue;
            }
            ImageView fileImage = new ImageView(getContext());
            LinearLayout linearLayout = getView().findViewById(R.id.view_linear_layout);
            linearLayout.addView(fileImage);
            Log.d("test", "file name : " + lecNote.getFilesName().get(0));
            StorageReference imageRef = fbStorage.getReferenceFromUrl("gs://lecshare-44a6a.appspot.com").child(lecNote.getFilesName().get(i));
            GlideApp.with(getContext()).load(imageRef).into(fileImage);
        }
    }
}
