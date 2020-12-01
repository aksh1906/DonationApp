package com.akshatsharma.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    DocumentReference reference, userReference;
    FirebaseAuth fAuth;
    Date donationDate;
    Toolbar toolbar;
    String userName, userID, donorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation);

        toolbar = findViewById(R.id.viewDonationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Donation details");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        userID = fAuth.getCurrentUser().getUid();
        getUserFullName();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewDonationActivity.this);
                builder.setTitle("Reason for claiming?");
                final View reasonAlert = getLayoutInflater().inflate(R.layout.donation_reason_dialog, null);
                builder.setView(reasonAlert);
                EditText reasonAlertEditText = reasonAlert.findViewById(R.id.donationReasonEditText);
                TextView reasonAlertTextView = reasonAlert.findViewById(R.id.donationReasonTextView);
                reasonAlertEditText.setVisibility(View.VISIBLE);
                reasonAlertTextView.setVisibility(View.INVISIBLE);

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("item_title", documentSnapshot.getString("title"));
                                    map.put("user_name", userName);
                                    map.put("claim_status", 0);
                                    map.put("donor_id", documentSnapshot.getString("donor_id"));

                                    fStore.collection(userID).add(map);
                                }
                            }
                        });


                        sendDialogDataToActivity(reasonAlertEditText.getText().toString());
                    }
                }).setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void sendDialogDataToActivity(String reason) {
        String title = mDonatedItemTitle.getText().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userID);
        map.put("user_name", userName);
        map.put("reason", reason);
        map.put("status", "pending");

        fStore.collection(title).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(ViewDonationActivity.this, "Your claim for the donation has been noted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void getUserFullName() {
        userReference = fStore.collection("users").document(userID);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName = documentSnapshot.getString("fullname");
            }
        });
    }
}