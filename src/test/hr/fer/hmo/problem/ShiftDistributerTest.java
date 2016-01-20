package hr.fer.hmo.problem;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dstankovic on 1/20/16.
 */
public class ShiftDistributerTest {

  @Test
  public void testDistributeShift() throws Exception {
    ShiftDistributer shiftDistributer = new ShiftDistributer();
    Map<String, Integer> originalMap = new HashMap<>();
    Map<String, Integer> map = new HashMap<>();
    map.put("a1", 1);
    map.put("a2", 1);
    map.put("d1", 182);
    map.put("d2", 182);
    map.put("n1", 182);
    map.put("n2", 182);
    for (String shiftId : map.keySet()) {
      originalMap.put(shiftId, map.get(shiftId));
    }
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
      Assert.assertTrue(checkMap.get(shiftId).equals(originalMap.get(shiftId)));
    }
  }
}