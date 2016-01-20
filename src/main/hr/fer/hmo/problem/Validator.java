package hr.fer.hmo.problem;

import hr.fer.hmo.data.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator {
  /**
   *
   * @param instance problem instance
   * @param solution problem solution
   * @return Number of broken hard constraints
   */
  public int validateHardConstraints(Instance instance, Solution solution) {
    int numberOfBrokenConstraints = 0;
    if (!validateShiftRotation(instance, solution)) {
      numberOfBrokenConstraints++;
      System.out.println("Validator|SHIFT_ROTATION");
    }
    if (!validateMaxShifts(instance, solution)) {
      numberOfBrokenConstraints++;
      System.out.println("Validator|MAX_SHIFTS");
    }
    if (!validateTotalMinutes(instance, solution)) {
      numberOfBrokenConstraints++;
      System.out.println("Validator|TOTAL_MINUTES");
    }
    if (!validateConsecutiveShifts(instance, solution)) {
      numberOfBrokenConstraints++;
      System.out.println("Validator|CONSECUTIVE_SHIFTS");
    }
    if (!validateConsecutiveDaysOff(instance, solution)) {
      numberOfBrokenConstraints++;
      System.out.println("Validator|CONSECUTIVE_DAYS_OFF");
    }
    if (!validateMaxWeekends(instance, solution)) {
      numberOfBrokenConstraints++;
      System.out.println("Validator|MAX_WEEKENDS");
    }
    if (!validateDaysOff(instance, solution)) {
      numberOfBrokenConstraints++;
      System.out.println("Validator|DAYS_OFF");
    }
    return numberOfBrokenConstraints;
  }


  /**
   *
   * @param instance problem instance
   * @param solution problem solution
   * @return Total weight of all broken soft constraints
   */
  public int validateSoftConstraints(Instance instance, Solution solution) {
    int totalCount = 0;
    totalCount += validateShiftOnRequests(instance, solution);
    totalCount += validateShiftOffRequests(instance, solution);
    totalCount += validateShiftCovers(instance, solution);
    return totalCount;
  }


  // SOFT CONSTRAINTS

  private int validateShiftCovers(Instance instance, Solution solution) {
    List<ShiftCover> shiftCovers = instance.getShiftCovers();
    int totalWeight = 0;
    for (ShiftCover shiftCover : shiftCovers) {
      int day = shiftCover.getDay();
      String shift = shiftCover.getShiftId();
      int requirement = shiftCover.getRequirement();
      int count = 0;
      for (String employeeId : solution.getEmployeeIds()) {
        // for each employee
        if (shift.equals(solution.getShift(employeeId, day))) {
          count++;
        }
      }
      if (count < requirement) {
        // not enough employees in a shift
        totalWeight += (requirement - count) * shiftCover.getWeightUnder();
      } else if (count > requirement) {
        // too many employees in a shift
        totalWeight += (count - requirement) * shiftCover.getWeightOver();
      }
    }
    return totalWeight;
  }


  private int validateShiftOffRequests(Instance instance, Solution solution) {
    List<ShiftRequest> shiftRequests = instance.getShiftOffRequests();
    int totalWeight = 0;
    for (ShiftRequest shiftRequest : shiftRequests) {
      // for each shift request
      String employeeId = shiftRequest.getEmployeeId();
      int day = shiftRequest.getDay();
      String requestedShift = shiftRequest.getShiftId();
      String shift = solution.getShift(employeeId, day);
      if (requestedShift.equals(shift)) {
        totalWeight += shiftRequest.getWeight();
      }
    }
    return totalWeight;
  }


  private int validateShiftOnRequests(Instance instance, Solution solution) {
    List<ShiftRequest> shiftRequests = instance.getShiftOnRequests();
    int totalWeight = 0;
    for (ShiftRequest shiftRequest : shiftRequests) {
      // for each shift request
      String employeeId = shiftRequest.getEmployeeId();
      int day = shiftRequest.getDay();
      String requestedShift = shiftRequest.getShiftId();
      String shift = solution.getShift(employeeId, day);
      if (!requestedShift.equals(shift)) {
        totalWeight += shiftRequest.getWeight();
      }
    }
    return totalWeight;
  }


  // HARD CONSTRAINTS

  private boolean validateDaysOff(Instance instance, Solution solution) {
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      if (!validateDaysOff(instance, solution, employeeId)) {
        return false;
      }
    }
    return true;
  }


  public boolean validateDaysOff(Instance instance, Solution solution, String employeeId) {
    Map<String, DaysOff> employeeDaysOff = instance.getEmployeeDaysOff();
    List<Integer> daysOff = employeeDaysOff.get(employeeId).getDaysOff();
    for (Integer dayOff : daysOff) {
      String shift = solution.getShift(employeeId, dayOff);
      if (shift != null) {
        // employee is working on a day off
        return false;
      }
    }
    return true;
  }


  private boolean validateMaxWeekends(Instance instance, Solution solution) {
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      if (!validateMaxWeekends(instance, solution, employeeId)) {
        return false;
      }
    }
    return true;
  }


  public boolean validateMaxWeekends(Instance instance, Solution solution, String employeeId) {
    int days = solution.getHorizon();
    Map<String, Employee> employees = instance.getEmployees();
    Employee employee = employees.get(employeeId);
    int maxWeekends = employee.getMaxWeekends();
    int workingWeekends = 0;
    for (int i = 5; i < days; i+=7) {
      // for each saturday
      String saturdayShift = solution.getShift(employeeId, i);
      String sundayShift = solution.getShift(employeeId, i+1);
      if (saturdayShift != null || sundayShift != null) {
        // working weekend
        workingWeekends++;
      }
    }
    if (workingWeekends > maxWeekends) {
      return false;
    }
    return true;
  }


  private boolean validateConsecutiveDaysOff(Instance instance, Solution solution) {
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      if (!validateConsecutiveDaysOff(instance, solution, employeeId)) {
        return false;
      }
    }
    return true;
  }


  public boolean validateConsecutiveDaysOff(Instance instance, Solution solution, String employeeId) {
    int days = solution.getHorizon();
    Map<String, Employee> employees = instance.getEmployees();
    Employee employee = employees.get(employeeId);
    int minConsecutiveDaysOff = employee.getMinConsecutiveDaysOff();
    int streak = 0;
    int start = 0;
    while (solution.getShift(employeeId, start) == null) {
      start++;
    }
    for (int i = start; i < days; i++) {
      // for each day
      String shift = solution.getShift(employeeId, i);
      if (shift != null) {
        // employee works on this day
        if (streak > 0) {
          // end of streak
          if (streak < minConsecutiveDaysOff) {
            return false;
          }
          streak = 0;
        }
      } else {
        // employee does not work on this day
        streak++;
      }
    }
    return true;
  }


  private boolean validateConsecutiveShifts(Instance instance, Solution solution) {
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      if (!validateConsecutiveShifts(instance, solution, employeeId)) {
        return false;
      }
    }
    return true;
  }


  public boolean validateConsecutiveShifts(Instance instance, Solution solution, String employeeId) {
    int days = solution.getHorizon();
    Map<String, Employee> employees = instance.getEmployees();
    Employee employee = employees.get(employeeId);
    int minConsecutiveShifts = employee.getMinConsecutiveShifts();
    int maxConsecutiveShifts = employee.getMaxConsecutiveShifts();
    int streak = 0;
    for (int i = 0; i < days; i++) {
      // for each day
      String shift = solution.getShift(employeeId, i);
      if (shift == null) {
        // employee does not work on this day
        if (streak > 0) {
          // end of streak
          if (streak < minConsecutiveShifts || streak > maxConsecutiveShifts) {
            return false;
          }
          streak = 0;
        }
      } else {
        // employee works on this day
        streak++;
      }
    }
    if (streak < minConsecutiveShifts || streak > maxConsecutiveShifts) {
      return false;
    }
    return true;
  }


  private boolean validateTotalMinutes(Instance instance, Solution solution) {
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      if (!validateTotalMinutes(instance, solution, employeeId)) {
        return false;
      }
    }
    return true;
  }


  public boolean validateTotalMinutes(Instance instance, Solution solution, String employeeId) {
    int days = solution.getHorizon();
    Map<String, Employee> employees = instance.getEmployees();
    Map<String, Shift> shifts = instance.getShifts();
    int minutesCount = 0;
    for (int i = 0; i < days; i++) {
      // for each day
      String shiftId = solution.getShift(employeeId, i);

      // Do not count if day off
      if (shiftId == null) {
        continue;
      }

      int length = shifts.get(shiftId).getLength();
      minutesCount += length;
    }
    Employee employee = employees.get(employeeId);
    if (minutesCount < employee.getMinTotalMinutes() || minutesCount > employee.getMaxTotalMinutes()) {
      return false;
    }
    return true;
  }


  private boolean validateMaxShifts(Instance instance, Solution solution) {
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      if (!validateMaxShifts(instance, solution, employeeId)) {
        return false;
      }
    }
    return true;
  }


  public boolean validateMaxShifts(Instance instance, Solution solution, String employeeId) {
    int days = solution.getHorizon();
    Map<String, Employee> employees = instance.getEmployees();
    Map<String, Integer> shiftsCount = new HashMap<>(); // shift -> shiftsCount
    for (int i = 0; i < days; i++) {
      // for each day
      String shiftId = solution.getShift(employeeId, i);

      // Do not count days off
      if (shiftId == null) {
        continue;
      }

      if (!shiftsCount.containsKey(shiftId)) {
        shiftsCount.put(shiftId, 0);
      }
      shiftsCount.put(shiftId, shiftsCount.get(shiftId) + 1);
    }
    Employee employee = employees.get(employeeId);
    Map<String, Integer> maxShifts = employee.getMaxShifts();
    for (String shift : shiftsCount.keySet()) {
      if (shiftsCount.get(shift) > maxShifts.get(shift)) {
        return false;
      }
    }
    return true;
  }


  private boolean validateShiftRotation(Instance instance, Solution solution) {
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      if (!validateShiftRotation(instance, solution, employeeId)) {
        return false;
      }
    }
    return true;
  }

  public boolean validateShiftRotation(Instance instance, Solution solution, String employeeId) {
    Map<String, Shift> shifts = instance.getShifts();
    int days = solution.getHorizon();
    for (int i = 0; i < days - 1; i++) {
      // for each day except last one
      String currShift = solution.getShift(employeeId, i);
      String nextShift = solution.getShift(employeeId, i+1);
      if (shifts.get(currShift).getNotFollowingShifts().contains(nextShift)) {
        return false;
      }
    }
    return true;
  }
}
