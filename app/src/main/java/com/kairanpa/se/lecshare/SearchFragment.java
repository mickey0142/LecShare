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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import model.LecNote;
import model.User;

public class SearchFragment extends Fragment {

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

        final Button orderByTitleButton = getView().findViewById(R.id.search_order_by_title);
        final Button orderBySubjectButton = getView().findViewById(R.id.search_order_by_subject);
        final Button orderByTimeButton = getView().findViewById(R.id.search_order_by_time);
        orderByTitleButton.setBackgroundColor(Color.CYAN);
        orderBySubjectButton.setBackgroundColor(Color.LTGRAY);
        orderByTimeButton.setBackgroundColor(Color.LTGRAY);

        orderByTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = "title";
                orderByTitleButton.setBackgroundColor(Color.CYAN);
                orderBySubjectButton.setBackgroundColor(Color.LTGRAY);
                orderByTimeButton.setBackgroundColor(Color.LTGRAY);
                queryOrderBy();
            }
        });

        orderBySubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = "subject";
                orderByTitleButton.setBackgroundColor(Color.LTGRAY);
                orderBySubjectButton.setBackgroundColor(Color.CYAN);
                orderByTimeButton.setBackgroundColor(Color.LTGRAY);
                queryOrderBy();
            }
        });

        orderByTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = "time";
                orderBySubjectButton.setBackgroundColor(Color.LTGRAY);
                orderByTitleButton.setBackgroundColor(Color.LTGRAY);
                orderByTimeButton.setBackgroundColor(Color.CYAN);
                queryOrderBy();
            }
        });

        Button searchButton = getView().findViewById(R.id.search_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchTextBox = getView().findViewById(R.id.search_search_box);
                String searchText = searchTextBox.getText().toString();
                final ArrayList<LecNote> lecNoteList = new ArrayList<>();
                for (int i = 0; i < allNote.size(); i++)
                {
                    if(allNote.get(i).getTitle().contains(searchText))
                    {
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
                        ft.replace(R.id.main_view, viewFragment).commit();
                    }
                });
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
                ft.replace(R.id.main_view, uploadFragment).commit();
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
                    allNote = (ArrayList<LecNote>) lecNoteList.clone();// make refresh button and set allNote again maybe move that into new function too
                    // maybe refactor this by copy all this about adapter into function
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
                            ft.replace(R.id.main_view, viewFragment).commit();
                        }
                    });
                }
            }
        });
    }
}
