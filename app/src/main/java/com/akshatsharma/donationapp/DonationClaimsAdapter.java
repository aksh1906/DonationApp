package com.akshatsharma.donationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonationClaimsAdapter extends FirestoreRecyclerAdapter<DonationClaims, DonationClaimsAdapter.DonationClaimsHolder> {
    private OnItemClickListener listener;

    public DonationClaimsAdapter(@NonNull FirestoreRecyclerOptions<DonationClaims> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DonationClaimsHolder holder, int position, @NonNull DonationClaims model) {
        holder.mUserName.setText(model.getUser_name());
        holder.mStatus.setText(model.getStatus());
    }

    @NonNull
    @Override
    public DonationClaimsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_claim_card, parent, false);
        return new DonationClaimsHolder(v);
    }

    class DonationClaimsHolder extends RecyclerView.ViewHolder {
        TextView mUserName, mStatus;

        public DonationClaimsHolder(@NonNull View itemView) {
            super(itemView);

            mUserName = itemView.findViewById(R.id.claimantNameTextView);
            mStatus = itemView.findViewById(R.id.claimStatusTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
