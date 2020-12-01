package com.akshatsharma.donationapp;

import java.util.Date;

public class AuctionedItem {
    private String title;
    private String description;
    private int bid_amount;
    private String image_url;
    private Date end_date;

    public AuctionedItem() {
        // empty constructor
    }

    public AuctionedItem(String title, String description, int bid_amount, String image_url, Date end_date) {
        this.title = title;
        this.description = description;
        this.bid_amount = bid_amount;
        this.image_url = image_url;
        this.end_date = end_date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getBid_amount() {
        return bid_amount;
    }

    public String getImage_url() {
        return image_url;
    }

    public Date getEnd_date() {
        return end_date;
    }
}
