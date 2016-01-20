package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Instance;
import hr.fer.hmo.problem.Solution;

import java.util.List;
import java.util.Random;

/**
 * Pure random generator.
 */
public class RandomSolutionGenerator implements SolutionGenerator {

  private Random random = new Random();

  @Override
  public void generate(Instance instance, Solution solution, String employeeId) {
    int horizon = solution.getHorizon();
    List<String> shiftIds = instance.getShiftIds();

    for (int i = 0; i < horizon; i++) {
      String shiftId = shiftIds.get(random.nextInt(shiftIds.size()));
      solution.setShift(employeeId, i, shiftId);
    }
  }
}
