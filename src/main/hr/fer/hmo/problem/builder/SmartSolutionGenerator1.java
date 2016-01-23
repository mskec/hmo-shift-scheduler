package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.data.Shift;
import hr.fer.hmo.problem.ShiftDistributor;
import hr.fer.hmo.problem.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    // Map from <shiftId, maxShifts> to list EmployeeShift
    Map<String, EmployeeShift> employeeShifts = new HashMap<>();
    for (String shiftId : employee.getMaxShifts().keySet()) {
      employeeShifts.put(shiftId, new EmployeeShift(shiftId, employee.getMaxShifts().get(shiftId)));
    }

    // <previousShiftId, ...>
    Map<String, List<EmployeeShift>> validEmployeeShifts = generateValidEmployeeShifts(instance.getShifts(), employeeShifts);

    String previousShiftId = "";
    for (int i = 0; i < horizon; i++) {
      // Hard constraint DAYS_OFF
      if (employeesDaysOff.contains(i)) {
        previousShiftId = "";
        continue;
      }

      String shiftId = shiftDistributor.distributeShift(validEmployeeShifts.get(previousShiftId));
      if (!shiftId.isEmpty()) {
        employeeShifts.get(shiftId).remainingShifts--;       // decrease chosen shift
        solution.setShift(employeeId, i, shiftId);
      }

      previousShiftId = shiftId;
    }
  }

  // Slow method
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

  // Faster method
  public Map<String, List<EmployeeShift>> generateValidEmployeeShifts(Map<String, Shift> shifts, Map<String, EmployeeShift> employeeShifts) {
    Map<String, List<EmployeeShift>> validEmployeeShifts = new HashMap<>();

    for (String employeeShiftId : employeeShifts.keySet()) {
      validEmployeeShifts.put(employeeShiftId, new ArrayList<>());
      Shift shift = shifts.get(employeeShiftId);

      shifts.keySet().stream()
          .filter(shiftId -> !shift.getNotFollowingShifts().contains(shiftId))
          .forEach(shiftId -> validEmployeeShifts.get(employeeShiftId).add(employeeShifts.get(shiftId)));
    }

    // Initialize for when previous day employee did not work
    validEmployeeShifts.put("", new ArrayList<>());
    for (String shiftId : employeeShifts.keySet()) {
      validEmployeeShifts.get("").add(employeeShifts.get(shiftId));
    }

    return validEmployeeShifts;
  }

  public static class EmployeeShift {
    public String shiftId;
    public int remainingShifts;
    public EmployeeShift(String shiftId, int remainingShifts) {
      this.shiftId = shiftId;
      this.remainingShifts = remainingShifts;
    }
  }
}
