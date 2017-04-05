package com.bignerdranch.android.IamWatchingYou;


import java.text.DecimalFormat;

public class Position {

    private double latitude;
    private double longitude;
    private DecimalFormat format;
    private static final double THRESHOLD = 123123;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        format = new DecimalFormat("##.######");
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if(o instanceof Position) {
            Position usersPosition = (Position) o ;
            String c1 = format.format(latitude) + format.format(longitude);
            String c2 = format.format(usersPosition.latitude) +
                    format.format(usersPosition.longitude);
            return c1.equals(c2);
        }
       return false;
    }



    @Override
    public int hashCode() {
        String coordinates = format.format(latitude) + format.format(longitude);
        return 31*coordinates.hashCode();
    }

    public boolean isNeighbor(Object o) {
        if (o == null) {
            return false;
        }
        if(o instanceof Position) {
            Position usersPosition = (Position) o;
            if(Math.abs(this.latitude - this.latitude)< THRESHOLD && Math.abs(
                    this.longitude- this.longitude)< THRESHOLD
                    ) {
                return true;
            }
        }

        return false;

    }
    //Created for saving separately in our database
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
