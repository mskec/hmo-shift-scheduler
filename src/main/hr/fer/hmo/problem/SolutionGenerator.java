package hr.fer.hmo.problem;

import hr.fer.hmo.data.Instance;

public interface SolutionGenerator {

  void generate(Instance instance, Solution solution, String employeeId);

}
