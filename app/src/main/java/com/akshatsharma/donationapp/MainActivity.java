package com.akshatsharma.donationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    FloatingActionButton mAddDonationFab, mAddAuctionFab;
    ExtendedFloatingActionButton mAddFab;
    TextView mAddDonationTextView, mAddAuctionTextView, mNameTextView, mShowAllTextView, mEmailTextView;
    Boolean allFabsVisible;
    NavigationView navigationView;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    DocumentReference documentReference;
    CollectionReference collectionReference;
    HomePageDonatedItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         toolbar = findViewById(R.id.mainActivityToolbar);
         setSupportActionBar(toolbar);

         fStore = FirebaseFirestore.getInstance();
         fAuth = FirebaseAuth.getInstance();
         collectionReference = fStore.collection("donated_items");

         navigationView = findViewById(R.id.mainActivityNavigationView);
         navigationView.setNavigationItemSelectedListener(this);

         mNameTextView = findViewById(R.id.drawerHeaderNameTextView);
         mEmailTextView = findViewById(R.id.drawerHeaderEmailTextView);
         mShowAllTextView = findViewById(R.id.showAllDonationsTextView);

//        String userId = fAuth.getCurrentUser().getUid();
//        documentReference = fStore.collection("users").document(userId);
//        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(documentSnapshot.exists()) {
//                    mNameTextView.setText(documentSnapshot.getString("fullname"));
//                    mEmailTextView.setText(documentSnapshot.getString("email"));
//                }
//            }
//        });

         mAddFab = findViewById(R.id.addFab);
         mAddAuctionFab = findViewById(R.id.addAuctionFab);
         mAddDonationFab = findViewById(R.id.addDonationFab);
         mAddDonationTextView = findViewById(R.id.addDonationTextView);
         mAddAuctionTextView = findViewById(R.id.addAuctionTextView);

         mAddDonationFab.setVisibility(View.GONE);
         mAddAuctionFab.setVisibility(View.GONE);
         mAddAuctionTextView.setVisibility(View.GONE);
         mAddDonationTextView.setVisibility(View.GONE);

         allFabsVisible = false;

         mAddFab.shrink();

         mAddFab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!allFabsVisible) {
                     mAddFab.extend();
                     mAddDonationFab.show();
                     mAddAuctionFab.show();
                     mAddAuctionTextView.setVisibility(View.VISIBLE);
                     mAddDonationTextView.setVisibility(View.VISIBLE);
                     allFabsVisible = true;
                 } else {
                     mAddDonationFab.hide();
                     mAddAuctionFab.hide();
                     mAddAuctionTextView.setVisibility(View.GONE);
                     mAddDonationTextView.setVisibility(View.GONE);
                     mAddFab.shrink();
                     allFabsVisible = false;
                 }
             }
         });

         mAddAuctionFab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getApplicationContext(), AddAuctionActivity.class));
             }
         });

         mAddDonationFab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getApplicationContext(), AddDonationActivity.class));
             }
         });


        drawerLayout = findViewById(R.id.navigation_drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        mShowAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayAllDonationsActivity.class);
                startActivity(intent);
            }
        });

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        Query query = collectionReference.whereEqualTo("donated_status", 0).orderBy("timestamp", Query.Direction.DESCENDING).limit(6);
        FirestoreRecyclerOptions<DonatedItem> options = new FirestoreRecyclerOptions.Builder<DonatedItem>()
                .setQuery(query, DonatedItem.class)
                .build();

        adapter = new HomePageDonatedItemAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recentDonationsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new HomePageDonatedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                DonatedItem donatedItem = documentSnapshot.toObject(DonatedItem.class);
                String path = documentSnapshot.getReference().getPath();
                Intent intent = new Intent(MainActivity.this, ViewDonationActivity.class);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.startListening();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.myDonationsMenuItem) {
            startActivity(new Intent(getApplicationContext(), MyDonationsActivity.class));
        } else if(item.getItemId() == R.id.myAuctionsMenuItem) {
            // add
        } else if(item.getItemId() == R.id.viewAllDonationsMenuItem) {
            startActivity(new Intent(getApplicationContext(), DisplayAllDonationsActivity.class));
        } else if(item.getItemId() == R.id.viewAllAuctionsMenuItem) {
            // add
        } else if(item.getItemId() == R.id.logoutMenuItem) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        return true;
    }
}