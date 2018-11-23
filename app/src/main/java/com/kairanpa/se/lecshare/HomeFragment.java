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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class HomeFragment extends Fragment {
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    ArrayList<LecNote> allNote;
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showLecNoteList();
        initRefreshButton();
        initAdvanceSearchButton();
        initUploadButton();
        initSearchButton();
        initToolbar();
    }

    public void showLecNoteList()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.home_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final ListView lecNoteListView = getView().findViewById(R.id.home_lec_note_list);
        lecNoteListView.setVisibility(View.GONE);
        fbStore.collection("LecNote").orderBy("title", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final ArrayList<LecNote> lecNoteList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        LecNote lecNote = document.toObject(LecNote.class);
                        lecNoteList.add(lecNote);
                    }
                    allNote = (ArrayList<LecNote>) lecNoteList.clone();
                    try {
                        LecNoteListAdapter lecNoteListAdapter = new LecNoteListAdapter(getActivity(), R.layout.fragment_file_list_item, allNote);

                        lecNoteListView.setAdapter(lecNoteListAdapter);
                        lecNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Fragment viewFragment = new ViewFragment();
                                Bundle bundle = new Bundle();
                                Log.d("test", allNote.get(position).toString());
                                bundle.putSerializable("LecNote object", allNote.get(position));
                                bundle.putSerializable("User object", user);
                                viewFragment.setArguments(bundle);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.replace(R.id.main_view, viewFragment).addToBackStack(null).commit();
                            }
                        });
                        ImageView refreshButton = getView().findViewById(R.id.home_refresh_button);
                        refreshButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        lecNoteListView.setVisibility(View.VISIBLE);
                    }
                    catch (NullPointerException e)
                    {
                        Log.d("test", "catch NullPointerException : " + e.getMessage());
                    }
                }
                else
                {
                    ImageView refreshButton = getView().findViewById(R.id.home_refresh_button);
                    refreshButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    lecNoteListView.setVisibility(View.VISIBLE);
                    Log.d("test", "get lecnote error : " + task.getException().getMessage());
                    Toast.makeText(getContext(), "get lecnote error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initRefreshButton()
    {
        final ImageView refreshButton = getView().findViewById(R.id.home_refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLecNoteList();
                refreshButton.setEnabled(false);
            }
        });
    }

    public void initAdvanceSearchButton()
    {
        TextView advanceSearchButton = getView().findViewById(R.id.home_advance_search);
        advanceSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allNote == null)
                {
                    Log.d("test", "return");
                    return;
                }
                Fragment searchFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                LecNotePack lecNotePack = new LecNotePack(allNote);
                bundle.putSerializable("allNote", lecNotePack);
                searchFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.main_view, searchFragment).addToBackStack(null).commit();
            }
        });
    }

    public void initUploadButton()
    {
        Button uploadButton = getView().findViewById(R.id.home_upload_button);
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
    }

    public void initSearchButton()
    {
        ImageView searchButton = getView().findViewById(R.id.home_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allNote == null)
                {
                    Log.d("test", "return");
                    return;
                }
                EditText searchTitle = getView().findViewById(R.id.home_search_title);
                String searchTitleStr = searchTitle.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putSerializable("User object", user);
                bundle.putString("searchTitle", searchTitleStr);
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

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.home_toolbar);
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
                    fbAuth.signOut();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new LoginFragment())
                            .commit();
                }
                return false;
            }
        });
    }
}
