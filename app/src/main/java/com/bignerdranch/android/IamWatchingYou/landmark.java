package com.bignerdranch.android.IamWatchingYou;



public class landmark extends Position {

    private String description;

    public landmark(double latitude, double longitude, String description) {
        super(latitude,longitude);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
