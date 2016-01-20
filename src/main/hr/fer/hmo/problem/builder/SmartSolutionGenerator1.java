package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.problem.ShiftDistributer;
import hr.fer.hmo.problem.Solution;
import hr.fer.hmo.utils.Utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This generator is taking in to account hard constraints:
 * DAYS_OFF and MAX_SHIFTS
 */
public class SmartSolutionGenerator1 implements SolutionGenerator {

  private ShiftDistributer shiftDistributer = new ShiftDistributer();

  @Override
  public void generate(Instance instance, Solution solution, String employeeId) {
    int horizon = solution.getHorizon();

    Employee employee = instance.getEmployee(employeeId);

    Set<Integer> employeesDaysOff = new HashSet<>(instance.getEmployeeDaysOff(employeeId));
    Map<String, Integer> employeeShifts = Utils.shallowCopy(employee.getMaxShifts());

    for (int i = 0; i < horizon; i++) {
      // Hard constraint DAYS_OFF
      if (employeesDaysOff.contains(i)) {
        continue;
      }

      String shiftId = shiftDistributer.distributeShift(employeeShifts);
      employeeShifts.put(shiftId, employeeShifts.get(shiftId) - 1);       // decrease chosen shift
      solution.setShift(employeeId, i, shiftId);
    }
  }
}
