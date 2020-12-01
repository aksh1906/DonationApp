package com.akshatsharma.donationapp;

public class MyClaims {
    private String item_title;
    private int claim_status;

    public MyClaims() {

    }

    public MyClaims(String item_title, int claim_status) {
        this.item_title = item_title;
        this.claim_status = claim_status;
    }

    public String getItem_title() {
        return item_title;
    }

    public int getClaim_status() {
        return claim_status;
    }
}
