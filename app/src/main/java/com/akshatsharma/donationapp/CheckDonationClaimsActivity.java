package com.akshatsharma.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CheckDonationClaimsActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private CollectionReference reference, reference1;
    private DonationClaimsAdapter adapter;
    private Toolbar toolbar;
    TextView mReason;
    private DocumentReference documentReference1, documentReference2;
    FirebaseAuth fAuth;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_donation_claims);

        toolbar = findViewById(R.id.checkDonationClaimsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Check Donation Claims");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        path = intent.getExtras().getString("item_title");
        String item_path = intent.getExtras().getString("path");

        fStore = FirebaseFirestore.getInstance();
        reference = fStore.collection(path);
        documentReference1 = fStore.document(item_path);
        fAuth = FirebaseAuth.getInstance();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = reference.orderBy("user_name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<DonationClaims> options = new FirestoreRecyclerOptions.Builder<DonationClaims>()
                .setQuery(query, DonationClaims.class)
                .build();

        adapter = new DonationClaimsAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.donationClaimsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.getLong("donated_status").intValue() == 1) {
                        adapter.setOnItemClickListener(null);
                    } else {
                        adapter.setOnItemClickListener(new DonationClaimsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                                DonationClaims donationClaims = documentSnapshot.toObject(DonationClaims.class);
                                String reason = documentSnapshot.getString("reason");
                                AlertDialog.Builder builder = new AlertDialog.Builder(CheckDonationClaimsActivity.this);
                                builder.setTitle("Reason for claiming item");
                                final View reasonAlert = getLayoutInflater().inflate(R.layout.donation_reason_dialog, null);
                                builder.setView(reasonAlert);
                                EditText reasonAlertEditText = reasonAlert.findViewById(R.id.donationReasonEditText);
                                TextView reasonAlertTextView = reasonAlert.findViewById(R.id.donationReasonTextView);
                                reasonAlertEditText.setVisibility(View.INVISIBLE);
                                reasonAlertTextView.setVisibility(View.VISIBLE);
                                reasonAlertTextView.setText(reason);

                                String path1 = documentSnapshot.getReference().getPath();
                                DocumentReference documentReference = fStore.document(path1);

                                String claimantID = documentSnapshot.getString("user_id");
                                Map<String, Object> map = new HashMap<>();

                                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        map.put("status", "accepted");
                                        documentReference.update(map);
                                        map.clear();

                                        map.put("donated_status", 1);
                                        map.put("claimed_by", fAuth.getUid());
                                        documentReference1.update(map);
                                        map.clear();

                                        fStore.collection(claimantID).whereEqualTo("item_title", path)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        for(QueryDocumentSnapshot document: task.getResult()) {
                                                            DocumentSnapshot documentSnapshot = document;
                                                            String document_path = documentSnapshot.getReference().getPath();
                                                            documentReference2 = fStore.document(document_path);
                                                            Log.d("msg", document_path);
                                                            map.put("claim_status", 1);
                                                            documentReference2.update(map);
                                                            map.clear();

                                                        }
                                                    }
                                                });

                                        Toast.makeText(CheckDonationClaimsActivity.this, "Donation Accepted", Toast.LENGTH_SHORT).show();
                                        adapter.setOnItemClickListener(null);
                                        finish();
                                    }
                                }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        map.put("status", "rejected");
                                        documentReference.update(map);
                                        map.clear();
                                        Toast.makeText(CheckDonationClaimsActivity.this, "Donation Rejected", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                    }
                }
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