package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.parser.InstanceParser;
import hr.fer.hmo.problem.EmployeeShifts;
import hr.fer.hmo.problem.ShiftDistributor;
import hr.fer.hmo.problem.Solution;
import hr.fer.hmo.problem.Validator;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SmartShiftGenerator implements SolutionGenerator {

  private ShiftDistributor shiftDistributor = new ShiftDistributor();

  @Override
  public void generate(Instance instance, Solution solution, String employeeId) {
    int horizon = solution.getHorizon();

    Employee employee = instance.getEmployee(employeeId);
    EmployeeShifts employeeShifts = new EmployeeShifts(instance.getShifts(), employee.getMaxShifts());

    String previousShiftId = "";
    for (int i = 0; i < horizon; i++) {

      // Skip days off
      if (solution.getShift(employeeId, i) == null) {
        continue;
      }

      String shiftId = shiftDistributor.distributeShift(employeeShifts.getValidShifts(previousShiftId));
      if (!shiftId.isEmpty()) {
        employeeShifts.usedShift(shiftId);       // decrease chosen shift
        solution.setShift(employeeId, i, shiftId);
      }

      previousShiftId = shiftId;
    }
  }

}
