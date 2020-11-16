package com.akshatsharma.donationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    FloatingActionButton mAddDonationFab, mAddAuctionFab;
    ExtendedFloatingActionButton mAddFab;
    TextView mAddDonationTextView, mAddAuctionTextView, mNameTextView;
    Boolean allFabsVisible;
    NavigationView navigationView;
    FirebaseFirestore fStore;
    DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         toolbar = findViewById(R.id.mainActivityToolbar);
         setSupportActionBar(toolbar);

//         fStore = FirebaseFirestore.getInstance();
//         reference = fStore.document()

         navigationView = findViewById(R.id.mainActivityNavigationView);
         navigationView.setNavigationItemSelectedListener(this);

         mNameTextView = findViewById(R.id.drawerHeaderNameTextView);
//         mNameTextView.setText(firestore.);

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

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.myDonationsMenuItem) {
            // add
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