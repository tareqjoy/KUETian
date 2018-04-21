package com.tareq.kuetian;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class PhoneVarifyAcitivity extends AppCompatActivity {
    private Button varify, edit;
    private EditText phoneEnter;
    private String str;
    private ProgressBar waiting;
    private CountDownTimer c;
    private int from;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_no_verify);

        varify = findViewById(R.id.verifyPhoneNo);
        phoneEnter = findViewById(R.id.phoneNoEditText);
        edit = findViewById(R.id.editPhoneNo);
        waiting = findViewById(R.id.time_remain_phone);

        c = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                float progress = ((120000 - millisUntilFinished) / 1000);
                waiting.setProgress((int) progress);
            }

            @Override
            public void onFinish() {


            }

        };

        getSupportActionBar().setTitle("Edit Phone No");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Bundle extras = getIntent().getExtras();
        str = extras != null ? extras.getString("prev_data") : null;
        from = extras.getInt("from");
        if (str == null) {
            EditMode(true);
        } else if (str.equals("")) {
            EditMode(true);
        } else {
            EditMode(false);
            phoneEnter.setText(str);
        }

        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBAck = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Wait(false);
                varify.setText("Verified");
                edit.setText("Save");
                edit.setEnabled(true);
                waiting.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                varify.setText("Verify");
                Wait(false);
                EditMode(true);
                Toast.makeText(getApplicationContext(), "Unable verify the phone number", Toast.LENGTH_SHORT).show();
            }
        };
        varify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneEnter.getText().toString().length() == 11) {
                    Wait(true);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+880" + phoneEnter.getText().toString(), 120, TimeUnit.SECONDS, PhoneVarifyAcitivity.this, mCallBAck);
                } else

                    phoneEnter.setError("Invalid Phone Number!");
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit.getText().toString().equals("Edit"))
                    EditMode(true);
                else {
                    Bundle conData = new Bundle();
                    conData.putString("data", phoneEnter.getText().toString());
                    conData.putInt("from", from);
                    Intent intent = new Intent();
                    intent.putExtras(conData);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void Wait(Boolean b) {
        if (b) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            waiting.setProgress(0);
            waiting.setVisibility(View.VISIBLE);
            varify.setEnabled(false);
            edit.setEnabled(false);
            phoneEnter.setEnabled(false);
            varify.setText("Verifying");
            c.start();
        } else {
            c.cancel();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            waiting.setVisibility(View.INVISIBLE);
        }
    }

    private void EditMode(boolean b) {
        if (b) {
            varify.setEnabled(true);
            edit.setEnabled(false);
            phoneEnter.setEnabled(true);
        } else {
            varify.setEnabled(false);
            edit.setEnabled(true);
            phoneEnter.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {

       if(varify.getText().toString().toLowerCase().equals("verified")){
            AlertDialog diaBox = AskOption();
            diaBox.show();
        } else if(!varify.getText().toString().toLowerCase().equals("verifying")){
           super.onBackPressed();
       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (varify.getText().toString().toLowerCase().equals("verified")) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            } else{
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Cancel")
                .setMessage("Do you want to cancel the process?")
                .setIcon(R.drawable.warning)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        PhoneVarifyAcitivity.this.finish();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}
