package hr.fer.hmo.data;

import java.util.List;

public class DaysOff {
  private String employeeId;
  private List<Integer> daysOff;      // index of days which employee is not working

  public DaysOff(String employeeId, List<Integer> daysOff) {
    this.employeeId = employeeId;
    this.daysOff = daysOff;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public List<Integer> getDaysOff() {
    return daysOff;
  }

}
