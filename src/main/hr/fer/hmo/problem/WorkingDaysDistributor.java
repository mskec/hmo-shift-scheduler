package hr.fer.hmo.problem;

import hr.fer.hmo.data.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkingDaysDistributor {
  private Random rand = new Random();

  public List<Integer> distribute(Employee employee, int horizon) {
    int minConsecutiveShifts = employee.getMinConsecutiveShifts();
    int maxConsecutiveShifts = employee.getMaxConsecutiveShifts();
    int minConsecutiveDaysOff = employee.getMinConsecutiveDaysOff();
    int remainingDays = horizon;

    List<Integer> list = new ArrayList<>();
    while (remainingDays > 0) {
      if (remainingDays < minConsecutiveShifts) {
        break;
      }

      int max = Integer.min(maxConsecutiveShifts, remainingDays);
      int num;
      if (employee.getMaxTotalMinutes() > 50000) {
        if (rand.nextDouble() > 0.1) {
          num = max;
        } else {
          num = rand.nextInt(max - minConsecutiveShifts + 1) + minConsecutiveShifts;
        }
      } else {
        num = rand.nextInt(max - minConsecutiveShifts + 1) + minConsecutiveShifts;
      }

      list.add(num);
      list.add(minConsecutiveDaysOff);
      remainingDays -= num + minConsecutiveDaysOff;
    }
    list.remove(list.size()-1);
    return list;
  }
}
