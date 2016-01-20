package hr.fer.hmo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Instance {

  private int horizon;
  private Map<String, Shift> shifts;              // <shiftId, Shift>
  private Map<String, Employee> employees;        // <employeeId, Employee>
  private Map<String, DaysOff> employeeDaysOff;   // <employeeId, DaysOff>
  private List<ShiftRequest> shiftOnRequests;
  private List<ShiftRequest> shiftOffRequests;
  private List<ShiftCover> shiftCovers;

  public Instance(int horizon, Map<String, Shift> shifts, Map<String, Employee> employees, Map<String, DaysOff> employeeDaysOff,
                  List<ShiftRequest> shiftOnRequests, List<ShiftRequest> shiftOffRequests, List<ShiftCover> shiftCovers) {

    this.horizon = horizon;
    this.shifts = shifts;
    this.employees = employees;
    this.employeeDaysOff = employeeDaysOff;
    this.shiftOnRequests = shiftOnRequests;
    this.shiftOffRequests = shiftOffRequests;
    this.shiftCovers = shiftCovers;
  }

  public int getHorizon() {
    return horizon;
  }

  public Map<String, Shift> getShifts() {
    return shifts;
  }

  public List<String> getShiftIds() {
    return new ArrayList<>(shifts.keySet());
  }

  public Employee getEmployee(String employeeId) {
    return employees.get(employeeId);
  }

  public Map<String, Employee> getEmployees() {
    return employees;
  }

  public List<String> getEmployeeIds() {
    return new ArrayList<>(employees.keySet());
  }

  public List<Integer> getEmployeeDaysOff(String employeeId) {
    return employeeDaysOff.get(employeeId).getDaysOff();
  }

  public Map<String, DaysOff> getEmployeeDaysOff() {
    return employeeDaysOff;
  }

  public List<ShiftRequest> getShiftOnRequests() {
    return shiftOnRequests;
  }

  public List<ShiftRequest> getShiftOffRequests() {
    return shiftOffRequests;
  }

  public List<ShiftCover> getShiftCovers() {
    return shiftCovers;
  }

}
