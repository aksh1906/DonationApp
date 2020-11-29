package com.akshatsharma.donationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AddDonationActivity extends AppCompatActivity {
    ImageView mImageView;
    Button mSubmitButton, mAddImageButton;
    EditText mDonationTitle, mDonationDescription;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String itemTitle, itemDescription, donorID;
    StorageReference storageReference;
    DocumentReference userReference;
    ProgressBar mProgressBar;
    Uri uploadImageUri;
    String downloadImageUri, donorName;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation);

        toolbar = findViewById(R.id.addDonationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a Donation");


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mDonationTitle = findViewById(R.id.donationTitleEditText);
        mDonationDescription = findViewById(R.id.donationDescriptionEditText);

        mSubmitButton = findViewById(R.id.submitDonationButton);
        mAddImageButton = findViewById(R.id.addDonationImageButton);

        mImageView = findViewById(R.id.donatedItemImageView);

        mProgressBar = findViewById(R.id.donationProgressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        donorID = fAuth.getCurrentUser().getUid();
        getUserFullName();

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                itemTitle = mDonationTitle.getText().toString();
                itemDescription = mDonationDescription.getText().toString();

                StorageReference fileReference = storageReference.child("donated_items/" + fAuth.getCurrentUser().getUid() + "/" + itemTitle + ".jpg");
                fileReference.putFile(uploadImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadImageUri = uri.toString();
                                Map<String, Object> donation = new HashMap<>();
                                donation.put("donor_id", donorID);
                                donation.put("donor_name", donorName);
                                donation.put("title", itemTitle);
                                donation.put("description", itemDescription);
                                donation.put("image_url", downloadImageUri);
                                donation.put("donated_status", 0);
                                donation.put("timestamp", FieldValue.serverTimestamp());


                                fStore.collection("donated_items").add(donation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(AddDonationActivity.this, "Item Successfully added for donation.", Toast.LENGTH_SHORT).show();
                                        Log.d("", "Document snapshot added with id " + documentReference.getId());
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });

                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK) {
                uploadImageUri = data.getData();
                mImageView.setImageURI(uploadImageUri);
                mImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getUserFullName() {
        userReference = fStore.collection("users").document(donorID);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                donorName = documentSnapshot.getString("fullname");
            }
        });
    }

}