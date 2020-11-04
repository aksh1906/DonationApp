package com.akshatsharma.donationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DisplayAllDonationsActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private CollectionReference reference;
    private DonatedItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_donations);

        fStore = FirebaseFirestore.getInstance();
        reference = fStore.collection("donated_items");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = reference.orderBy("title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<DonatedItem> options = new FirestoreRecyclerOptions.Builder<DonatedItem>()
                .setQuery(query, DonatedItem.class)
                .build();

        adapter = new DonatedItemAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.donationRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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