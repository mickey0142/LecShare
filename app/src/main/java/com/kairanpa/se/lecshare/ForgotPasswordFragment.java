package com.kairanpa.se.lecshare;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment{

    private Button _resetBtn;
    private EditText _sendMail;

    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initResetButton();
    }

    public void initResetButton()
    {
        _resetBtn = getView().findViewById(R.id.resetBtn);
        _sendMail = getView().findViewById(R.id.sendMail);

        _resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMail = _sendMail.getText().toString();

                if (TextUtils.isEmpty(newMail))
                    Toast.makeText(getActivity(), "Email not empty.!", Toast.LENGTH_SHORT).show();
                else{

                    mAuth.sendPasswordResetEmail(newMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(getActivity(), "Please check your email account.", Toast.LENGTH_SHORT).show();

                            else{
                                String message = task.getException().getMessage();
                                Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
