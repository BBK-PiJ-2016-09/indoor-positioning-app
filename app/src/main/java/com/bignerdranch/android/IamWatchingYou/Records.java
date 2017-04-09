package com.bignerdranch.android.IamWatchingYou;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indooratlas.android.sdk.IALocation;

/**
 * A class to handle records from external input (in this case
 * indoor atlas).
 *
 * It handles saving to the database through getter methods (anything
 * with a getter will be saved).
 *
 */

public class Records {

    /**
     * The latitude in radians.
     */
    private double latitude;

    /**
     * the longitude in radians.
     */
    private double longitude;

    /**
     * Epoch timestamp (seconds).
     */
    private long time;

    /**
     * The firebase object to save to.
     */
    private DatabaseReference mDbRef;
    private static final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static final DatabaseReference dbRef = db.getReference("User_Records");
    /**
     * Default Constructor.
     *  The default behaviour of Records objects is
     *  <ul>
     *  <li>latitude provided in radians</li>
     *  <li>longitude in radians </li>
     *  <li>time unix epoch </li>
     *  <li>mDbRed a reference to a database </li>
     *  </ul>
    */
    public Records(IALocation location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.time = location.getTime();
        // save the record to the database when created.
        this.dbRef.push().setValue(this);
    }


    /**
     * Getter for the latitude.
     *
     * @return The latitude in radians.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter for the longitude.
     *
     * @return The longitude in radians.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Getter for time.
     *
     * @return timestamp in seconds.
     */
    public long getTime() {
        return time;
    }
}
