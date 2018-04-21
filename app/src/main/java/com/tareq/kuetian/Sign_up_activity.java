package com.tareq.kuetian;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Sign_up_activity extends AppCompatActivity {
    private EditText inputEmail, inputPassword, confirmPassword, fullName, batch, roll;
    private RadioButton male, female;
    private Spinner dept;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    static boolean err = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.sign_up_layout);


        InitializeFirebase();
        InitializeViews();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                err = false;
                final String email = inputEmail.getText().toString().trim();
                final String gender;
                String password = inputPassword.getText().toString().trim();
                String conpassword = confirmPassword.getText().toString().trim();
                final String fname = fullName.getText().toString();
                String batchno = batch.getText().toString();
                batchno = batchno.toLowerCase();
                final String deptName = dept.getSelectedItem().toString();
                final String rollNo = roll.getText().toString();

                if (fname.trim().length() == 0) {
                    fullName.setError("Enter Full Name");
                    fullName.requestFocus();
                    err = true;
                }
                if (batchno.trim().length() == 0) {
                    batch.setError("Enter Batch No");
                    batch.requestFocus();
                    err = true;
                } else {
                    boolean k = false;
                    for (int i = 0; i < batchno.length(); i++) {
                        if (batchno.charAt(i) == 'k') {
                            k = true;
                            break;
                        }
                    }
                    if (!k) {
                        batch.setError("Wrong Format. Ex: 2k15");
                    }
                }
                if (dept.getSelectedItemId() == 0) {
                    Toast.makeText(getApplicationContext(), "Select Department", Toast.LENGTH_SHORT).show();
                    dept.requestFocus();
                    err = true;
                }
                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Enter Email Address!");
                    inputEmail.requestFocus();
                    err = true;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Not a valid email!");
                    inputEmail.requestFocus();
                    err = true;
                }
                if (TextUtils.isEmpty(rollNo)) {
                    roll.setError("Enter Full Roll No");
                    roll.requestFocus();
                    err = true;
                }


                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Enter Password");
                    inputPassword.requestFocus();
                    err = true;
                } else if (password.length() < 6) {
                    inputPassword.requestFocus();
                    inputPassword.setError("Password Must be minimum 6 digits");
                    err = true;
                } else if (!password.equals(conpassword)) {
                    confirmPassword.setError("Password don't match!");
                    confirmPassword.requestFocus();
                    err = true;
                }
                if (male.isChecked())
                    gender = male.getText().toString();
                else
                    gender = female.getText().toString();

                if (err)
                    return;

                progressBar.setVisibility(View.VISIBLE);
                final String constBatchNo = batchno;

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Sign_up_activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(Sign_up_activity.this, "Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(Sign_up_activity.this, MainActivity.class));
                            String uid = auth.getCurrentUser().getUid();

                            DatabaseReference users = database.getReference("Users");
                            DatabaseReference root = users.child(uid);
                            DatabaseReference root1 = root.child("Full Name");
                            root1.setValue(fname);
                            DatabaseReference root2 = root.child("Batch");
                            root2.setValue(constBatchNo);
                            DatabaseReference root3 = root.child("Department");
                            root3.setValue(deptName);
                            DatabaseReference root4 = root.child("Roll");
                            root4.setValue(rollNo);
                            DatabaseReference root5 = root.child("Gender");
                            root5.setValue(gender);

                            DatabaseReference usersTree = database.getReference();
                            usersTree.child("UsersTree").child(constBatchNo).child(deptName).child(rollNo).setValue(uid);


                            finish();
                        }
                    }

                });

            }
        });

    }

    private void InitializeFirebase() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private void InitializeViews() {
        btnSignIn = findViewById(R.id.go_to_sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.username_sign_up_editText);
        inputPassword = findViewById(R.id.password_sign_up_editText);
        progressBar = findViewById(R.id.sign_up_progressBar);
        confirmPassword = findViewById(R.id.confirm_password_sign_up_editText);
        fullName = findViewById(R.id.full_name_sign_up_editText);
        batch = findViewById(R.id.batch_sign_up_editText);
        dept = findViewById(R.id.dept_sign_up_spinner);
        roll = findViewById(R.id.roll_sign_up_editText);
        male = findViewById(R.id.male_sign_up_radioButton);
        female = findViewById(R.id.female_sign_up_radioButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
