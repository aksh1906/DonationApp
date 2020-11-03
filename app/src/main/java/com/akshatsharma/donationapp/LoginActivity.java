package com.akshatsharma.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText mUsername, mPassword;
    Button mLoginButton;
    TextView mRegisterText;
    ProgressBar progressBar;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = findViewById(R.id.usernameEditText);
        mPassword = findViewById(R.id.passwordEditText);

        mLoginButton = findViewById(R.id.loginButton);

        mRegisterText = findViewById(R.id.dontHaveAccountTextView);

        progressBar = findViewById(R.id.loginProgressBar);

        fAuth = FirebaseAuth.getInstance();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

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

                progressBar.setVisibility(View.VISIBLE);

                // login

                fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Successfully logged In.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect details. Please try again.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        mRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });
    }
}