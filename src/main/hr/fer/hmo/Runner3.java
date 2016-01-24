package hr.fer.hmo;

import hr.fer.hmo.data.Instance;
import hr.fer.hmo.parser.InstanceParser;
import hr.fer.hmo.problem.Solution;
import hr.fer.hmo.problem.Validator;
import hr.fer.hmo.problem.builder.SmartSolutionGenerator;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Runner3 {

  private static SmartSolutionGenerator smartSolutionGenerator = new SmartSolutionGenerator();

  public static void main(String[] args) throws FileNotFoundException {
    Instance instance = InstanceParser.parse("instance.txt");

    Validator validator = new Validator();

    Solution solution;
    Solution bestSolution = null;
    int cost = Integer.MAX_VALUE;
    for (int i = 0; i < 100; i++) {
      solution = generateSolutionDaysOff(instance, validator);

//      if (validator.validateHardConstraints(instance, solution) > 0) {
//        System.out.println("INVALID SOLUTION!");
//        continue;
//      }

      int newCost = validator.validateSoftConstraints(instance, solution);
      if (newCost < cost) {
        System.out.println("New cost: " + newCost);
        cost = newCost;
        bestSolution = solution;
      }
    }

    System.out.println();
    System.out.println("Best solution:");
    System.out.println(bestSolution);
    System.out.println(validator.validateHardConstraints(instance, bestSolution));
    System.out.println(validator.validateSoftConstraints(instance, bestSolution));
  }

  public static Solution generateSolutionDaysOff(Instance instance, Validator validator) {
    List<String> employeeIds = instance.getEmployeeIds();

    Solution solution = new Solution(employeeIds, instance.getHorizon());

    Set<String> employeeIdsSet = new HashSet<>(employeeIds);
    Set<String> removedEmployeeIds = new HashSet<>();

    while (!employeeIdsSet.isEmpty()) {
      removedEmployeeIds.clear();

      for (String employeeId : employeeIdsSet) {

        for (int i = 0; i < 10000000; i++) {
          smartSolutionGenerator.generate(instance, solution, employeeId);
          int brokenConstraintsCount = validator.validateHardConstraints(instance, solution, employeeId);
          if (brokenConstraintsCount == 0) {
            removedEmployeeIds.add(employeeId);
            break;
          }
        }
      }

      employeeIdsSet.removeAll(removedEmployeeIds);
    }

    return solution;
  }

}
