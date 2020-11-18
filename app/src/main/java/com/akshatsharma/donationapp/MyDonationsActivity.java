package com.akshatsharma.donationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyDonationsActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private CollectionReference reference;
    private DonatedItemAdapter adapter;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        reference = fStore.collection("donated_items");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = reference.whereEqualTo("donor_id", fAuth.getUid()).orderBy("title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<DonatedItem> options = new FirestoreRecyclerOptions.Builder<DonatedItem>()
                .setQuery(query, DonatedItem.class)
                .build();

        adapter = new DonatedItemAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.myDonationsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new DonatedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                DonatedItem donatedItem = documentSnapshot.toObject(DonatedItem.class);
                String path = documentSnapshot.getReference().getPath();
                Intent intent = new Intent(MyDonationsActivity.this, DonationDetailsActivity.class);
                intent.putExtra("donated_item_id", path);
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