package hr.fer.hmo.problem;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.data.Shift;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dstankovic on 1/18/16.
 */
public class Validator {
  public int validate(Instance instance, Solution solution) {
    // hard constraints
    if (!validateShiftRotation(instance, solution)) {
      return Integer.MAX_VALUE;
    }
    if (!validateMaxShifts(instance, solution)) {
      return Integer.MAX_VALUE;
    }
    if (!validateTotalMinutes(instance, solution)) {
      return Integer.MAX_VALUE;
    }

    // soft constraints
    // TODO
    return 0; // TODO
  }

  private boolean validateTotalMinutes(Instance instance, Solution solution) {
    int days = solution.getNumberOfDays();
    Map<String, Employee> employees = instance.getEmployees();
    Map<String, Shift> shifts = instance.getShifts();
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      int minutesCount = 0;
      for (int i = 0; i < days; i++) {
        // for each day
        String shift = solution.getShift(employeeId, i);
        int length = shifts.get(shift).getLength();
        minutesCount += length;
      }
      Employee employee = employees.get(employeeId);
      if (minutesCount < employee.getMinTotalMinutes() || minutesCount > employee.getMaxTotalMinutes()) {
        return false;
      }
    }
    return true;
  }

  private boolean validateMaxShifts(Instance instance, Solution solution) {
    int days = solution.getNumberOfDays();
    Map<String, Employee> employees = instance.getEmployees();
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      Map<String, Integer> shiftsCount = new HashMap<>(); // shift -> shiftsCount
      for (int i = 0; i < days; i++) {
        // for each day
        String shift = solution.getShift(employeeId, i);
        if (!shiftsCount.containsKey(shift)) {
          shiftsCount.put(shift, 0);
        }
        shiftsCount.put(shift, shiftsCount.get(shift) + 1);
      }
      Employee employee = employees.get(employeeId);
      Map<String, Integer> maxShifts = employee.getMaxShifts();
      for (String shift : shiftsCount.keySet()) {
        if (shiftsCount.get(shift) > maxShifts.get(shift)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean validateShiftRotation(Instance instance, Solution solution) {
    Map<String, Shift> shifts = instance.getShifts();
    int days = solution.getNumberOfDays();
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      for (int i = 0; i < days - 1; i++) {
        // for each day except last one
        String currShift = solution.getShift(employeeId, i);
        String nextShift = solution.getShift(employeeId, i+1);
        if (shifts.get(currShift).getNotFollowingShifts().contains(nextShift)) {
          return false;
        }
      }
    }
    return true;
  }
}
