package com.bignerdranch.android.IamWatchingYou;

/**
 * A class used to handle ppositions.
 */
public class Position {

    /**
     * The latitude of the position in radians.
     */
    private double latitude;

    /**
     * The longitude of the position in radians.
     */
    private double longitude;

    /**
     * A String describing the position.
     */
    private String description;

    /**
     * Constructor
     * @param latitude the latitude in degrees.
     * @param longitude the longitude in degrees.
     * @param description a string describing the position
     */
    public Position(double latitude,
                    double longitude,
                    String description) {
        this.latitude = Math.toRadians(latitude);
        this.longitude = Math.toRadians(longitude);
        this.description = description;
    }

    /**
     * Getter for latitude.
     *
     * @return the latitude in radians.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter for longitude
     *
     * @return the longitude in radians
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Getter for the description.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }
}