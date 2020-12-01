package com.akshatsharma.donationapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ClaimedDonationsActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private CollectionReference reference;
    private DocumentReference userReference;
    private MyClaimsAdapter adapter;
    private FirebaseAuth fAuth;
    private Toolbar toolbar;
    private String userName, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimed_donations);

        toolbar = findViewById(R.id.claimedDonationsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Claimed Donations");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        reference = fStore.collection(userID);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = reference.orderBy("item_title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<MyClaims> options = new FirestoreRecyclerOptions.Builder<MyClaims>()
                .setQuery(query, MyClaims.class)
                .build();

        adapter = new MyClaimsAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.claimedDonationsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyClaimsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClaimedDonationsActivity.this);
                builder.setTitle("Claim Status");
                final View statusAlert = getLayoutInflater().inflate(R.layout.claim_status_dialog, null);
                builder.setView(statusAlert);
                TextView statusTextView = statusAlert.findViewById(R.id.statusTextView);
                TextView contactTextView = statusAlert.findViewById(R.id.contactDetailsTextView);
                if(documentSnapshot.getLong("claim_status") == 1) {
                    statusTextView.setText("Your claim was Accepted");
                    contactTextView.setVisibility(View.VISIBLE);
                } else if(documentSnapshot.getLong("claim_status") == -1) {
                    statusTextView.setText("Your claim was Rejected");
                    contactTextView.setVisibility(View.GONE);
                } else {
                    statusTextView.setText("Your claim is Pending");
                    contactTextView.setVisibility(View.GONE);
                }

                builder.setPositiveButton("OK", null);

                AlertDialog dialog = builder.create();
                dialog.show();
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

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.startListening();
    }

}