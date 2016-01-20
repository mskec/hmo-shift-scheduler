package hr.fer.hmo;

import hr.fer.hmo.data.Instance;
import hr.fer.hmo.problem.Solution;
import hr.fer.hmo.problem.builder.SolutionBuilder;
import hr.fer.hmo.problem.builder.SmartSolutionGenerator1;
import hr.fer.hmo.problem.Validator;

import java.io.FileNotFoundException;

public class Runner {
  public static void main(String[] args) throws FileNotFoundException, InterruptedException {
    System.out.println("Parsing instance file");
    Instance instance = InstanceParser.parse("instance.txt");

    System.out.println("Generating initial solution");

    SolutionBuilder builder = new SolutionBuilder(instance, new SmartSolutionGenerator1());
    for (int i = 0; i < 100000; i++) {
//      System.out.println("Case " + (i+1));
      Solution solution = builder.build();

//      System.out.println("| Validating generated solution");
      Validator validator = new Validator();
      int brokenConstraintsCount = validator.validateHardConstraints(instance, solution);
//      System.out.println("| Validating done " + fitness);

//      if ((i+1) % 1000 == 0) {
//        System.out.println("Case " + (i+1));
//      }

      if (brokenConstraintsCount < 4) {
        System.out.printf("Case %d: fitness %d\n", (i + 1), brokenConstraintsCount);
      }
    }
  }
}
