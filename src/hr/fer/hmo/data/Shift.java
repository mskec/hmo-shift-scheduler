package hr.fer.hmo.data;

import java.util.Set;

public class Shift {

    private String shiftId;
    private int length;
    private Set<String> notFollowingShifts;         // shiftIds of shifts that cannon be after this shift

    public Shift(int length, Set<String> notFollowingShifts) {
        this.length = length;
        this.notFollowingShifts = notFollowingShifts;
    }

    public int getLength() {
        return length;
    }

    public Set<String> getNotFollowingShifts() {
        return notFollowingShifts;
    }

}
