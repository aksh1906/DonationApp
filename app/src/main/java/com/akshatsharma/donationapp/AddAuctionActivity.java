package com.akshatsharma.donationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAuctionActivity extends AppCompatActivity {
    ImageView mImageView;
    Button mSubmitButton, mAddImageButton, mStartDateButton, mEndDateButton;
    EditText mAuctionTitle, mAuctionDescription, mBidAmount;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String itemTitle, itemDescription, sellerID;
    StorageReference storageReference;
    DocumentReference userReference;
    ProgressBar mProgressBar;
    Uri uploadImageUri;
    String downloadImageUri, sellerName;
    Toolbar toolbar;
    int mYear, mMonth, mDay;
    int bidStartAmount;
    Date startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_auction);

        toolbar = findViewById(R.id.addAuctionToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add an Auction");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuctionTitle = findViewById(R.id.auctionTitleEditText);
        mAuctionDescription = findViewById(R.id.auctionDescriptionEditText);
        mBidAmount = findViewById(R.id.bidAmountEditText);

        mSubmitButton = findViewById(R.id.submitAuctionButton);
        mAddImageButton = findViewById(R.id.addAuctionImageButton);
        mStartDateButton = findViewById(R.id.auctionStartDateButton);
        mEndDateButton = findViewById(R.id.auctionEndDateButton);

        mImageView = findViewById(R.id.auctionItemImageView);

        mProgressBar = findViewById(R.id.auctionProgressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        sellerID = fAuth.getCurrentUser().getUid();
        getUserFullName();

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddAuctionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mStartDateButton.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddAuctionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mEndDateButton.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                itemTitle = mAuctionTitle.getText().toString();
                itemDescription = mAuctionDescription.getText().toString();
                bidStartAmount = Integer.parseInt(mBidAmount.getText().toString());

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String sDate = mStartDateButton.getText().toString();
                String eDate = mEndDateButton.getText().toString();
                try {
                    startDate = formatter.parse(sDate);
                    endDate = formatter.parse(eDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                StorageReference fileReference = storageReference.child("auctioned_items/" + fAuth.getCurrentUser().getUid() + "/" + itemTitle + ".jpg");
                fileReference.putFile(uploadImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadImageUri = uri.toString();
                                Map<String, Object> auction = new HashMap<>();
                                auction.put("seller_id", sellerID);
                                auction.put("seller_name", sellerName);
                                auction.put("title", itemTitle);
                                auction.put("description", itemDescription);
                                auction.put("image_url", downloadImageUri);
                                auction.put("auctioned_status", 0);
                                auction.put("bid_amount", bidStartAmount);
                                auction.put("start_date", startDate);
                                auction.put("end_date", endDate);
                                auction.put("timestamp", FieldValue.serverTimestamp());

                                fStore.collection("auctioned_items").add(auction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(AddAuctionActivity.this, "Item successfully put up for Auction.", Toast.LENGTH_SHORT).show();
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
        userReference = fStore.collection("users").document(sellerID);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                sellerName = documentSnapshot.getString("fullname");
            }
        });
    }
}