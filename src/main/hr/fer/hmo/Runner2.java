package hr.fer.hmo;

import hr.fer.hmo.data.Instance;
import hr.fer.hmo.parser.InstanceParser;
import hr.fer.hmo.problem.Solution;
import hr.fer.hmo.problem.Validator;
import hr.fer.hmo.problem.builder.SmartShiftGenerator;
import hr.fer.hmo.problem.builder.SmartSolutionGenerator;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Runner2 {

  private static SmartSolutionGenerator smartSolutionGenerator = new SmartSolutionGenerator();
  private static SmartShiftGenerator smartShiftGenerator = new SmartShiftGenerator();

  public static void main(String[] args) throws FileNotFoundException {
    Instance instance = InstanceParser.parse("instance.txt");

    Validator validator = new Validator();

    Solution solution;
    Solution bestSolution = null;
    int cost = Integer.MAX_VALUE;
    for (int i = 0; i < 1000; i++) {
      solution = generateSolutionDaysOff(instance, validator);

      rearangeShifts(instance, solution, validator);

      if (validator.validateHardConstraints(instance, solution) > 0) {
        System.out.println("INVALID SOLUTION!");
        break;
      }

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
//        System.out.print(employeeId + " ");
      }

      employeeIdsSet.removeAll(removedEmployeeIds);
    }

    return solution;
  }

  private static void rearangeShifts(Instance instance, Solution solution, Validator validator) {
    List<String> employeeIds = instance.getEmployeeIds();

    for (String employeeId : employeeIds) {
      smartShiftGenerator.generate(instance, solution, employeeId);
    }
  }

}
