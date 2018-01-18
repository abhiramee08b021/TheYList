package com.example.abhirammoturi.theylist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText mName, mEmail, mPassword;
    private RadioGroup mGender, mPreferGender;
    private Button mRegister, mCancel;

    private String name, email, password, gender, preferGender;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mName = (EditText) findViewById(R.id.name);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mGender = (RadioGroup) findViewById(R.id.gender);
        mPreferGender = (RadioGroup) findViewById(R.id.preferGender);

        mCancel = (Button) findViewById(R.id.cancelButton);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });

        mRegister = (Button) findViewById(R.id.registerButton);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = mName.getText().toString();
                password = mPassword.getText().toString();
                email = mEmail.getText().toString();

                // Create user with email and password.

                final RadioButton g = (RadioButton) findViewById(mGender.getCheckedRadioButtonId());
                final RadioButton pg = (RadioButton) findViewById(mPreferGender.getCheckedRadioButtonId());
                gender = g.getText().toString();
                preferGender = pg.getText().toString();

                if (name.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Enter your name", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!email.toLowerCase().contains("@yale.edu")){
                    Toast.makeText(RegisterActivity.this, "Email should be a yale email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password should be atleast 6 characters long", Toast.LENGTH_LONG).show();
                    return;
                }

                if (gender == null) {
                    Toast.makeText(RegisterActivity.this, "Need to pick your gender", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "sign up error. Contact support@ylist.com", Toast.LENGTH_LONG).show();
                        }else{
                            final FirebaseUser u = mAuth.getCurrentUser();
                            sendEmailVerification(u);
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("gender", gender);
                            userInfo.put("preferGender", preferGender);
                            userInfo.put("email", email);
                            userInfo.put("profileImageUrl", "default");
                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });

            }
        });
    }

    private void sendEmailVerification(FirebaseUser u){
        u.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + "sample",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

}
