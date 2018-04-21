package com.tareq.kuetian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Boolean calledAuth = false;
    private Button signOut, changePic, browseButton;
    private ProgressBar loadingInfo;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ImageView proPic, editBioImg, editNameImg;
    private FirebaseStorage storage;
    private Uri proPicFilePath;
    private FirebaseUser user;
    private File currProPicFile;
    private StorageReference storageReference;
    private DatabaseReference usersRoot, usersTreeRoot;
    private TextView bioText, nameText;
    private FirebaseDatabase fullDatabase;
    private SingleInfoView infoGen, infoDept, infoBatch, infoRoll, infoBday, infoReli, infoPhone, infoCurrCity, infoHome;
    public static final int BIO = 1, NAME = 2, DEPT_NAME = 3, BATCH_NO = 4, ROLL_NO = 5, GENDER = 6, BIRTH_DAY = 7, RELIGION = 8, PHONE_NO = 9, CURRENT_CITY = 10, HOMETOWN = 11;
    private String guestUid, mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        InitializeFirebase();

        InitializeViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            mode = extras.getString("user");

        if (mode == null) {
            mode = "current";
        } else {
            guestUid = extras.getString("uid");
        }

        if (mode.equals("current")) {
            LoadCurrentUserProfile();
        } else {
            readOnlyView();
            LoadGuestProfile();
        }

    }

    private void LoadGuestProfile() {
        currProPicFile = new File(getApplicationContext().getFilesDir(), guestUid + ".jpg");
        DownLoadProPic(guestUid);
        LoadAllData(guestUid);
    }

    private void LoadCurrentUserProfile() {
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, Sign_in_activity.class));
                    finish();
                } else {
                    currProPicFile = new File(getApplicationContext().getFilesDir(), user.getUid() + ".jpg");

                    if (!calledAuth) {
                        calledAuth = true;
                        if (currProPicFile.exists()) {
                            OfflineLoadProPic();
                        } else {
                            DownLoadProPic(user.getUid());
                        }
                        LoadAllData(user.getUid());
                    }
                }
            }
        };
    }

    private void LoadAllData(String uid) {
        ReadData(uid, bioText, "Bio");
        ReadData(uid, nameText, "Full Name");
        ReadData(uid, infoDept, "Department");
        ReadData(uid, infoBatch, "Batch");
        ReadData(uid, infoRoll, "Roll");
        ReadData(uid, infoGen, "Gender");
        ReadData(uid, infoReli, "Religion");
        ReadData(uid, infoBday, "Birthday");
        ReadData(uid, infoPhone, "Phone");
        ReadData(uid, infoCurrCity, "Current City");
        ReadData(uid, infoHome, "Hometown");
    }

    private void readOnlyView() {
        changePic.setVisibility(View.GONE);
        signOut.setVisibility(View.GONE);
        browseButton.setVisibility(View.GONE);
        editBioImg.setVisibility(View.GONE);
        editNameImg.setVisibility(View.GONE);

        infoDept.ReadOnly();
        infoGen.ReadOnly();
        infoBatch.ReadOnly();
        infoRoll.ReadOnly();
        infoReli.ReadOnly();
        infoBday.ReadOnly();
        infoPhone.ReadOnly();
        infoCurrCity.ReadOnly();
        infoHome.ReadOnly();
    }

    private void InitializeFirebase() {
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fullDatabase = FirebaseDatabase.getInstance();
        usersRoot = fullDatabase.getReference("Users");
        usersTreeRoot = fullDatabase.getReference("UsersTree");
    }

    private void InitializeViews() {

        signOut = findViewById(R.id.sign_out_button);
        browseButton = findViewById(R.id.browse_button);

        proPic = findViewById(R.id.pro_pic);
        changePic = findViewById(R.id.change_pic);

        loadingInfo = findViewById(R.id.loadingProPic);


        nameText = findViewById(R.id.fullNameReadOnly);
        editNameImg = findViewById(R.id.editFullName);


        bioText = findViewById(R.id.bioReadOnly);
        editBioImg = findViewById(R.id.editBio);

        infoDept = findViewById(R.id.dept_InfoView);
        infoGen = findViewById(R.id.gender_InfoView);
        infoBatch = findViewById(R.id.batch_InfoView);
        infoRoll = findViewById(R.id.roll_InfoView);
        infoBday = findViewById(R.id.birthday_InfoView);
        infoReli = findViewById(R.id.religion_InfoView);
        infoPhone = findViewById(R.id.phone_no_infoView);
        infoCurrCity = findViewById(R.id.currentcity_InfoView);
        infoHome = findViewById(R.id.hometown_InfoView);


        InitializeEvents();
    }


    private void InitializeEvents() {
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateImageChooser();
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ManageBatchView.class));

            }
        });

        editBioImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEditTextView(bioText.getText().toString(), "Edit Bio", BIO, InputType.TYPE_CLASS_TEXT, null);
            }
        });

        editNameImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEditTextView(nameText.getText().toString(), "Edit Full Name", NAME, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, null);
            }
        });
        infoDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateSpinView(infoDept.getText(), "Select department", DEPT_NAME);
            }
        });
        infoBatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEditTextView(infoBatch.getText(), "Edit Batch", BATCH_NO, InputType.TYPE_CLASS_TEXT, "kK1234567890");
            }
        });
        infoRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEditTextView(infoRoll.getText(), "Edit Roll", ROLL_NO, InputType.TYPE_CLASS_NUMBER, "1234567890");
            }
        });
        infoGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateOptionView(infoGen.getText(), "Select Gender", GENDER);
            }
        });
        infoBday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDatePickerView(infoBday.getText(), "Set Birthday", BIRTH_DAY);
            }
        });
        infoReli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEditTextView(infoReli.getText(), "Edit Religion", RELIGION, InputType.TYPE_CLASS_TEXT, null);
            }
        });
        infoPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhoneVarifyAcitivity.class);
                intent.putExtra("prev_data", infoPhone.getText());
                intent.putExtra("from", PHONE_NO);
                startActivityForResult(intent, 3);
            }
        });
        infoCurrCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEditTextView(infoCurrCity.getText(), "Edit Current City", CURRENT_CITY, InputType.TYPE_CLASS_TEXT, null);
            }
        });
        infoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEditTextView(infoHome.getText(), "Edit Hometown", HOMETOWN, InputType.TYPE_CLASS_TEXT, null);
            }
        });

        infoPhone.setValueClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (infoPhone.getText() != null) {
                    if (!infoPhone.getText().isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + infoPhone.getText()));
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void CreateSpinView(String prevData, String name, int from) {
        Intent intent = new Intent(MainActivity.this, EditInfoView.class);
        intent.putExtra("prevData", prevData);
        intent.putExtra("name", name);
        intent.putExtra("from", from);
        intent.putExtra("operation", "spin");
        startActivityForResult(intent, 3);
    }

    private void CreateDatePickerView(String prevData, String name, int from) {
        Intent intent = new Intent(MainActivity.this, EditInfoView.class);
        intent.putExtra("prevData", prevData);
        intent.putExtra("name", name);
        intent.putExtra("from", from);
        intent.putExtra("operation", "date");
        startActivityForResult(intent, 3);
    }

    private void CreateEditTextView(String prevData, String name, int from, int inputType, String digits) {
        Intent intent = new Intent(MainActivity.this, EditInfoView.class);
        intent.putExtra("prevData", prevData);
        intent.putExtra("inputType", inputType);
        intent.putExtra("digits", digits);
        intent.putExtra("name", name);
        intent.putExtra("from", from);

        startActivityForResult(intent, 3);
    }

    private void CreateOptionView(String prevData, String name, int from) {
        Intent intent = new Intent(MainActivity.this, EditInfoView.class);
        intent.putExtra("prevData", prevData);
        intent.putExtra("name", name);
        intent.putExtra("from", from);
        intent.putExtra("operation", "radio");
        startActivityForResult(intent, 3);

    }

    private void ReadData(String Uid, final TextView read, String attribute) {
        try {
            usersRoot.child(Uid).child(attribute).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    read.setText(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {

        }
    }

    private void ReadData(String Uid, final SingleInfoView read, String attribute) {
        try {
            usersRoot.child(Uid).child(attribute).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    read.setText(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {

        }
    }


    private void UpdateData(String key, String value) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(user.getUid() + "/" + key, value);
        usersRoot.updateChildren(childUpdates);
    }

    private void UpdateTree(String key, final String oldval, String newval) {
        if (key.equals("Batch")) {
            usersTreeRoot.child(newval).child(infoDept.getText()).child(infoRoll.getText()).setValue(user.getUid());
            usersTreeRoot.child(oldval).child(infoDept.getText()).child(infoRoll.getText()).removeValue();
        } else if (key.equals("Department")) {
            usersTreeRoot.child(infoBatch.getText()).child(newval).child(infoRoll.getText()).setValue(user.getUid());
            usersTreeRoot.child(infoBatch.getText()).child(oldval).child(infoRoll.getText()).removeValue();
        } else if (key.equals("Roll")) {
            usersTreeRoot.child(infoBatch.getText()).child(infoDept.getText()).child(newval).setValue(user.getUid());
            usersTreeRoot.child(infoBatch.getText()).child(infoDept.getText()).child(oldval).removeValue();
        }
    }


    private void CreateImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, new File(Environment.getExternalStorageDirectory(),"temp_pro_pic.jpg"));
        Toast.makeText(getApplication(),  getExternalFilesDir(null).getAbsolutePath(), Toast.LENGTH_SHORT).show();
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }




    private void OfflineLoadProPic() {
        Bitmap myBitmap = BitmapFactory.decodeFile(currProPicFile.getAbsolutePath());
        proPic.setImageBitmap(myBitmap);
    }

    private void DownLoadProPic(String uid) {
        StorageReference ref = storageReference.child("pro_pic/" + uid);
        ref.getFile(currProPicFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                OfflineLoadProPic();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2) {

            if (resultCode == RESULT_OK) {
                if (data!=null){
                    File file = new File(Environment.getExternalStorageDirectory(),"temp_pro_pic.jpg");
                    String filePath=  Environment.getExternalStorageDirectory()+  "/temp_pro_pic.jpg";

                    Bitmap selectedImage =  BitmapFactory.decodeFile(filePath);
                    proPic.setImageBitmap(selectedImage);

                }
            }
/*
            proPicFilePath = data.getData();

            if (null != proPicFilePath) {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(proPicFilePath, "image/*");
                cropIntent.putExtra("crop", true);
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 300);
                cropIntent.putExtra("outputY", 300);
                cropIntent.putExtra("output", proPicFilePath);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, 1);



            }*/

        } else if (requestCode == 1) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
              //  proPic.setImageBitmap(selectedBitmap);
                uploadImage();
            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                String strRes = extras.getString("data");
                String strPrev = extras.getString("prevData");
                int from = extras.getInt("from");
                if (from == BIO) {
                    bioText.setText(strRes);
                    UpdateData("Bio", strRes);
                } else if (from == NAME) {
                    if (strRes != null)
                        if (strRes.trim().length() != 0) {
                            UpdateData("Full Name", strRes);
                            nameText.setText(strRes);
                        }
                } else if (from == DEPT_NAME) {
                    if (strRes != null)
                        if (strRes.trim().length() != 0) {
                            infoDept.setText(strRes);
                            UpdateData("Department", strRes);
                            UpdateTree("Department", strPrev, strRes);
                        }
                } else if (from == BATCH_NO) {
                    if (strRes != null)
                        if (strRes.trim().length() != 0) {
                            infoBatch.setText(strRes);
                            UpdateData("Batch", strRes);
                            UpdateTree("Batch", strPrev, strRes);
                        }
                } else if (from == ROLL_NO) {
                    if (strRes != null)
                        if (strRes.trim().length() != 0) {
                            infoRoll.setText(strRes);
                            UpdateData("Roll", strRes);
                            UpdateTree("Roll", strPrev, strRes);
                        }
                } else if (from == GENDER) {
                    infoGen.setText(strRes);
                    UpdateData("Gender", strRes);
                } else if (from == BIRTH_DAY) {
                    infoBday.setText(strRes);
                    UpdateData("Birthday", strRes);
                } else if (from == RELIGION) {
                    infoReli.setText(strRes);
                    UpdateData("Religion", strRes);
                } else if (from == PHONE_NO) {
                    infoPhone.setText(strRes);
                    UpdateData("Phone", strRes);
                } else if (from == CURRENT_CITY) {
                    infoCurrCity.setText(strRes);
                    UpdateData("Current City", strRes);
                } else if (from == HOMETOWN) {
                    infoHome.setText(strRes);
                    UpdateData("Hometown", strRes);
                }
            }
        }

    }


    private void uploadImage() {
        if (proPicFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("pro_pic/" + user.getUid());
            ref.putFile(proPicFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                    DownLoadProPic(user.getUid());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Image isn't selected. Please choose from Gallery.", Toast.LENGTH_SHORT).show();
        }
    }

    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (authListener != null)
            auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

}
