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
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {
    private FirebaseAuth fbAuth;
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
    }

    private void initRegister(){
        Button _regisBtn = getView().findViewById(R.id.register_register_button);
        _regisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText _username = getView().findViewById(R.id.register_username);
                EditText _email = getView().findViewById(R.id.register_email);
                EditText _stdNumber = getView().findViewById(R.id.register_std_number);
                EditText _passowrd = getView().findViewById(R.id.register_password);
                EditText _rePassword = getView().findViewById(R.id.register_rePassword);

                String _usernameStr = _username.getText().toString();
                String _emailStr = _email.getText().toString();
                String _stdNumberStr = _stdNumber.getText().toString();
                String _passwordStr = _passowrd.getText().toString();
                String _repasswordStr = _rePassword.getText().toString();

                if(_usernameStr.isEmpty() || _emailStr.isEmpty() || _stdNumberStr.isEmpty() || _passwordStr.isEmpty() || _repasswordStr.isEmpty()){
                    Toast.makeText(getActivity(), "Can't be blank", Toast.LENGTH_LONG).show();
                    Log.e("REGISTER", "Can't be blank.");
                }
                else if (_passwordStr.equals(_repasswordStr)){
                    Toast.makeText(getActivity(), "Password and Re-Password not equal.", Toast.LENGTH_LONG).show();
                    Log.e("REGISTER", "Not equal.");
                }
                else if (_passwordStr.length() > 6){
                    Toast.makeText(getActivity(), "Please fill Password at least 6 or more.", Toast.LENGTH_LONG).show();
                    Log.e("REGISTER", "Password at least 6 or more.");
                }
                else{
                    fbAuth.createUserWithEmailAndPassword(_emailStr, _passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            sendVerifyEmail(authResult.getUser());
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new LoginFragment()).addToBackStack(null).commit();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("REGISTER", "Register fail.");
                        }
                    });
                }
            }
        });
    }

    private void sendVerifyEmail(FirebaseUser _email){
        _email.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) { }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) { }
        });
    }
}
