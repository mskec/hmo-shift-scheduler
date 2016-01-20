package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Instance;
import hr.fer.hmo.problem.Solution;

public class SolutionBuilder {

  private Instance instance;
  private SolutionGenerator solutionGenerator;

  public SolutionBuilder(Instance instance, SolutionGenerator solutionGenerator) {
    this.instance = instance;
    this.solutionGenerator = solutionGenerator;
  }

  public Solution build() {
    Solution solution = new Solution(instance.getEmployeeIds(), instance.getHorizon());

    solution.getEmployeeIds()
        .forEach(employeeId -> solutionGenerator.generate(instance, solution, employeeId));

    return solution;
  }

}
