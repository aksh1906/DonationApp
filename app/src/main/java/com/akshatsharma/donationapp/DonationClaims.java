package com.akshatsharma.donationapp;

public class DonationClaims {
    private String user_name;
    private String reason;
    private String status;

    public DonationClaims() {
        //
    }

    public DonationClaims(String user_name, String reason, String status) {
        this.user_name = user_name;
        this.reason = reason;
        this.status = status;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }
}
