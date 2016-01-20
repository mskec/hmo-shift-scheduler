package hr.fer.hmo.problem;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.parser.InstanceParser;
import hr.fer.hmo.problem.builder.SolutionGenerator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dstankovic on 1/20/16.
 */
public class SmartSolutionGenerator implements SolutionGenerator {
  @Override
  public void generate(Instance instance, Solution solution, String employeeId) {
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
          setSolution(solution, employeeId, lastDayOff+offset+1, daysOn, shiftDistributor, shiftsCounter);
          offset += daysOn;
          if (i < size - 1) {
            // not last one
            offset += list.get(i+1);
          }
        }
      }
      lastDayOff = dayOff;
    }
  }

  private void setSolution(Solution solution, String employeeId, int firstDay, int daysOn,
                           ShiftDistributor shiftDistributor, Map<String, Integer> shiftsCounter) {
    for (int i = 0; i < daysOn; i++) {
      String shiftId = shiftDistributor.distributeShift(shiftsCounter);
      shiftsCounter.put(shiftId, shiftsCounter.get(shiftId)-1);
      solution.setShift(employeeId, firstDay+i, shiftId);
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    SmartSolutionGenerator smartSolutionGenerator = new SmartSolutionGenerator();
    List<String> employeeIds = new ArrayList<>(1);
    String employeeId = "A";
    employeeIds.add(employeeId);
    Instance instance = InstanceParser.parse("instance.txt");
    Solution solution = new Solution(employeeIds, instance.getHorizon());
    Validator validator = new Validator();
    for (int i = 0; i < 10; i++) {
      smartSolutionGenerator.generate(instance, solution, employeeId);
      int brokenConstraintsCount = validator.validateHardConstraints(instance, solution);
      if (brokenConstraintsCount > 3) {
        System.out.println(brokenConstraintsCount);
        System.out.println(solution.toString());
      }
    }
  }
}
