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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AuctionDetailsActivity extends AppCompatActivity {
    EditText mAuctionedItemTitle, mAuctionedItemDescription, mStartingBid;
    ImageView imageView;
    Button mSubmitButton, mEditImageButton, mEditEndDateButton;
    String image_url;
    FirebaseFirestore fStore;
    DocumentReference reference;
    StorageReference storageReference;
    Uri uploadImageUri;
    String downloadImageUri;
    FirebaseAuth fAuth;
    Toolbar toolbar;
    int mYear, mMonth, mDay;
    Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_details);

        toolbar = findViewById(R.id.auctionDetailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Auction Details");


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuctionedItemTitle = findViewById(R.id.editAuctionTitleEditText);
        mAuctionedItemDescription = findViewById(R.id.editAuctionDescriptionEditText);
        imageView = findViewById(R.id.myAuctionedItemImageView);
        mSubmitButton = findViewById(R.id.updateAuctionButton);
        mEditImageButton = findViewById(R.id.editAuctionImageButton);
        mEditEndDateButton = findViewById(R.id.editAuctionEndDateButton);
        mStartingBid = findViewById(R.id.editStartingBidEditText);

        Intent intent = getIntent();
        String path = intent.getExtras().getString("auctioned_item_id");

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

        mEditEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AuctionDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mEditEndDateButton.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    mAuctionedItemTitle.setText(documentSnapshot.getString("title"));
                    mAuctionedItemDescription.setText(documentSnapshot.getString("description"));
                    Long startingBid = documentSnapshot.getLong("bid_amount");
                    mStartingBid.setText(Long.toString(startingBid));
                    Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("end_date");
                    Date endDate = timestamp.toDate();
                    DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
                    String dateString = dateFormat.format(endDate);
                    mEditEndDateButton.setText(dateString);
                    image_url = documentSnapshot.getString("image_url");
                    Picasso.get().load(image_url).into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(AuctionDetailsActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemTitle = mAuctionedItemTitle.getText().toString();
                String itemDescription = mAuctionedItemDescription.getText().toString();
                int bidStartAmount = Integer.parseInt(mStartingBid.getText().toString());

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String eDate = mEditEndDateButton.getText().toString();
                try {
                    endDate = formatter.parse(eDate);
                } catch(ParseException e) {
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
                                Map<String, Object> map = new HashMap<>();
                                map.put("title", mAuctionedItemTitle.getText().toString());
                                map.put("description", mAuctionedItemDescription.getText().toString());
                                map.put("image_url", downloadImageUri);
                                map.put("end_date", endDate);
                                map.put("bid_amount", bidStartAmount);

                                reference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AuctionDetailsActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
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