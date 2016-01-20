package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Instance;
import hr.fer.hmo.problem.Solution;

public interface SolutionGenerator {

  void generate(Instance instance, Solution solution, String employeeId);

}
