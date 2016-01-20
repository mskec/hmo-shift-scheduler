package hr.fer.hmo.problem;

import java.util.Map;
import java.util.Random;

/**
 * Solving hard constraint MAX_SHIFTS
 */
public class ShiftDistributor {
  private Random rand = new Random();

  /**
   * Roulette wheel distribution
   */
  public String distributeShift(Map<String, Integer> shiftsCounter) {
    int totalShiftCount = 0;
    for (String shiftId : shiftsCounter.keySet()) {
      totalShiftCount += shiftsCounter.get(shiftId);
    }

    int chosenNumber = rand.nextInt(totalShiftCount) + 1;
    for (String shiftId : shiftsCounter.keySet()) {
      chosenNumber -= shiftsCounter.get(shiftId);
      if (chosenNumber <= 0) {
        return shiftId;
      }
    }
    return null;
  }
}
