package com.akshatsharma.donationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewDonationActivity extends AppCompatActivity {
    TextView mDonatedItemTitle, mDonatedItemDescription, mDonorName;
    ImageView mDonatedItemImageView;
    Button mClaimItemButton;
    String image_url;
    FirebaseFirestore fStore;
    DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation);

        mDonatedItemTitle = findViewById(R.id.donatedItemTitleTextView);
        mDonatedItemDescription = findViewById(R.id.donatedItemDescriptionTextView);
        mDonorName = findViewById(R.id.donorNameTextView);

        mDonatedItemImageView = findViewById(R.id.donatedItemImageImageView);

        mClaimItemButton = findViewById(R.id.claimDonationButton);

        Intent intent = getIntent();
        String path = intent.getExtras().getString("donated_item_id");

        fStore = FirebaseFirestore.getInstance();
        reference = fStore.document(path);

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    mDonatedItemTitle.setText(documentSnapshot.getString("title"));
                    mDonatedItemDescription.setText(documentSnapshot.getString("description"));
//                    mDonorName.append(documentSnapshot.getString("donor_name"));
                    image_url = documentSnapshot.getString("image_url");
                    Picasso.get().load(image_url).into(mDonatedItemImageView);


                } else {
                    Toast.makeText(ViewDonationActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}