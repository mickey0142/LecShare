package com.kairanpa.se.lecshare;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import model.LecNote;
import model.LecNotePack;
import model.User;

public class SearchFragment extends Fragment {

    ArrayList<LecNote> allNote;
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        allNote = ((LecNotePack) bundle.getSerializable("allNote")).getLecNoteList();
        Log.d("test", "user : " + user);
        Log.d("test", "allNote : " + allNote);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initSearchButton();
        initBackButton();
    }

    public void initSearchButton()
    {
        Button searchButton = getView().findViewById(R.id.search_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchTitle = getView().findViewById(R.id.search_search_title);
                EditText searchSubject = getView().findViewById(R.id.search_search_subject);
                EditText searchOwner = getView().findViewById(R.id.search_search_owner);
                String searchTitleStr = searchTitle.getText().toString();
                String searchSubjectStr = searchSubject.getText().toString();
                String searchOwnerStr = searchOwner.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                bundle.putString("searchTitle", searchTitleStr);
                bundle.putString("searchSubject", searchSubjectStr);
                bundle.putString("searchOwner", searchOwnerStr);
                LecNotePack lecNotePack = new LecNotePack(allNote);
                bundle.putSerializable("allNote", lecNotePack);
                Fragment searchResultFragment = new SearchResultFragment();
                searchResultFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, searchResultFragment).addToBackStack(null).commit();
            }
        });
    }

    public void initBackButton()
    {
        Button backButton = getView().findViewById(R.id.search_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                Fragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, homeFragment).commit();
            }
        });
    }
}
