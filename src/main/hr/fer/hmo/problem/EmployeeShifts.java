package hr.fer.hmo.problem;

import hr.fer.hmo.data.Shift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeShifts {

  private Map<String, EmployeeShiftData> employeeShifts = new HashMap<>();    // <shiftId, ...>
  private Map<String, List<EmployeeShiftData>> validEmployeeShifts;           // <previousShiftId, ...>

  public EmployeeShifts(Map<String, Shift> shifts, Map<String, Integer> maxShifts) {
    for (String shiftId : maxShifts.keySet()) {
      this.employeeShifts.put(shiftId, new EmployeeShiftData(shiftId, maxShifts.get(shiftId)));
    }

    generateValidEmployeeShifts(shifts, employeeShifts);
  }

  public void usedShift(String shiftId) {
    this.employeeShifts.get(shiftId).remainingShifts--;
  }

  public List<EmployeeShiftData> getValidShifts(String previousShiftId) {
    return this.validEmployeeShifts.get(previousShiftId);
  }

  private void generateValidEmployeeShifts(Map<String, Shift> shifts, Map<String, EmployeeShiftData> employeeShifts) {
    validEmployeeShifts = new HashMap<>();

    for (String employeeShiftId : employeeShifts.keySet()) {
      validEmployeeShifts.put(employeeShiftId, new ArrayList<>());
      Shift shift = shifts.get(employeeShiftId);

      shifts.keySet().stream()
          .filter(shiftId -> !shift.getNotFollowingShifts().contains(shiftId))
          .forEach(shiftId -> validEmployeeShifts.get(employeeShiftId).add(employeeShifts.get(shiftId)));
    }

    // Initialize for when previous day employee did not work
    validEmployeeShifts.put("", new ArrayList<>());
    for (String shiftId : employeeShifts.keySet()) {
      validEmployeeShifts.get("").add(employeeShifts.get(shiftId));
    }
  }

  public static class EmployeeShiftData {
    public String shiftId;
    public int remainingShifts;

    public EmployeeShiftData(String shiftId, int remainingShifts) {
      this.shiftId = shiftId;
      this.remainingShifts = remainingShifts;
    }
  }

}
