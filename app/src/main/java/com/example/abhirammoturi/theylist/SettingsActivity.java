package com.example.abhirammoturi.theylist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.app.AlertDialog.Builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText mName;
    private RadioGroup mGender, mPreferGender;
    private Button mSave, mCancel, mLogout, mDeleteAccount;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private String name, profileImageUrl, gender, preferGender;
    private Uri resultUri;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user == null){
                    Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mName = (EditText) findViewById(R.id.name);
        mGender = (RadioGroup) findViewById(R.id.gender);
        mPreferGender = (RadioGroup) findViewById(R.id.preferGender);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        userId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mSave = (Button) findViewById(R.id.save);
        mCancel = (Button) findViewById(R.id.cancel);
        mLogout = (Button) findViewById(R.id.logout);
        mDeleteAccount = (Button) findViewById(R.id.deleteAccount);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                finish();
                return;
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });

        mDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Builder builder = new Builder(SettingsActivity.this);
                builder.setTitle("Delete Account")
                        .setMessage("Are you sure?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Yes button clicked, do something
                                mAuth.getCurrentUser().delete().addOnCompleteListener(SettingsActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()){
                                            Toast.makeText(SettingsActivity.this, "Error Deleting User", Toast.LENGTH_LONG);
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    //Put up the Yes/No message box
                    Builder builder = new Builder(SettingsActivity.this);
                    builder.setTitle("Logout")
                            .setMessage("Are you sure?")
                            .setIcon(android.R.drawable.ic_delete)
                            .setPositiveButton("Yes Logout", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Yes button clicked, do something
                                    try {
                                        mAuth.signOut();
                                        Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } catch (Error e){
                                        Toast.makeText(SettingsActivity.this, "Error Logging Out User", Toast.LENGTH_LONG);
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

            }
        });

    }

    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        mName.setText(name);
                    }
                    if(map.get("gender")!=null){
                        gender = map.get("gender").toString();
                        RadioButton g;
                        switch (gender){
                            case "Male":
                                g = (RadioButton) findViewById(R.id.genderMale);
                                g.setChecked(true);
                                break;
                            case "Female":
                                g = (RadioButton) findViewById(R.id.genderFemale);
                                g.setChecked(true);
                                break;
                            case "Other":
                                g = (RadioButton) findViewById(R.id.genderOther);
                                g.setChecked(true);
                                break;
                        }
                    }
                    if(map.get("preferGender")!=null){
                        preferGender = map.get("preferGender").toString();
                        RadioButton g;
                        switch (preferGender){
                            case "Male":
                                g = (RadioButton) findViewById(R.id.preferGenderMale);
                                g.setChecked(true);
                                break;
                            case "Female":
                                g = (RadioButton) findViewById(R.id.preferGenderFemale);
                                g.setChecked(true);
                                break;
                            case "Other":
                                g = (RadioButton) findViewById(R.id.preferGenderOther);
                                g.setChecked(true);
                                break;
                        }

                    }
                    //Glide.clear(mProfileImage);
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch(profileImageUrl){
                            case "default":
                                // Load generic image based on gender
                                switch (gender) {
                                    case "Male":
                                        Glide.with(getApplication()).load(R.mipmap.generic_male).into(mProfileImage);
                                        break;
                                    case "Female":
                                        Glide.with(getApplication()).load(R.mipmap.generic_female).into(mProfileImage);
                                        break;
                                    case "Other":
                                        Glide.with(getApplication()).load(R.mipmap.generic_other).into(mProfileImage);
                                        break;
                                    default:
                                        Glide.with(getApplication()).load(R.mipmap.generic_other).into(mProfileImage);

                                }
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInformation() {
        name = mName.getText().toString();
        RadioButton g = (RadioButton) findViewById(mGender.getCheckedRadioButtonId());
        gender = g.getText().toString();

        RadioButton pg = (RadioButton) findViewById(mPreferGender.getCheckedRadioButtonId());
        preferGender = pg.getText().toString();
        Map userInfo = new HashMap();

        userInfo.put("name", name);
        userInfo.put("gender", gender);
        userInfo.put("preferGender", preferGender);
        mUserDatabase.updateChildren(userInfo);
        if(resultUri != null){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map userInfo = new HashMap();
                    userInfo.put("profileImageUrl", downloadUrl.toString());
                    mUserDatabase.updateChildren(userInfo);

                    finish();
                    return;
                }
            });
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
