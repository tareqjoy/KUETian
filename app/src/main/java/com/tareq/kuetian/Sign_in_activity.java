package com.tareq.kuetian;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by tareq on 10-Feb-18.
 */

public class Sign_in_activity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Sign_in_activity.this, MainActivity.class));
            finish();
        }

        // set the view now
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.sign_in_layout);



        inputEmail = (EditText) findViewById(R.id.username_sign_in_editText);
        inputPassword = (EditText) findViewById(R.id.password_sign_in_editText);
        progressBar = (ProgressBar) findViewById(R.id.sign_in_progressBar);
        btnSignup = (Button) findViewById(R.id.go_to_sign_up_button);
        btnLogin = (Button) findViewById(R.id.sign_in_button);
        btnReset = (Button) findViewById(R.id.forget_password_button);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Sign_in_activity.this, Sign_up_activity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(Sign_in_activity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                Boolean err=false;
                if (email.trim().length()==0) {
                    inputEmail.setError("Enter Email Address!");
                    inputEmail.requestFocus();
                    InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inputEmail, InputMethodManager.SHOW_IMPLICIT);
                    err=true;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    inputEmail.setError("Not a valid email!");
                    inputEmail.requestFocus();
                    InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inputEmail, InputMethodManager.SHOW_IMPLICIT);
                    err=true;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Enter Password");
                    inputPassword.requestFocus();
                    InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inputPassword, InputMethodManager.SHOW_IMPLICIT);
                    err=true;
                }
                else if (password.length()<6)
                {
                    inputPassword.setError("Password Must be minimum 6 digits");
                    inputPassword.requestFocus();
                    InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inputPassword, InputMethodManager.SHOW_IMPLICIT);
                    err=true;
                }
                if(err)
                {
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Sign_in_activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                        Toast.makeText(Sign_in_activity.this, "Failed to Login!", Toast.LENGTH_LONG).show();

                                } else {
                                    Intent intent = new Intent(Sign_in_activity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}
