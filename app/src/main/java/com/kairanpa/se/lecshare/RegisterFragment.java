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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import model.User;

public class RegisterFragment extends Fragment {
    private FirebaseAuth fbAuth;
    FirebaseFirestore fbStore = FirebaseFirestore.getInstance();
    String avatarName = "blue";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fbAuth = FirebaseAuth.getInstance();
        initRegister();
        initBackButton();
        initChooseAvatar();
    }

    private void initRegister(){
        Button _regisBtn = getView().findViewById(R.id.register_register_button);
        _regisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText _username = getView().findViewById(R.id.register_username);
                final EditText _email = getView().findViewById(R.id.register_email);
                EditText _password = getView().findViewById(R.id.register_password);
                EditText _rePassword = getView().findViewById(R.id.register_rePassword);

                final String _usernameStr = _username.getText().toString();
                final String _emailStr = _email.getText().toString();
                final String _passwordStr = _password.getText().toString();
                String _repasswordStr = _rePassword.getText().toString();

                if(_usernameStr.isEmpty() || _emailStr.isEmpty() || _passwordStr.isEmpty() || _repasswordStr.isEmpty())
                {
                    Toast.makeText(getActivity(), "Can't be blank", Toast.LENGTH_LONG).show();
                    Log.e("REGISTER", "Can't be blank.");
                }
                else if (!_passwordStr.equals(_repasswordStr))
                {
                    Toast.makeText(getActivity(), "Password and Re-Password not equal.", Toast.LENGTH_LONG).show();
                    Log.e("REGISTER", "Not equal.");
                }
                else if (_passwordStr.length() < 6){
                    Toast.makeText(getActivity(), "Please fill Password at least 6 or more.", Toast.LENGTH_LONG).show();
                    Log.e("REGISTER", "Password at least 6 or more.");
                }
                else{
                    final ProgressBar progressBar = getView().findViewById(R.id.register_progress_bar);
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "please wait...", Toast.LENGTH_SHORT).show();
                    fbStore.collection("User").whereEqualTo("username", _usernameStr).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        boolean duplicate = false;
                                        for (DocumentSnapshot doc : task.getResult())
                                        {
                                            duplicate = true;
                                            break;
                                        }
                                        if (duplicate)
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            Log.d("test", "username already exist");
                                            Toast.makeText(getContext(), "username already exist", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            fbAuth.createUserWithEmailAndPassword(_emailStr, _passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    User user = new User(_usernameStr, "aboutMe", _emailStr, avatarName);
                                                    final FirebaseUser authUser = authResult.getUser();
                                                    fbStore.collection("User").add(user)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    fbStore.collection("User").document(documentReference.getId())
                                                                            .update("documentId", documentReference.getId())
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d("cafe", "set document id for user success");
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressBar.setVisibility(View.GONE);
                                                                            Log.d("cafe", "set document id for user fail : " + e.getMessage());
                                                                        }
                                                                    });
                                                                    sendVerifyEmail(authUser);
                                                                }
                                                            });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Log.e("REGISTER", "Register fail : " + e.getMessage());
                                                    Toast.makeText(getContext(), "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        Log.d("test", "get user to check username fail");
                                    }
                                }
                            });
                }
            }
        });
    }

    private void sendVerifyEmail(FirebaseUser _email){
        final ProgressBar progressBar = getView().findViewById(R.id.register_progress_bar);
        _email.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("REGISTER", "send email success");
                Toast.makeText(getContext(), "register success", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new LoginFragment()).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.e("REGISTER", "send email fail : " + e.getMessage());
            }
        });
    }

    public void initBackButton()
    {
        ImageView backButton = getView().findViewById(R.id.register_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    void initChooseAvatar()
    {
        final ImageView avatar = getView().findViewById(R.id.register_avatar);
        ImageView leftButton = getView().findViewById(R.id.register_avatar_left);
        ImageView rightButton = getView().findViewById(R.id.register_avatar_right);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (avatarName)
                {
                    case "blue":
                        avatar.setImageResource(R.drawable.avatar_green);
                        avatarName = "green";
                        break;
                    case "green":
                        avatar.setImageResource(R.drawable.avatar_grey);
                        avatarName = "grey";
                        break;
                    case "grey":
                        avatar.setImageResource(R.drawable.avatar_red);
                        avatarName = "red";
                        break;
                    case "red":
                        avatar.setImageResource(R.drawable.avatar_blue);
                        avatarName = "blue";
                        break;
                }
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (avatarName)
                {
                    case "blue":
                        avatar.setImageResource(R.drawable.avatar_red);
                        avatarName = "red";
                        break;
                    case "green":
                        avatar.setImageResource(R.drawable.avatar_blue);
                        avatarName = "blue";
                        break;
                    case "grey":
                        avatar.setImageResource(R.drawable.avatar_green);
                        avatarName = "green";
                        break;
                    case "red":
                        avatar.setImageResource(R.drawable.avatar_grey);
                        avatarName = "grey";
                        break;
                }
            }
        });
    }
}
