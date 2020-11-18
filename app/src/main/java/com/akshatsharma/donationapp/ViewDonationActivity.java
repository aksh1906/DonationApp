package com.akshatsharma.donationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewDonationActivity extends AppCompatActivity {
    TextView mDonatedItemTitle, mDonatedItemDescription, mDonorName, mDonationDate;
    ImageView mDonatedItemImageView;
    Button mClaimItemButton;
    String image_url;
    FirebaseFirestore fStore;
    DocumentReference reference;
    FirebaseAuth fAuth;
    Date donationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation);

        mDonatedItemTitle = findViewById(R.id.donatedItemTitleTextView);
        mDonatedItemDescription = findViewById(R.id.donatedItemDescriptionTextView);
        mDonorName = findViewById(R.id.donorNameTextView);
        mDonationDate = findViewById(R.id.itemDateTextView);

        mDonatedItemImageView = findViewById(R.id.donatedItemImageImageView);

        mClaimItemButton = findViewById(R.id.claimDonationButton);

        Intent intent = getIntent();
        String path = intent.getExtras().getString("donated_item_id");

        fStore = FirebaseFirestore.getInstance();
        reference = fStore.document(path);
        fAuth = FirebaseAuth.getInstance();

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("timestamp");
                    donationDate = timestamp.toDate();
                    DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
                    String dateString = dateFormat.format(donationDate);
                    mDonatedItemTitle.setText(documentSnapshot.getString("title"));
                    mDonationDate.append(dateString);
                    mDonatedItemDescription.setText(documentSnapshot.getString("description"));
                    mDonorName.append(documentSnapshot.getString("donor_name"));
                    image_url = documentSnapshot.getString("image_url");
                    Picasso.get().load(image_url).into(mDonatedItemImageView);


                } else {
                    Toast.makeText(ViewDonationActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mClaimItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("donated_status", 1);
                map.put("claimed_by", fAuth.getUid());
                reference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ViewDonationActivity.this, "Item claimed successfully.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });
    }
}