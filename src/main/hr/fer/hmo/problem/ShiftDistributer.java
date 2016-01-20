package hr.fer.hmo.problem;

import java.util.Map;
import java.util.Random;

public class ShiftDistributer {
  private Random rand = new Random();

  public String distributeShift(Map<String, Integer> shiftsCounter) {
    int totalCount = 0;
    for (String shiftId : shiftsCounter.keySet()) {
      totalCount += shiftsCounter.get(shiftId);
    }
    int chosenNumber = rand.nextInt(totalCount) + 1;
    for (String shiftId : shiftsCounter.keySet()) {
      chosenNumber -= shiftsCounter.get(shiftId);
      if (chosenNumber <= 0) {
        return shiftId;
      }
    }
    return null;
  }
}
