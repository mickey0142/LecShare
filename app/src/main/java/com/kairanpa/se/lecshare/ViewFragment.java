package com.kairanpa.se.lecshare;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import model.LecNote;
import model.User;

public class ViewFragment extends Fragment{

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
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

        setTextBox();
        initBackButton();
        showFileList();
        showImage();
        initToolbar();
    }

    public void setTextBox()
    {
        TextView titleBox = getView().findViewById(R.id.view_lec_note_title);
        TextView descriptionBox = getView().findViewById(R.id.view_lec_note_description);
        titleBox.setText(lecNote.getTitle());
        descriptionBox.setText(lecNote.getDescription());
    }

    public void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.view_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void showFileList()
    {
        ArrayList<String> fileName = lecNote.getFilesName();
        FileListDownloadAdapter nameAdapter = new FileListDownloadAdapter(getActivity(), R.layout.fragment_view, fileName);
        ListView fileList = getView().findViewById(R.id.view_file_list);
        fileList.setAdapter(nameAdapter);
    }

    public void showImage()
    {
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

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.view_toolbar);
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
                    Log.d("test", "press profile");
                    Toast.makeText(getContext(), "page is not exist yet :3", Toast.LENGTH_SHORT).show();
                }
                else if (itemId == R.id.menu_logout)
                {
                    Log.d("test", "press logout");
                    fbAuth.signOut();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new LoginFragment())
                            .commit();
                }
                return false;
            }
        });
        TextView titleText = getView().findViewById(R.id.view_title_text);
        titleText.setText(lecNote.getTitle());
    }
}
