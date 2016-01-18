package hr.fer.hmo.data;

import java.util.Map;

public class Employee {

  private String employeeId;
  private Map<String, Integer> maxShifts;         // <shiftId, maxdays>
  private int maxTotalMinutes;
  private int minTotalMinutes;
  private int maxConsecutiveShifts;
  private int minConsecutiveShifts;
  private int minConsecutiveDaysOff;
  private int maxWeekends;

  public Employee(String employeeId, Map<String, Integer> maxShifts, int maxTotalMinutes, int minTotalMinutes,
                  int maxConsecutiveShifts, int minConsecutiveShifts, int minConsecutiveDaysOff, int maxWeekends) {

    this.employeeId = employeeId;
    this.maxShifts = maxShifts;
    this.maxTotalMinutes = maxTotalMinutes;
    this.minTotalMinutes = minTotalMinutes;
    this.maxConsecutiveShifts = maxConsecutiveShifts;
    this.minConsecutiveShifts = minConsecutiveShifts;
    this.minConsecutiveDaysOff = minConsecutiveDaysOff;
    this.maxWeekends = maxWeekends;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public Map<String, Integer> getMaxShifts() {
    return maxShifts;
  }

  public int getMaxTotalMinutes() {
    return maxTotalMinutes;
  }

  public int getMinTotalMinutes() {
    return minTotalMinutes;
  }

  public int getMaxConsecutiveShifts() {
    return maxConsecutiveShifts;
  }

  public int getMinConsecutiveShifts() {
    return minConsecutiveShifts;
  }

  public int getMinConsecutiveDaysOff() {
    return minConsecutiveDaysOff;
  }

  public int getMaxWeekends() {
    return maxWeekends;
  }

}
