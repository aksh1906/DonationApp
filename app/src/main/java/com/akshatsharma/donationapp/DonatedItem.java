package com.akshatsharma.donationapp;

public class DonatedItem {
    private String title;
    private String description;

    public DonatedItem() {
        // empty constructor
    }

    public DonatedItem(String donatedItemTitle, String donatedItemDescription) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
