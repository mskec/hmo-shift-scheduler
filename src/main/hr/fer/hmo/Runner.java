package hr.fer.hmo;

import hr.fer.hmo.data.Instance;
import hr.fer.hmo.parser.InstanceParser;
import hr.fer.hmo.problem.Solution;
import hr.fer.hmo.problem.builder.SmartSolutionGenerator1;
import hr.fer.hmo.problem.Validator;
import hr.fer.hmo.problem.builder.SolutionBuilder;

import java.io.FileNotFoundException;

public class Runner {
  public static void main(String[] args) throws FileNotFoundException, InterruptedException {
    System.out.println("Parsing instance file");
    Instance instance = InstanceParser.parse("instance.txt");

    System.out.println("Generating initial solution");

    Validator validator = new Validator();
    SmartSolutionGenerator1 smartSolutionGenerator = new SmartSolutionGenerator1();

    String employeeId = "A";
//    Solution solution = new Solution(Arrays.asList(employeeId), instance.getHorizon());

    for (int i = 0; i < 100000; i++) {
      Solution solution = new SolutionBuilder(instance, smartSolutionGenerator).build();

      smartSolutionGenerator.generate(instance, solution, employeeId);

      if (!validator.validateShiftRotation(instance, solution, employeeId)) {
        System.out.println("VIOLATED|SHIFT_ROTATION");
      }

      if (!validator.validateMaxShifts(instance, solution, employeeId)) {
        System.out.println("VIOLATED|MAX_SHIFTS");
      }

      if (!validator.validateConsecutiveDaysOff(instance, solution, employeeId)) {
        System.out.println("VIOLATED|DAYS_OFF");
      }

      if ((i+1) % 1000 == 0) {
        System.out.printf("Case: %d\n", i + 1);
      }
    }
  }
}
