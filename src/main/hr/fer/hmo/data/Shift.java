package hr.fer.hmo.data;

import java.util.Set;

public class Shift {

  private String shiftId;
  private int length;
  private Set<String> notFollowingShifts;         // shiftIds of shifts that cannon be after this shift

  public Shift(String shiftId, int length, Set<String> notFollowingShifts) {
    this.shiftId = shiftId;
    this.length = length;
    this.notFollowingShifts = notFollowingShifts;
  }

  public String getShiftId() {
    return shiftId;
  }

  public int getLength() {
    return length;
  }

  public Set<String> getNotFollowingShifts() {
    return notFollowingShifts;
  }

}
