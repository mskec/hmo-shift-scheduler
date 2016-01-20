package hr.fer.hmo.problem;

import hr.fer.hmo.data.Instance;

import java.util.List;
import java.util.Random;

public class SolutionRandom implements SolutionGenerator {

  private Random random = new Random();

  @Override
  public Solution generate(Instance instance) {
    Solution solution = new Solution(instance.getEmployeeIds(), instance.getHorizon());

    solution.getEmployeeIds()
        .forEach(employeeId -> generateEmployeeShifts(solution, instance, employeeId));

    return solution;
  }

  private void generateEmployeeShifts(Solution solution, Instance instance, String employeeId) {
    int horizon = solution.getHorizon();
    List<String> shiftIds = instance.getShiftIds();

    for (int i = 0; i < horizon; i++) {
      String shiftId = shiftIds.get(random.nextInt(shiftIds.size()));
      solution.setShift(employeeId, i, shiftId);
    }
  }

}
