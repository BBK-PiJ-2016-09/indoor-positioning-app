package com.bignerdranch.android.IamWatchingYou;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.*;
import java.lang.reflect.Method;

import static org.junit.Assert.*;


public class PositionTest {
    private Position usersPosition;
    PositioningMethods positioningMethods = PositioningMethods.getInstance();
    @Before
    public void setUp() {
        usersPosition = new Position(51.52170448542311, -0.1300068345539113, "Pos");
    }


    @Test
    public void getLatitudeTest() {
        assertEquals(Math.toRadians(51.52170448542311), usersPosition.getLatitude(), 0);
    }

    @Test
    public void getLongitudeTest() {
        assertEquals(Math.toRadians(-0.1300068345539113), usersPosition.getLongitude(), 0);
    }


<<<<<<< HEAD
=======
    @Test
    public void assesPointsOfInterestAreFound() {
        assertEquals(positioningMethods.isNearPointsOfInterest(51.5216929085858,
                -0.12999587740967156, 3).size(), 3, 0);
        assertEquals(positioningMethods.isNearPointsOfInterest(51.5216929085858,
                -0.12991, 3).size(),0, 0);
    }
>>>>>>> BBK-PiJ-2016-09-debugging


}