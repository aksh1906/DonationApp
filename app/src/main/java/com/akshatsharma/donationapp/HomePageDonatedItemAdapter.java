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

public class HomePageDonatedItemAdapter extends FirestoreRecyclerAdapter<DonatedItem, HomePageDonatedItemAdapter.HomePageDonatedItemHolder> {
    private OnItemClickListener listener;

    public HomePageDonatedItemAdapter(@NonNull FirestoreRecyclerOptions<DonatedItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HomePageDonatedItemHolder holder, int position, @NonNull DonatedItem model) {
        holder.mDonatedItemTitle.setText(model.getTitle());
        Picasso.get().load(model.getImage_url()).into(holder.mImageView);
    }

    @NonNull
    @Override
    public HomePageDonatedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_item_card, parent, false);
        return new HomePageDonatedItemHolder(v);
    }

    class HomePageDonatedItemHolder extends RecyclerView.ViewHolder {
        TextView mDonatedItemTitle;
        ImageView mImageView;

        public HomePageDonatedItemHolder(@NonNull View itemView) {
            super(itemView);
            mDonatedItemTitle = itemView.findViewById(R.id.homePageDonationTitle);
            mImageView = itemView.findViewById(R.id.homePageDonatedItemImage);

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
