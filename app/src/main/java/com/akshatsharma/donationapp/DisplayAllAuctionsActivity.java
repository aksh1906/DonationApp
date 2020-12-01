package com.akshatsharma.donationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class DisplayAllAuctionsActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private CollectionReference reference;
    private AuctionedItemAdapter adapter;
    Toolbar toolbar;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_auctions);

        toolbar = findViewById(R.id.displayAllAuctionsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Auctions");


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        reference = fStore.collection("auctioned_items");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = reference.whereEqualTo("auctioned_status", 0)
                .whereNotEqualTo("seller_id", fAuth.getUid())
                .orderBy("seller_id", Query.Direction.DESCENDING)
                .orderBy("title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<AuctionedItem> options = new FirestoreRecyclerOptions.Builder<AuctionedItem>()
                .setQuery(query, AuctionedItem.class)
                .build();

        adapter = new AuctionedItemAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.auctionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AuctionedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                AuctionedItem auctionedItem = documentSnapshot.toObject(AuctionedItem.class);
                String path = documentSnapshot.getReference().getPath();
                Intent intent = new Intent(DisplayAllAuctionsActivity.this, ViewAuctionActivity.class);
                intent.putExtra("auctioned_item_id", path);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}