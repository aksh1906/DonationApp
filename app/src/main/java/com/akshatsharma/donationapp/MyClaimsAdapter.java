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

public class MyClaimsAdapter extends FirestoreRecyclerAdapter<MyClaims, MyClaimsAdapter.MyClaimsHolder> {
    private OnItemClickListener listener;

    public MyClaimsAdapter(@NonNull FirestoreRecyclerOptions<MyClaims> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyClaimsHolder holder, int position, @NonNull MyClaims model) {
        holder.mItemTitle.setText(model.getItem_title());
        if(model.getClaim_status() == 0) {
            holder.mClaimedStatus.setText("Pending");
        } else if(model.getClaim_status() == 1) {
            holder.mClaimedStatus.setText("Accepted");
        } else if(model.getClaim_status() == -1) {
            holder.mClaimedStatus.setText("Rejected");
        }
    }

    @NonNull
    @Override
    public MyClaimsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_claim_card, parent, false);
        return new MyClaimsHolder(v);
    }

    class MyClaimsHolder extends RecyclerView.ViewHolder {
        TextView mItemTitle, mClaimedStatus;

        public MyClaimsHolder(@NonNull View itemView) {
            super(itemView);
            mItemTitle = itemView.findViewById(R.id.claimantNameTextView);
            mClaimedStatus = itemView.findViewById(R.id.claimStatusTextView);

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
