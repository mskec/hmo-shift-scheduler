package hr.fer.hmo.problem;

import hr.fer.hmo.problem.builder.SmartSolutionGenerator1.EmployeeShift;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Solving hard constraint MAX_SHIFTS
 */
public class ShiftDistributor {
  private Random random = new Random();


  /**
   * Roulette wheel distribution
   */
  public String distributeShift(Map<String, Integer> shiftsCounter) {
    int totalShiftCount = 0;
    for (String shiftId : shiftsCounter.keySet()) {
      totalShiftCount += shiftsCounter.get(shiftId);
    }

    int chosenNumber = random.nextInt(totalShiftCount) + 1;
    for (String shiftId : shiftsCounter.keySet()) {
      chosenNumber -= shiftsCounter.get(shiftId);
      if (chosenNumber <= 0) {
        return shiftId;
      }
    }
    return null;
  }

  public String distributeShift(List<EmployeeShift> employeeShifts) {
    int totalShiftCount = 0;
    for (EmployeeShift employeeShift : employeeShifts) {
      totalShiftCount += employeeShift.remainingShifts;
    }

    int chosenNumber = random.nextInt(totalShiftCount) + 1;
    for (EmployeeShift employeeShift : employeeShifts) {
      chosenNumber -= employeeShift.remainingShifts;
      if (chosenNumber <= 0) {
        return employeeShift.shiftId;
      }
    }
    return null;
  }
}
