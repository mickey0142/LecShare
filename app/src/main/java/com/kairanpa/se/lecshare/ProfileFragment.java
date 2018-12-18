package com.kairanpa.se.lecshare;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import adapter.LecNoteListAdapter;
import model.LecNote;
import model.User;

public class ProfileFragment extends Fragment {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    ArrayList<LecNote> allNote;
    User user;
    User target;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("User object");
        target = (User) bundle.getSerializable("Target object");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initAvatar();
        initText();
        initUpdateButton();
        initLecNoteList();
        initBackButton();
        initToolbar();
    }

    void initAvatar()
    {
        ImageView avatar = getView().findViewById(R.id.profile_avatar);
        if (user.getDocumentId().equals(target.getDocumentId()))
        {
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment fragment = new AvatarFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                }
            });
            Button avatarButton= getView().findViewById(R.id.profile_avatar_button);
            avatarButton.setVisibility(View.VISIBLE);
            avatarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User object", user);
                    Fragment fragment = new AvatarFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.main_view, fragment).addToBackStack(null).commit();
                }
            });
        }
        String pictureName = target.getAvatar();
        Log.d("test", "picture name is " + pictureName);
        switch (pictureName)
        {
            case "blue":
                avatar.setImageResource(R.drawable.avatar_blue);
                break;
            case "green":
                avatar.setImageResource(R.drawable.avatar_green);
                break;
            case "grey":
                avatar.setImageResource(R.drawable.avatar_grey);
                break;
            case "red":
                avatar.setImageResource(R.drawable.avatar_red);
                break;
            case "brown bunny":
                avatar.setImageResource(R.drawable.avatar_brown_bunny);
                break;
            case "pinky":
                avatar.setImageResource(R.drawable.avatar_pinky);
                break;
            case "wingky":
                avatar.setImageResource(R.drawable.avatar_wingky);
                break;
            case "zombie":
                avatar.setImageResource(R.drawable.avatar_zombie);
                break;
            case "spinny":
                avatar.setImageResource(R.drawable.avatar_spinny);
                break;
            case "feddy":
                avatar.setImageResource(R.drawable.avatar_feddy);
                break;
            case "gold ufo":
                avatar.setImageResource(R.drawable.avatar_gold_ufo);
                break;
            case "judy":
                avatar.setImageResource(R.drawable.avatar_judy);
                break;
            case "marico":
                avatar.setImageResource(R.drawable.avatar_marico);
                break;
            case "penny":
                avatar.setImageResource(R.drawable.avatar_penny);
                break;
            case "ufo":
                avatar.setImageResource(R.drawable.avatar_ufo);
                break;
            case "helicopty":
                avatar.setImageResource(R.drawable.avatar_helicopty);
                break;
            case "spikey":
                avatar.setImageResource(R.drawable.avatar_spikey);
                break;
        }
    }

    void initText()
    {
        EditText username = getView().findViewById(R.id.profile_username);
        EditText aboutMe = getView().findViewById(R.id.profile_about_me);
        username.setText(target.getUsername());
        aboutMe.setText(target.getAboutMe());
        Button updateButton = getView().findViewById(R.id.profile_update_button);
        if (!user.getUsername().equals(target.getUsername()))
        {
            username.setEnabled(false);
            aboutMe.setEnabled(false);
            updateButton.setVisibility(View.GONE);
        }
        TextView averageScore = getView().findViewById(R.id.profile_average_score);
        averageScore.setText("Average score : " + target.getAverageScore());
        fbStore.collection("User").orderBy("averageScore", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            int rank = 1;
                            double score = -1;
                            for (DocumentSnapshot doc : task.getResult())
                            {
                                User temp = doc.toObject(User.class);
                                if (score > 0)
                                {
                                    if (score == temp.getAverageScore())
                                    {
                                        rank -= 1;
                                    }
                                }
                                score = temp.getAverageScore();
                                if (user.getUsername().equals(temp.getUsername()))
                                {
                                    break;
                                }
                                rank += 1;
                            }
                            try
                            {
                                TextView ranking = getView().findViewById(R.id.profile_ranking);
                                ranking.setText("Score ranking : " + rank);
                            }
                            catch (NullPointerException e)
                            {
                                Log.d("test", "catch NullPointerException : " + e.getMessage());
                            }
                        }
                        else
                        {
                            Log.d("test", "get user for ranking fail : " + task.getException());
                            Toast.makeText(getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        TextView money = getView().findViewById(R.id.profile_money);
        money.setText("Money : " + target.getMoney());
        TextView uploadBy = getView().findViewById(R.id.profile_upload_by);
        uploadBy.setText("All note uploaded by " + user.getUsername());
    }

    void initUpdateButton()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.profile_update_progress_bar);
        Button updateButton = getView().findViewById(R.id.profile_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                progressBar.setVisibility(View.VISIBLE);
                EditText username = getView().findViewById(R.id.profile_username);
                EditText aboutMe = getView().findViewById(R.id.profile_about_me);
                String usernameStr = username.getText().toString();
                String aboutMeStr = aboutMe.getText().toString();
                final String oldUsername = user.getUsername();
                user.setUsername(usernameStr);
                user.setAboutMe(aboutMeStr);
                fbStore.collection("User").document(user.getDocumentId()).set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fbStore.collection("LecNote").whereEqualTo("owner", oldUsername).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if (task.isSuccessful())
                                                {
                                                    for (DocumentSnapshot doc : task.getResult())
                                                    {
                                                        fbStore.collection("LecNote").document(doc.getId())
                                                                .update("owner", user.getUsername())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(getContext(), "update success", Toast.LENGTH_SHORT).show();
                                                                        Log.d("test", "update profile success");
                                                                        initLecNoteList();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                Log.d("test", "update owner in order fail : " + e.getMessage());
                                                            }
                                                        });
                                                    }
                                                }
                                                else
                                                {
                                                    Toast.makeText(getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    Log.d("test", "get lecnote to update owner fail : " + task.getException());
                                                }
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("test", "update profile error : " + e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    void initLecNoteList()
    {
        final ProgressBar progressBar = getView().findViewById(R.id.profile_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final ListView lecNoteListView = getView().findViewById(R.id.profile_lec_note_list);
        fbStore.collection("LecNote").whereEqualTo("owner", target.getUsername())
                .orderBy("title", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    final ArrayList<LecNote> lecNoteList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        LecNote lecNote = document.toObject(LecNote.class);
                        lecNoteList.add(lecNote);
                    }
                    allNote = (ArrayList<LecNote>) lecNoteList.clone();
                    try {
                        LecNoteListAdapter lecNoteListAdapter = new LecNoteListAdapter(getActivity(), R.layout.fragment_file_list_item, allNote);
                        lecNoteListView.setFocusable(false);
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
                    }
                    catch (NullPointerException e)
                    {
                        Log.d("test", "catch NullPointerException : " + e.getMessage());
                    }
                }
                else
                {
                    Log.d("test", "get lec note list fail : " + task.getException());
                    Toast.makeText(getContext(), "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.profile_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void initToolbar()
    {
        Toolbar mTool = getView().findViewById(R.id.profile_toolbar);
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
