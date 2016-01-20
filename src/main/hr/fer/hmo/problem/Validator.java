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
    }
    if (!validateMaxShifts(instance, solution)) {
      numberOfBrokenConstraints++;
    }
    if (!validateTotalMinutes(instance, solution)) {
      numberOfBrokenConstraints++;
    }
    if (!validateConsecutiveShifts(instance, solution)) {
      numberOfBrokenConstraints++;
    }
    if (!validateConsecutiveDaysOff(instance, solution)) {
      numberOfBrokenConstraints++;
    }
    if (!validateMaxWeekends(instance, solution)) {
      numberOfBrokenConstraints++;
    }
    if (!validateDaysOff(instance, solution)) {
      numberOfBrokenConstraints++;
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


  private boolean validateDaysOff(Instance instance, Solution solution) {
    Map<String, DaysOff> employeeDaysOff = instance.getEmployeeDaysOff();
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
      List<Integer> daysOff = employeeDaysOff.get(employeeId).getDaysOff();
      for (Integer dayOff : daysOff) {
        String shift = solution.getShift(employeeId, dayOff);
        if (shift != null) {
          // employee is working on a day off
          return false;
        }
      }
    }
    return true;
  }


  private boolean validateMaxWeekends(Instance instance, Solution solution) {
    int days = solution.getHorizon();
    Map<String, Employee> employees = instance.getEmployees();
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
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
    }
    return true;
  }


  private boolean validateConsecutiveDaysOff(Instance instance, Solution solution) {
    int days = solution.getHorizon();
    Map<String, Employee> employees = instance.getEmployees();
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
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
    }
    return true;
  }


  private boolean validateConsecutiveShifts(Instance instance, Solution solution) {
    int days = solution.getHorizon();
    Map<String, Employee> employees = instance.getEmployees();
    for (String employeeId : solution.getEmployeeIds()) {
      // for each employee
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
    }
    return true;
  }


  private boolean validateTotalMinutes(Instance instance, Solution solution) {
    int days = solution.getHorizon();
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
    int days = solution.getHorizon();
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
    int days = solution.getHorizon();
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
