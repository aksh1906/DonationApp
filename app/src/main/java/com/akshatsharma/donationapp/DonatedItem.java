package com.akshatsharma.donationapp;

public class DonatedItem {
    private String title;
    private String description;
    private String image_url;

    public DonatedItem() {
        // empty constructor
    }

    public DonatedItem(String donatedItemTitle, String donatedItemDescription) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
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

}
