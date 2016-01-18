package hr.fer.hmo.problem;

import hr.fer.hmo.data.Instance;
import hr.fer.hmo.data.Shift;

import java.util.Map;

/**
 * Created by dstankovic on 1/18/16.
 */
public class Validator {
  public int validate(Instance instance, Solution solution) {
    if (!validateShiftRotation(instance, solution)) {
      return Integer.MAX_VALUE; // hard constraint
    }

    return 0; // TODO
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
