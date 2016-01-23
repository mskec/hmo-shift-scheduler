package hr.fer.hmo.problem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
  private int fitness;
  private Map<String, String[]> employeeShifts;     // <employeeId, day>
  private List<String> employeeIds;
  private int horizon;

  public Solution(List<String> employeeIds, int horizon) {
    this.employeeIds = employeeIds;
    this.horizon = horizon;
    employeeShifts = new HashMap<>(employeeIds.size());
    for (String employeeId : employeeIds) {
      employeeShifts.put(employeeId, new String[horizon]);
    }
    fitness = Integer.MAX_VALUE;
  }

  public int getFitness() {
    return fitness;
  }

  public void setFitness(int fitness) {
    this.fitness = fitness;
  }

  public String getShift(String employeeId, int day) {
    return employeeShifts.get(employeeId)[day];
  }

  public void setShift(String employeeId, int day, String shiftId) {
    employeeShifts.get(employeeId)[day] = shiftId;
  }

  public int getHorizon() {
    return horizon;
  }

  public List<String> getEmployeeIds() {
    return employeeIds;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String employeeId : employeeIds) {
      for (int day = 0; day < horizon-1; day++) {
        sb.append(printString(getShift(employeeId, day))).append('\t');
      }
      sb.append(printString(getShift(employeeId, horizon-1))).append('\n');
    }
    return sb.toString();
  }

  private static String printString(String str) {
    if (str == null) {
      return " ";
    }
    return str;
  }

  public void anulateEmployeeShifts(String employeeId) {
    String[] shifts = employeeShifts.get(employeeId);
    for (int i = shifts.length - 1; i >= 0; i--) {
      shifts[i] = null;
    }
  }
}
