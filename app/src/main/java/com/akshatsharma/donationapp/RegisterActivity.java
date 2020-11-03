package com.akshatsharma.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mUsername, mPassword, mFullName;
    Button mRegisterButton;
    TextView mLoginText;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullnameEditText);
        mUsername = findViewById(R.id.usernameEditText);
        mPassword = findViewById(R.id.passwordEditText);

        mRegisterButton = findViewById(R.id.registerButton);

        progressBar = findViewById(R.id.progressBar);

        mLoginText = findViewById(R.id.dontHaveAccountTextView);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String fullname = mFullName.getText().toString();

                if(TextUtils.isEmpty(username)) {
                    mUsername.setError("Username can't be empty.");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Password can't be empty.");
                    return;
                }

                if(password.length() < 6) {
                    mPassword.setError("Password needs to be at least 6 characters long.");
                }

                if(TextUtils.isEmpty(fullname)) {
                    mFullName.setError("Full Name required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register user in firebase
                fAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration completed successfully.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullname", fullname);
                            user.put("email", username);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Profile created for user " + userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration could not be completed.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        mLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}