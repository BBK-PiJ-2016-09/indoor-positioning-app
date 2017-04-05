package com.bignerdranch.android.IamWatchingYou;


import java.util.Set;
import java.util.TreeSet;

public class PositionContainer {
    private static final PositionContainer ourInstance = new PositionContainer();
    private Set<landmark> savedPositions;
    public static PositionContainer getInstance() {
        return ourInstance;
    }

    private PositionContainer() {

        savedPositions = new TreeSet<landmark>() {{
            add(new landmark(12312, 1231231,"1stPointOfInterest"));
            add(new landmark(12312, 1231231,"2ndPointOfInterest"));
            add(new landmark(12312, 1231231,"3rdPointOfInterest"));
        }};
    }

    public String isNeighbor(Position position) {
        for(landmark landMark : savedPositions) {
            if(position.isNeighbor(landMark)) {
                return landMark.getDescription();
            }
        }
        return null;
    }
}
