package com.bignerdranch.android.IamWatchingYou;

/**
 * Objects of this class are going to be saved in our db.
 */

public class Records {
    private Position usersCurrentPosition;
    private long time;
    public Records(Position position, long time) {
        usersCurrentPosition = position;
        this.time = time;
    }

    public Position getUsersCurrentPosition() {
        return usersCurrentPosition;
    }

    public long getTime() {
        return time;
    }
}
