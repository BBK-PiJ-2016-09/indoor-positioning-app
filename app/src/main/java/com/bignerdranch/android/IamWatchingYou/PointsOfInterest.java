package com.bignerdranch.android.IamWatchingYou;


import java.util.ArrayList;
import java.util.List;

public class PointsOfInterest {

    private static PointsOfInterest instance = new PointsOfInterest();
    private List<Position> pointsOfInterest = new ArrayList<>();

    /**
     * Stores our list of points of interest.
     *
     */
    private PointsOfInterest() {
        pointsOfInterest.add(new Position(51.521815,
                -0.130188, "Elevator"));
        pointsOfInterest.add(new Position(51.521807,
                -0.130199, "Library and bathroom corridor"));
        pointsOfInterest.add(new Position(51.522128,
                -0.130617, "Entrance to extension building."));
    }

    public List<Position> getPointsOfInterest(){
        return pointsOfInterest;
    }

    public static PointsOfInterest getInstance () {
        return  instance;
    }
}


