package com.kairanpa.se.lecshare;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import model.LecNote;
import model.User;

public class SearchFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    ArrayList<LecNote> allNote;
    String orderBy = "title";
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        Log.d("test", "user : " + user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        queryOrderBy();

        Spinner dropDown = getView().findViewById(R.id.search_drop_down);
        String[] orderType = new String[]{"title", "subject", "time"};
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, orderType);
        dropDown.setAdapter(orderAdapter);
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    orderBy = "title";
                    queryOrderBy();
                }
                else if (position == 1)
                {
                    orderBy = "subject";
                    queryOrderBy();
                }
                else if (position == 2)
                {
                    orderBy = "time";
                    queryOrderBy();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final TextView moreOption = getView().findViewById(R.id.search_more_option);
        final EditText searchSubject = getView().findViewById(R.id.search_search_subject);
        final EditText searchOwner = getView().findViewById(R.id.search_search_owner);
        moreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchSubject.getVisibility() == View.GONE)
                {
                    searchSubject.setVisibility(View.VISIBLE);
                    searchOwner.setVisibility(View.VISIBLE);
                    moreOption.setText("less option");
                }
                else if(searchSubject.getVisibility() == View.VISIBLE)
                {
                    searchSubject.setVisibility(View.GONE);
                    searchOwner.setVisibility(View.GONE);
                    searchSubject.setText("");
                    searchOwner.setText("");
                    moreOption.setText("more option");
                }
            }
        });

        Button searchButton = getView().findViewById(R.id.search_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAllNote();
            }
        });

        Button uploadButton = getView().findViewById(R.id.search_upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                Fragment uploadFragment = new UploadFragment();
                uploadFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, uploadFragment).addToBackStack(null).commit();
            }
        });

        ImageView refreshButton = getView().findViewById(R.id.search_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryOrderBy();
                searchAllNote();
            }
        });

        Button logOutButton = getView().findViewById(R.id.search_log_out_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbAuth.signOut();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new LoginFragment())
                        .commit();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void queryOrderBy()
    {
        Query query = fbStore.collection("LecNote");
//        if (orderBy.equals("score"))
//        {
//            query = fbStore.collection("LecNote")
//                    .orderBy("score", Query.Direction.DESCENDING)
//                    .orderBy("uploadTimeStamp", Query.Direction.DESCENDING)
//                    .orderBy("title", Query.Direction.ASCENDING);
//        }
        if(orderBy.equals("subject"))
        {
            query = fbStore.collection("LecNote").orderBy("subject", Query.Direction.ASCENDING);
        }
        else if(orderBy.equals("title"))
        {
            query = fbStore.collection("LecNote").orderBy("title", Query.Direction.ASCENDING);
        }
        else if(orderBy.equals("time"))
        {
            query = fbStore.collection("LecNote").orderBy("uploadTimeStamp", Query.Direction.DESCENDING);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    final ArrayList<LecNote> lecNoteList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        LecNote lecNote = document.toObject(LecNote.class);
                        lecNoteList.add(lecNote);
                    }
                    allNote = (ArrayList<LecNote>) lecNoteList.clone();
                    searchAllNote();
//                    LecNoteListAdapter lecNoteListAdapter = new LecNoteListAdapter(getActivity(), R.layout.fragment_file_list_item, lecNoteList);
//                    ListView lecNoteListView = getView().findViewById(R.id.search_lec_note_list);
//                    lecNoteListView.setAdapter(lecNoteListAdapter);
//                    lecNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            Fragment viewFragment = new ViewFragment();
//                            Bundle bundle = new Bundle();
//                            Log.d("test", lecNoteList.get(position).toString());
//                            bundle.putSerializable("LecNote object", lecNoteList.get(position));
//                            bundle.putSerializable("User object", user);
//                            viewFragment.setArguments(bundle);
//                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                            ft.replace(R.id.main_view, viewFragment).commit();
//                        }
//                    });
                }
            }
        });
    }

    public void searchAllNote() {
        try
        {
            EditText searchTitle = getView().findViewById(R.id.search_search_title);
            EditText searchSubject = getView().findViewById(R.id.search_search_subject);
            EditText searchOwner = getView().findViewById(R.id.search_search_owner);
            String searchTitleStr = searchTitle.getText().toString();
            String searchSubjectStr = searchSubject.getText().toString();
            String searchOwnerStr = searchOwner.getText().toString();

            final ArrayList<LecNote> lecNoteList = new ArrayList<>();
            for (int i = 0; i < allNote.size(); i++) {
                String title = allNote.get(i).getTitle();
                String subject = allNote.get(i).getSubject();
                String owner = allNote.get(i).getOwner();
                if (title.contains(searchTitleStr) && subject.contains(searchSubjectStr) && owner.contains(searchOwnerStr)) {
                    lecNoteList.add(allNote.get(i));
                }
            }
            LecNoteListAdapter lecNoteListAdapter = new LecNoteListAdapter(getActivity(), R.layout.fragment_file_list_item, lecNoteList);
            ListView lecNoteListView = getView().findViewById(R.id.search_lec_note_list);
            lecNoteListView.setAdapter(lecNoteListAdapter);
            lecNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment viewFragment = new ViewFragment();
                    Bundle bundle = new Bundle();
                    Log.d("test", lecNoteList.get(position).toString());
                    bundle.putSerializable("LecNote object", lecNoteList.get(position));
                    bundle.putSerializable("User object", user);
                    viewFragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, viewFragment).addToBackStack(null).commit();
                }
            });
        }
        catch (NullPointerException e)
        {
            Log.d("test", "catch null exception : " + e.getMessage());
            return;
        }
    }
}
