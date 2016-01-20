package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.data.Shift;
import hr.fer.hmo.problem.ShiftDistributor;
import hr.fer.hmo.problem.Solution;
import hr.fer.hmo.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    Map<String, Integer> employeeShifts = Utils.shallowCopy(employee.getMaxShifts());

    String previousShiftId = null;
    for (int i = 0; i < horizon; i++) {
      // Hard constraint DAYS_OFF
      if (employeesDaysOff.contains(i)) {
        previousShiftId = null;
        continue;
      }

      Map<String, Integer> validEmployeeShifts = getValidEmployeeShifts(instance.getShifts().get(previousShiftId), employeeShifts);
      String shiftId = shiftDistributor.distributeShift(validEmployeeShifts);

      if (shiftId != null) {
        employeeShifts.put(shiftId, employeeShifts.get(shiftId) - 1);       // decrease chosen shift
        solution.setShift(employeeId, i, shiftId);
      }

      previousShiftId = shiftId;
    }
  }

  private Map<String, Integer> getValidEmployeeShifts(Shift previousShift, Map<String, Integer> employeeShifts) {
    if (previousShift == null) {
      return employeeShifts;
    }

    Map<String, Integer> validEmployeeShifts = new HashMap<>();
    employeeShifts.keySet().stream()
        .filter(shiftId -> !previousShift.getNotFollowingShifts().contains(shiftId))
        .forEach(shiftId -> validEmployeeShifts.put(shiftId, employeeShifts.get(shiftId)));

    return validEmployeeShifts;
  }
}
