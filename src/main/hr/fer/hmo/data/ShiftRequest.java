package hr.fer.hmo.data;

public class ShiftRequest {

  private String employeeId;
  private int day;
  private String shiftId;
  private int weight;

  public ShiftRequest(String employeeId, int day, String shiftId, int weight) {
    this.employeeId = employeeId;
    this.day = day;
    this.shiftId = shiftId;
    this.weight = weight;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public int getDay() {
    return day;
  }

  public String getShiftId() {
    return shiftId;
  }

  public int getWeight() {
    return weight;
  }

}
