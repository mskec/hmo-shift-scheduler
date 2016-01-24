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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ParallelRunner {

  private static final int ITERATIONS = 10;
  private static final int PARALLEL_COEF = 2;

  public static void main(String[] args) throws FileNotFoundException {
    int cpuCors = Runtime.getRuntime().availableProcessors();
    System.out.println("CPUs number: " + cpuCors);
    ExecutorService executor = Executors.newFixedThreadPool(cpuCors);
    int solsSize = cpuCors * PARALLEL_COEF;
    BlockingQueue<Solution> solutionQueue = new LinkedBlockingQueue<>(solsSize);

    for (int i = 0; i < solsSize; i++) {
      Runnable worker = () -> {
        try {
          runSmartGenerator(false, solutionQueue);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      };
      executor.execute(worker);
    }

    executor.shutdown();
    while (!executor.isTerminated()) {}

    System.out.println("Everything is done");
    Solution best = null;
    for (Solution sol : solutionQueue) {
      if (best == null || sol.getFitness() < best.getFitness()) {
        best = sol;
      }
    }

    System.out.println("Best solution:");
    System.out.println(best.toString());
    System.out.println("\n\n\n");
    System.out.println(best.getFitness());
  }

  private static void runSmartGenerator(boolean print, BlockingQueue<Solution> solsQ) throws FileNotFoundException {
    SmartSolutionGenerator smartSolutionGenerator = new SmartSolutionGenerator();
    Instance instance = InstanceParser.parse("instance.txt");
    List<String> employeeIds = instance.getEmployeeIds();
    Validator validator = new Validator();
    Solution best = null;
    for (int iter = 0; iter < ITERATIONS; iter++) {
      if (print) System.out.println("Iteration: " + iter);
      Solution solution = new Solution(employeeIds, instance.getHorizon());
      Set<String> employeeIdsSet = new HashSet<>(employeeIds);
      Set<String> removedEmployeeIds = new HashSet<>();
      while (!employeeIdsSet.isEmpty()) {
//        System.out.println(employeeIdsSet);
        removedEmployeeIds.clear();
        for (String employeeId : employeeIdsSet) {
//          System.out.println(employeeId);
          for (int i = 0; i < 1000000; i++) {
            smartSolutionGenerator.generate(instance, solution, employeeId);
            int brokenConstraintsCount = validator.validateHardConstraints(instance, solution, employeeId);
            if (brokenConstraintsCount <= 0) {
//              System.out.println(brokenConstraintsCount);
              removedEmployeeIds.add(employeeId);
              break;
            }
          }
        }
        employeeIdsSet.removeAll(removedEmployeeIds);
//        System.out.println(removedEmployeeIds);
      }
//      validator.setPrint(true);
      int brokenConstraintsCount = validator.validateHardConstraints(instance, solution);
      if (print) System.out.println(brokenConstraintsCount);
      int fitness = validator.validateSoftConstraints(instance, solution);
      if (print) System.out.println(fitness);
      solution.setFitness(fitness);
      if (best == null || best.getFitness() > fitness) {
        best = solution;
      }
      if (print) System.out.println("Fitness: " + fitness);
      if (print) System.out.println();
    }
    if (print) System.out.println("Final solution:");
    if (print) System.out.println(best);
    if (print) System.out.println("\n\n\n");
    if (print) System.out.println(best.getFitness());
    solsQ.add(best);
  }

}
