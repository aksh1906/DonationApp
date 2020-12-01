package com.akshatsharma.donationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DonatedItemAdapter extends FirestoreRecyclerAdapter<DonatedItem, DonatedItemAdapter.DonatedItemHolder> {
    private OnItemClickListener listener;

    public DonatedItemAdapter(@NonNull FirestoreRecyclerOptions<DonatedItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DonatedItemHolder holder, int position, @NonNull DonatedItem model) {
        holder.mDonatedItemTitle.setText(model.getTitle());
        holder.mDonatedItemDescription.setText((model.getDescription()));
        holder.mDonationStatus.setText("Claimed");
        if(model.getDonated_status() == 1) {
            holder.mDonationStatus.setVisibility(View.VISIBLE);
        } else {
            holder.mDonationStatus.setVisibility(View.INVISIBLE);
        }
        Picasso.get().load(model.getImage_url()).into(holder.mItemImageView);
    }

    @NonNull
    @Override
    public DonatedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_item_card, parent, false);
        return new DonatedItemHolder(v);
    }

    class DonatedItemHolder extends RecyclerView.ViewHolder {
        TextView mDonatedItemTitle, mDonatedItemDescription, mDonationStatus;
        ImageView mItemImageView;
        
        public DonatedItemHolder(@NonNull View itemView) {
            super(itemView);
            mDonatedItemTitle = itemView.findViewById(R.id.donationTitleTextView);
            mDonatedItemDescription = itemView.findViewById(R.id.donationDescriptionTextView);
            mDonationStatus = itemView.findViewById(R.id.donatedStatusTextView);
            mItemImageView = itemView.findViewById(R.id.itemImageView);

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
