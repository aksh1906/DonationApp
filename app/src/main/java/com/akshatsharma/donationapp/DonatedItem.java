package com.akshatsharma.donationapp;

public class DonatedItem {
    private String title;
    private String description;
    private String image_url;
    private int donated_status;

    public DonatedItem() {
        // empty constructor
    }

    public DonatedItem(String donatedItemTitle, String donatedItemDescription, int donated_status) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.donated_status = donated_status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public int getDonated_status() {
        return donated_status;
    }
}
