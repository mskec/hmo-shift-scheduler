package hr.fer.hmo.problem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dstankovic on 1/18/16.
 */
public class Solution {
  private int fitness;
  private Map<String, String[]> solution; // [employee][day]
  private List<String> employeeIds;
  private int numberOfDays;

  public Solution(List<String> employeeIds, int numberOfDays) {
    this.employeeIds = employeeIds;
    this.numberOfDays = numberOfDays;
    solution = new HashMap<>(employeeIds.size());
    for (String employeeId : employeeIds) {
      solution.put(employeeId, new String[numberOfDays]);
    }
    fitness = Integer.MAX_VALUE;
  }

  public int getFitness() {
    return fitness;
  }

  public void setFitness(int fitness) {
    this.fitness = fitness;
  }

  public String getShift(String employeeId, int day) {
    return solution.get(employeeId)[day];
  }

  public int getNumberOfDays() {
    return numberOfDays;
  }

  public List<String> getEmployeeIds() {
    return employeeIds;
  }
}
