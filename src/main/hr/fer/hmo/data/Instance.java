package hr.fer.hmo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Instance {

  private int horizon;
  private Map<String, Shift> shifts;              // <shiftId, Shift>
  private List<Employee> employeesList;
  private Map<String, Employee> employees;        // <employeeId, Employee>
  private Map<String, DaysOff> employeeDaysOff;   // <employeeId, DaysOff>
  private List<ShiftRequest> shiftOnRequests;
  private List<ShiftRequest> shiftOffRequests;
  private List<ShiftCover> shiftCovers;

  public Instance(int horizon, Map<String, Shift> shifts, List<Employee> employeesList, Map<String, DaysOff> employeeDaysOff,
                  List<ShiftRequest> shiftOnRequests, List<ShiftRequest> shiftOffRequests, List<ShiftCover> shiftCovers) {

    this.horizon = horizon;
    this.shifts = shifts;
    this.employeesList = employeesList;
    this.employeeDaysOff = employeeDaysOff;
    this.shiftOnRequests = shiftOnRequests;
    this.shiftOffRequests = shiftOffRequests;
    this.shiftCovers = shiftCovers;

    this.employees = new HashMap<>(employeesList.size());
    for (Employee employee : employeesList) {
      this.employees.put(employee.getEmployeeId(), employee);
    }
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

  public List<Employee> getEmployeesList() {
    return employeesList;
  }

  public Map<String, Employee> getEmployees() {
    return employees;
  }

  public List<String> getEmployeeIds() {
    return employeesList.stream().map(Employee::getEmployeeId).collect(Collectors.toList());
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
