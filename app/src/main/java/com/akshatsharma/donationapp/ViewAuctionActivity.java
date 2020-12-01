package com.akshatsharma.donationapp;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewAuctionActivity extends AppCompatActivity {
    TextView mAuctionedItemTitle, mAuctionedItemDescription, mSellerName, mAuctionEndDate;
    ImageView mAuctionedItemView;
    Button mBidButton;
    String image_url;
    FirebaseFirestore fStore;
    DocumentReference reference;
    FirebaseAuth fAuth;
    Date endDate;
    Toolbar toolbar;
    String bid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_auction);

        toolbar = findViewById(R.id.viewAuctionToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Auction details");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuctionedItemTitle = findViewById(R.id.auctionedItemTitleTextView);
        mAuctionedItemDescription = findViewById(R.id.auctionedItemDescriptionTextView);
        mSellerName = findViewById(R.id.sellerNameTextView);
        mAuctionEndDate = findViewById(R.id.auctionEndDateTextView);

        mAuctionedItemView = findViewById(R.id.auctionedItemImageImageView);

        mBidButton = findViewById(R.id.bidForItemButton);

        Intent intent = getIntent();
        String path = intent.getExtras().getString("auctioned_item_id");

        fStore = FirebaseFirestore.getInstance();
        reference = fStore.document(path);
        fAuth = FirebaseAuth.getInstance();

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Timestamp timestamp = (Timestamp) documentSnapshot.getData().get("end_date");
                    endDate = timestamp.toDate();
                    DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
                    String dateString = dateFormat.format(endDate);
                    mAuctionedItemTitle.setText(documentSnapshot.getString("title"));
                    mAuctionEndDate.append(dateString);
                    mAuctionedItemDescription.setText(documentSnapshot.getString("description"));
                    mSellerName.setText(documentSnapshot.getString("seller_name"));
                    image_url = documentSnapshot.getString("image_url");
                    Picasso.get().load(image_url).into(mAuctionedItemView);
                    Long currentBid = documentSnapshot.getLong("bid_amount");
                    bid = Long.toString(currentBid);
                    if(endDate.before(Calendar.getInstance().getTime())) {
                        mBidButton.setEnabled(false);
                        mBidButton.setText("Auction Ended");
                    } else if(documentSnapshot.getString("winner_id") == fAuth.getCurrentUser().getUid()) {
                        mBidButton.setEnabled(false);
                        mBidButton.setText("You are the leading bidder");
                    } else {
                        mBidButton.setEnabled(true);
                        mBidButton.setText("Make a bid");
                    }
                } else {
                    Toast.makeText(ViewAuctionActivity.this, "Document not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewAuctionActivity.this);
                builder.setTitle("Enter your bid");
                final View bidAlert = getLayoutInflater().inflate(R.layout.bid_alert_dialog, null);
                builder.setView(bidAlert);
                EditText bidAlertEditText = bidAlert.findViewById(R.id.bidAlertEditText);
                bidAlertEditText.setHint("Current Bid: " + bid);

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText bidAlertEditText = bidAlert.findViewById(R.id.bidAlertEditText);
                        sendDialogDataToActivity(bidAlertEditText.getText().toString());
                    }
                })
                        .setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void sendDialogDataToActivity(String data) {
        Map<String, Object> map = new HashMap<>();
        int bidAmount = Integer.parseInt(data);
        map.put("bid_amount", bidAmount);
        map.put("winner_id", fAuth.getUid());
        reference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ViewAuctionActivity.this, "Bid Submitted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}