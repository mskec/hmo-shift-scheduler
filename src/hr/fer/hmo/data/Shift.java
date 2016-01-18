package hr.fer.hmo.data;

import java.util.Set;

public class Shift {

    private int length;
    private Set<String> notFollowingShifts;

    public Shift(int length, Set<String> notFollowingShifts) {
        this.length = length;
        this.notFollowingShifts = notFollowingShifts;
    }

}
