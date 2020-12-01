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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AuctionedItemAdapter extends FirestoreRecyclerAdapter<AuctionedItem, AuctionedItemAdapter.AuctionedItemHolder> {
    private OnItemClickListener listener;

    public AuctionedItemAdapter(@NonNull FirestoreRecyclerOptions<AuctionedItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AuctionedItemHolder holder, int position, @NonNull AuctionedItem model) {
        holder.mAuctionedItemTitle.setText(model.getTitle());
        holder.mAuctionedItemDescription.setText(model.getDescription());
        int bid_amount = model.getBid_amount();
        String bid = "Current Bid: " + Integer.toString(bid_amount);
        holder.mCurrentBid.setText(bid);
        Picasso.get().load(model.getImage_url()).into(holder.mItemImageView);
        Date date = Calendar.getInstance().getTime();
        if(model.getEnd_date().before(date) ) {
            holder.mAuctionEndedTextView.setVisibility(View.VISIBLE);
            holder.mCurrentBid.setVisibility(View.INVISIBLE);
        }
    }

    @NonNull
    @Override
    public AuctionedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.auction_item_card, parent, false);
        return new AuctionedItemHolder(v);
    }

    class AuctionedItemHolder extends RecyclerView.ViewHolder {
        TextView mAuctionedItemTitle, mAuctionedItemDescription, mCurrentBid, mAuctionEndedTextView;
        ImageView mItemImageView;

        public AuctionedItemHolder(@NonNull View itemView) {
            super(itemView);
            mAuctionedItemTitle = itemView.findViewById(R.id.auctionTitleTextView);
            mAuctionedItemDescription = itemView.findViewById(R.id.auctionDescriptionTextView);
            mCurrentBid = itemView.findViewById(R.id.currentBidTextView);
            mItemImageView = itemView.findViewById(R.id.auctionItemImageView);
            mAuctionEndedTextView = itemView.findViewById(R.id.auctionEndedTextView);

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
