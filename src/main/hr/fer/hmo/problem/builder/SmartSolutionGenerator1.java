package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.problem.EmployeeShifts;
import hr.fer.hmo.problem.ShiftDistributor;
import hr.fer.hmo.problem.Solution;

import java.util.HashSet;
import java.util.Set;

/**
 * This generator is taking in to account hard constraints:
 * DAYS_OFF and MAX_SHIFTS
 */
public class SmartSolutionGenerator1 implements SolutionGenerator {

  private ShiftDistributor shiftDistributor = new ShiftDistributor();

  @Override
  public void generate(Instance instance, Solution solution, String employeeId) {
    int horizon = solution.getHorizon();

    Employee employee = instance.getEmployee(employeeId);

    Set<Integer> employeesDaysOff = new HashSet<>(instance.getEmployeeDaysOff(employeeId));

    EmployeeShifts employeeShifts = new EmployeeShifts(instance.getShifts(), employee.getMaxShifts());

    String previousShiftId = "";
    for (int i = 0; i < horizon; i++) {
      // Hard constraint DAYS_OFF
      if (employeesDaysOff.contains(i)) {
        previousShiftId = "";
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
