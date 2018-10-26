package com.kairanpa.se.lecshare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import model.LecNote;
import model.User;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLogin();
        initRegister();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        Button skipButton = getView().findViewById(R.id.login_skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User("tester", "99999999", "testereiei@mail.com");
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
    public void initLogin(){

        mAuth = FirebaseAuth.getInstance();
        final Button loginBtn = getView().findViewById(R.id.login_login_button);
        final ProgressBar progressBar = getView().findViewById(R.id.login_progress_bar);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText _email = getView().findViewById(R.id.login_email);
                EditText _password = getView().findViewById(R.id.login_password);
                String _emailStr = _email.getText().toString();
                String _passwordStr = _password.getText().toString();

                if (_emailStr.isEmpty() || _passwordStr.isEmpty()){
                    Log.e("LOGIN", "Email or Password is empty");
                    Toast.makeText(getActivity(), "Please fill E-mail and Password.", Toast.LENGTH_SHORT).show();
                }
                else {
                    loginBtn.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(_emailStr, _passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    Log.e("LOGIN", "Login: successful");
                                    fbStore.collection("User").whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful())
                                            {
                                                Log.d("test", "get user info from firestore");
                                                for (QueryDocumentSnapshot document : task.getResult())
                                                {
                                                    User user = document.toObject(User.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("User object", user);
                                                    Fragment homeFragment = new HomeFragment();
                                                    homeFragment.setArguments(bundle);
                                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                                    ft.replace(R.id.main_view, homeFragment).commit();
                                                }
                                            }
                                            else
                                            {
                                                Log.d("test", "get user from firestore failed");
                                                loginBtn.setEnabled(true);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "Please verify your email.", Toast.LENGTH_SHORT).show();
                                    loginBtn.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                            else{
                                Toast.makeText(getActivity(), "Please enter correct your E-mail and Password.", Toast.LENGTH_SHORT).show();
                                loginBtn.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    public void initRegister(){
        TextView _registerBtn = getView().findViewById(R.id.login_new_account);
        _registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("LOGIN", "Go to register");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new RegisterFragment()).addToBackStack(null).commit();
            }
        });
    }
}
