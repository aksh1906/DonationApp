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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DonationDetailsActivity extends AppCompatActivity {
    EditText mDonatedItemTitle, mDonatedItemDescription;
    ImageView imageView;
    Button mSubmitButton, mEditImageButton, mCheckClaimsButton;
    String image_url;
    FirebaseFirestore fStore;
    DocumentReference reference;
    StorageReference storageReference;
    Uri uploadImageUri;
    String downloadImageUri;
    FirebaseAuth fAuth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_details);

        toolbar = findViewById(R.id.editDonationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Donation Details");


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDonatedItemTitle = findViewById(R.id.editDonationTitleEditText);
        mDonatedItemDescription = findViewById(R.id.editDonationDescriptionEditText);
        imageView = findViewById(R.id.myDonatedItemImageView);
        mSubmitButton = findViewById(R.id.updateDonationButton);
        mEditImageButton = findViewById(R.id.editDonationImageButton);
        mCheckClaimsButton = findViewById(R.id.checkClaimsButton);

        Intent intent = getIntent();
        String path = intent.getExtras().getString("donated_item_id");

        fStore = FirebaseFirestore.getInstance();
        reference = fStore.document(path);
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        mEditImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    mDonatedItemTitle.setText(documentSnapshot.getString("title"));
                    mDonatedItemDescription.setText(documentSnapshot.getString("description"));
                    image_url = documentSnapshot.getString("image_url");
                    Picasso.get().load(image_url).into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(DonationDetailsActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCheckClaimsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mDonatedItemTitle.getText().toString();
                Intent intent = new Intent(DonationDetailsActivity.this, CheckDonationClaimsActivity.class);
                intent.putExtra("item_title", title);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemTitle = mDonatedItemTitle.getText().toString();
                StorageReference fileReference = storageReference.child("donated_items/" + fAuth.getCurrentUser().getUid() + "/" + itemTitle + ".jpg");
                fileReference.putFile(uploadImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadImageUri = uri.toString();
                                Map<String, Object> map = new HashMap<>();
                                map.put("title", mDonatedItemTitle.getText().toString());
                                map.put("description", mDonatedItemDescription.getText().toString());
                                map.put("image_url", downloadImageUri);
                                
                                reference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(DonationDetailsActivity.this, "Update Successful.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK) {
                uploadImageUri = data.getData();
                imageView.setImageURI(uploadImageUri);
            }
        }
    }
}