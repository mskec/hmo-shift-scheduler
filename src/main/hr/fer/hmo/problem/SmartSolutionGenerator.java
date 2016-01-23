package hr.fer.hmo.problem;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.parser.InstanceParser;
import hr.fer.hmo.problem.builder.SolutionGenerator;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dstankovic on 1/20/16.
 */
public class SmartSolutionGenerator implements SolutionGenerator {
  @Override
  public void generate(Instance instance, Solution solution, String employeeId) {
    solution.anulateEmployeeShifts(employeeId);
    List<Integer> daysOff = instance.getEmployeeDaysOff(employeeId);
    Employee employee = instance.getEmployee(employeeId);
    WorkingDaysDistributor workingDaysDistributor = new WorkingDaysDistributor();
    ShiftDistributor shiftDistributor = new ShiftDistributor();
    int minConsecutiveShifts = employee.getMinConsecutiveShifts();
    int lastDayOff = -1;
    Map<String, Integer> shiftsCounter = new HashMap<>(employee.getMaxShifts());
    int daysOffSize = daysOff.size();
    for (int j = 0; j <= daysOffSize; j++) {
      Integer dayOff;
      if (j == daysOffSize) {
        dayOff = instance.getHorizon();
      } else {
        dayOff = daysOff.get(j);
      }
      // there is gap big enough between two days off
      int gap = dayOff - lastDayOff - 1;
      if (gap >= minConsecutiveShifts) {
        List<Integer> list = workingDaysDistributor.distribute(employee, gap);
        int size = list.size();
        int offset = 0;
        for (int i = 0; i < size; i+=2) {
          Integer daysOn = list.get(i);
          String shiftId = shiftDistributor.distributeShift(shiftsCounter);
          shiftsCounter.put(shiftId, shiftsCounter.get(shiftId)-daysOn);
          setSolution(solution, employeeId, lastDayOff+offset+1, daysOn, shiftId);
          offset += daysOn;
          if (i < size - 1) {
            // not last one
            int daysOffCount = list.get(i+1);
            setSolution(solution, employeeId, lastDayOff+offset+1, daysOffCount, null);
            offset += daysOffCount;
          }
        }
      }
      lastDayOff = dayOff;
    }
  }

  private void setSolution(Solution solution, String employeeId, int firstDay, int daysOn, String shiftId) {
    for (int i = 0; i < daysOn; i++) {
      solution.setShift(employeeId, firstDay+i, shiftId);
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    SmartSolutionGenerator smartSolutionGenerator = new SmartSolutionGenerator();
    Instance instance = InstanceParser.parse("instance.txt");
    List<String> employeeIds = instance.getEmployeeIds();
    Validator validator = new Validator();
    Solution solution = new Solution(employeeIds, instance.getHorizon());
    for (String employeeId : employeeIds) {
      System.out.println(employeeId);
      for (int i = 0; i < 100000; i++) {
        smartSolutionGenerator.generate(instance, solution, employeeId);
        int brokenConstraintsCount = validator.validateHardConstraints(instance, solution, employeeId);
        if (brokenConstraintsCount <= 1) {
          System.out.println();
          System.out.println(brokenConstraintsCount);
//          System.out.println(solution.toString());
          if (brokenConstraintsCount == 0) break;
        } else {
//          System.out.print(brokenConstraintsCount + " ");
        }
      }
    }
    System.out.println("Final solution:");
    System.out.println(solution);
    validator.setPrint(true);
    System.out.println(validator.validateHardConstraints(instance, solution));
    System.out.println(validator.validateSoftConstraints(instance, solution));
  }
}
