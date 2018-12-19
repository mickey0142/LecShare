package com.kairanpa.se.lecshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapter.LecNoteListAdapter;
import model.LecNote;
import model.LecNotePack;
import model.User;

public class SearchResultFragment extends Fragment {
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    ArrayList<LecNote> allNote;
    String searchTitle = "";
    String searchSubject = "";
    String searchOwner = "";
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        allNote = ((LecNotePack) bundle.getSerializable("allNote")).getLecNoteList();
        searchTitle = bundle.getString("searchTitle");
        searchSubject = bundle.getString("searchSubject");
        searchOwner = bundle.getString("searchOwner");
        if (searchTitle == null)
        {
            searchTitle = "";
        }
        if (searchSubject == null)
        {
            searchSubject = "";
        }
        if (searchOwner == null)
        {
            searchOwner = "";
        }
        Log.d("test", "allNote : " + allNote);
        Log.d("test", "user : " + user);
        Log.d("test", "searchTitle : " + searchTitle);
        Log.d("test", "searchSubject : " + searchSubject);
        Log.d("test", "searchOwner : " + searchOwner);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkAuthen();
        initOrderBy();
        showSearchResult();
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

    public void initOrderBy()
    {
        Spinner dropDown = getView().findViewById(R.id.search_result_drop_down);
        String[] orderType = new String[]{"title", "subject", "time", "score"};
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, orderType);
        dropDown.setAdapter(orderAdapter);
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    queryOrderBy("title");
                }
                else if (position == 1)
                {
                    queryOrderBy("subject");
                }
                else if (position == 2)
                {
                    queryOrderBy("time");
                }
                else if (position == 3)
                {
                    queryOrderBy("score");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showSearchResult()
    {
        final ArrayList<LecNote> lecNoteList = new ArrayList<>();
        for (int i = 0; i < allNote.size(); i++) {
            String title = allNote.get(i).getTitle();
            String subject = allNote.get(i).getSubject();
            String owner = allNote.get(i).getOwner();
            if (title.contains(searchTitle) && subject.contains(searchSubject) && owner.contains(searchOwner))
            {
                lecNoteList.add(allNote.get(i));
            }
        }
        try {
            LecNoteListAdapter lecNoteListAdapter = new LecNoteListAdapter(getActivity(), R.layout.fragment_file_list_item, lecNoteList);
            ListView lecNoteListView = getView().findViewById(R.id.search_result_lec_note_list);
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
            Log.d("test", "catch NullPointerException : " + e.getMessage());
        }
    }

    public void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.search_result_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void queryOrderBy(String orderBy)
    {
        try {
            final ProgressBar progressBar = getView().findViewById(R.id.search_result_progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            final ListView resultListView = getView().findViewById(R.id.search_result_lec_note_list);
            resultListView.setVisibility(View.GONE);
            Query query = fbStore.collection("LecNote");
            if (orderBy.equals("score"))
            {
                query = fbStore.collection("LecNote")
                        .orderBy("score", Query.Direction.DESCENDING)
                        .orderBy("uploadTimeStamp", Query.Direction.DESCENDING)
                        .orderBy("title", Query.Direction.ASCENDING);
            }
            else if (orderBy.equals("subject")) {
                query = fbStore.collection("LecNote").orderBy("subject", Query.Direction.ASCENDING);
            } else if (orderBy.equals("title")) {
                query = fbStore.collection("LecNote").orderBy("title", Query.Direction.ASCENDING);
            } else if (orderBy.equals("time")) {
                query = fbStore.collection("LecNote").orderBy("uploadTimeStamp", Query.Direction.DESCENDING);
            }

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        final ArrayList<LecNote> lecNoteList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LecNote lecNote = document.toObject(LecNote.class);
                            lecNoteList.add(lecNote);
                        }
                        allNote = (ArrayList<LecNote>) lecNoteList.clone();
                        showSearchResult();
                    }
                    progressBar.setVisibility(View.GONE);
                    resultListView.setVisibility(View.VISIBLE);
                }
            });
        }
        catch (NullPointerException e)
        {
            Log.d("test", "catch null exception : " + e.getMessage());
            return;
        }
    }

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.search_result_toolbar);
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
