package hr.fer.hmo.problem;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by dstankovic on 1/20/16.
 */
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

  public static void main(String[] args) {
    ShiftDistributer shiftDistributer = new ShiftDistributer();
    Map<String, Integer> map = new HashMap<>();
    map.put("a1", 1);
    map.put("a2", 1);
    map.put("d1", 182);
    map.put("d2", 182);
    map.put("n1", 182);
    map.put("n2", 182);
    Map<String, Integer> checkMap = new HashMap<>();
    for (int i = 4 * 182 + 2; i >= 1; i--) {
      String shift = shiftDistributer.distributeShift(map);
      map.put(shift, map.get(shift) - 1);
      if (!checkMap.containsKey(shift)) {
        checkMap.put(shift, 0);
      }
      checkMap.put(shift, checkMap.get(shift) + 1);
    }
    for (String shiftId : checkMap.keySet()) {
      System.out.println(shiftId + ": " + checkMap.get(shiftId));
    }
  }
}
