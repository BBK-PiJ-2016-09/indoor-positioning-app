package com.bignerdranch.android.IamWatchingYou;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by vasileiosDavios on 05/04/2017.
 */
public class PositionTest {
    Position usersPosition;
    @Before
    public void setUp() throws Exception {
        usersPosition = new Position(51.52170448542311, -0.1300068345539113);
    }

    @Test
    public void getLatitude() throws Exception {
        assertEquals(51.52170448542311,usersPosition.getLatitude(), 0);
    }

    @Test
    public void getLongitude() throws Exception {
        assertEquals(-0.1300068345539113, usersPosition.getLongitude(), 0);
    }

}