package hr.fer.hmo.problem;

import hr.fer.hmo.data.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by dstankovic on 1/20/16.
 */
public class WorkingDaysDistributer {
  private Random rand = new Random();

  public List<Integer> distribute(Employee employee, int horizon) {
    int minConsecutiveShifts = employee.getMinConsecutiveShifts();
    int maxConsecutiveShifts = employee.getMaxConsecutiveShifts();
    int minConsecutiveDaysOff = employee.getMinConsecutiveDaysOff();
    int remainingDays = horizon;
    List<Integer> list = new ArrayList<>();
    while (remainingDays > 0) {
      int min = minConsecutiveShifts;
      if (remainingDays < min) {
        break;
      }
      int max = Integer.min(maxConsecutiveShifts, remainingDays);
      int num = rand.nextInt(max - min + 1) + min;
      list.add(num);
      int daysOff = minConsecutiveDaysOff; // add random number to this variable
      list.add(daysOff);
      remainingDays -= num + daysOff;
    }
    list.remove(list.size()-1);
    return list;
  }
}
