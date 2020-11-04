package com.akshatsharma.donationapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {
//    TextView mFullName, mEmail;
//    FirebaseAuth fAuth;
//    FirebaseFirestore fStore;
//    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mFullName = findViewById(R.id.fullnameTextView);
//        mEmail = findViewById(R.id.emailIdTextView);
//
//        fAuth = FirebaseAuth.getInstance();
//        fStore = FirebaseFirestore.getInstance();
//
//        userID = fAuth.getCurrentUser().getUid();
//        DocumentReference documentReference = fStore.collection("users").document(userID);
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                mFullName.setText(documentSnapshot.getString("fullname"));
//                mEmail.setText(documentSnapshot.getString("email"));
//            }
//        });

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void gotoAddDonation(View view) {
        startActivity(new Intent(getApplicationContext(), AddDonationActivity.class));
    }
}