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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import model.User;

public class SearchUserFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    User user;
    ArrayList<User> userList = new ArrayList<>();

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
        return inflater.inflate(R.layout.fragment_search_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showUserList();
        initSearchButton();
        initBackButton();
        initToolbar();
    }

    void showUserList()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.search_user_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        fbStore.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    ArrayList<User> temp = new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult())
                    {
                        User data = doc.toObject(User.class);
                        temp.add(data);
                    }
                    userList = (ArrayList<User>) temp.clone();
                    try {
                        ListView userListView = getView().findViewById(R.id.search_user_user_list);
                        UserAdapter userAdapter = new UserAdapter(getActivity(), R.layout.fragment_user_list_item, userList);
                        userListView.setAdapter(userAdapter);
                        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("User object", user);
                                bundle.putSerializable("Target object", userList.get(position));
                                Fragment fragment = new ProfileFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                            }
                        });
                    }
                    catch (NullPointerException e)
                    {
                        Log.d("test", "catch NullPointerException : " + e.getMessage());
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                    Log.d("test", "get user error : " + task.getException());
                }
            }
        });
    }

    void initSearchButton()
    {
        ImageView searchButton = getView().findViewById(R.id.search_user_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<User> result = new ArrayList<>();
                EditText username = getView().findViewById(R.id.search_user_username);
                String usernameStr = username.getText().toString();
                for (int i = 0; i < userList.size(); i++)
                {
                    if (userList.get(i).getUsername().contains(usernameStr))
                    {
                        result.add(userList.get(i));
                    }
                }
                try {
                    ListView userListView = getView().findViewById(R.id.search_user_user_list);
                    UserAdapter userAdapter = new UserAdapter(getActivity(), R.layout.fragment_user_list_item, result);
                    userListView.setAdapter(userAdapter);
                    userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("User object", user);
                            bundle.putSerializable("Target object", userList.get(position));
                            Fragment fragment = new ProfileFragment();
                            fragment.setArguments(bundle);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                        }
                    });
                }
                catch (NullPointerException e)
                {
                    Log.d("test", "catch NullPointerException : " + e.getMessage());
                }
            }
        });
    }

    void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.search_user_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.search_user_toolbar);
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
