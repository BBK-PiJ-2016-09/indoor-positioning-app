package com.bignerdranch.android.IamWatchingYou;


import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton for positioning calculations and verification.
 */
public class PositioningMethods {

    /**
     * the TAG for logs.
     *
     */
    private static final String TAG = "PositioningMethods";

    private static PointsOfInterest pointsOfInterest = PointsOfInterest.getInstance();
    private static PositioningMethods ourInstance = new PositioningMethods();

    /**
     * Flag that determines whether a toast should be made or has already been.
     */
    private boolean toastFlag = true;

    private PositioningMethods() {}

    public static PositioningMethods getInstance() {
        return ourInstance;
    }

    /**
     * Getter for the toastFlag.
     * @return the toastFlag value.
     */
    public boolean getToastFlag() {
        return toastFlag;
    }

    /**
     * Sets the flag for the toast, this will be used to check whether
     * a toast should be made or has already been made and is unnecessary.
     * @param flagValue the value for the flag.
     */
    public void setToastFlag(boolean flagValue) {
        toastFlag = flagValue;
    }

    /**
     * Finds the distance between 2 points in meters.
     * @param p1lat the latitude of point 1 in radians
     * @param p1long the longitude of point 1 in radians
     * @param p2lat the latitude of point 2 in radians
     * @param p2long the longitude of point 2 in radians
     * @return the distance between the 2 points in meters
     */
    private double findDistance(double p1lat,
                                double p1long,
                                double p2lat,
                                double p2long) {

        //radius of the sphere
        final int earthRadiusInMeters = 6371009;
        double meanLatitude = (p1lat + p2lat) / 2;
        double deltaLatitude = (p1lat - p2lat);
        double deltaLongitude = (p1long - p2long);
        double sqrtArgs = Math.pow(deltaLatitude, 2) +
                Math.pow((Math.cos(meanLatitude) * deltaLongitude), 2);
        return earthRadiusInMeters * Math.sqrt(sqrtArgs);
    }

    /**
     * Compares a position with a latitude and longitude to check if they are
     * separated less than a certain threshold.
     *
     * @param position a position object
     * @param latitude a latitude (the user's latitude)
     * @param longitude a longitude (the user's longitude)
     * @return boolean whether the user is at a distance < threshold
     */
    private boolean areTheyCloseEnough(Position position,
                                       double latitude,
                                      double longitude,
                                       double threshold) {
        double distance = findDistance(latitude, longitude,
                position.getLatitude(), position.getLongitude());
        return distance < threshold;
    }

    /**
     *
     * @param latitude
     * @param longitude
     * @return a list of points of interest at a distance < than a threshold>
     */
    public  List<Position> isNearPointsOfInterest(double latitude,
                                                  double longitude, double threshold) {
        List<Position> pointsOfInterestNearby = new ArrayList<>();
        List<Position> pointsOfInterestFarEnough = new ArrayList<>();
        double latitudeRad = Math.toRadians(latitude);
        double longitudeRad = Math.toRadians(longitude);
        for(Position position: pointsOfInterest.getPointsOfInterest()) {
            if (areTheyCloseEnough(position, latitudeRad, longitudeRad, threshold)) {
                pointsOfInterestNearby.add(position);
            }
        }
        return pointsOfInterestNearby;
    }
}
