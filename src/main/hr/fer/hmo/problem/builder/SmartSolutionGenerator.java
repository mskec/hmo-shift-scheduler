package hr.fer.hmo.problem.builder;

import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.parser.InstanceParser;
import hr.fer.hmo.problem.ShiftDistributor;
import hr.fer.hmo.problem.Solution;
import hr.fer.hmo.problem.Validator;
import hr.fer.hmo.problem.WorkingDaysDistributor;
import hr.fer.hmo.utils.Utils;

import java.io.FileNotFoundException;
import java.util.*;

public class SmartSolutionGenerator implements SolutionGenerator {
  @Override
  public void generate(Instance instance, Solution solution, String employeeId) {
    Random rand = new Random();
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
        int possibleOffset = gap - listSum(list);
        int iOffset = rand.nextInt(possibleOffset + 1);
        int size = list.size();
        int offset = 0;
        for (int i = 0; i < size; i+=2) {
          Integer daysOn = list.get(i);
          String shiftId = shiftDistributor.distributeShift(shiftsCounter);
          shiftsCounter.put(shiftId, shiftsCounter.get(shiftId)-daysOn);
          setSolution(solution, employeeId, lastDayOff+offset+1+iOffset, daysOn, shiftId);
          offset += daysOn;
          if (i < size - 1) {
            // not last one
            int daysOffCount = list.get(i+1);
            setSolution(solution, employeeId, lastDayOff+offset+1+iOffset, daysOffCount, null);
            offset += daysOffCount;
          }
        }
      }
      lastDayOff = dayOff;
    }

    // check if max total shifts constraint is broken
    int workingMinutes = 0;
    int horizon = instance.getHorizon();
    int maxWorkingMinutes = employee.getMaxTotalMinutes();
    for (int i = 0; i < horizon; i++) {
      String shiftId = solution.getShift(employeeId, i);
      if (shiftId != null) {
        workingMinutes += instance.getShifts().get(shiftId).getLength();
      }
    }

    int workingMinutesDiff = workingMinutes - maxWorkingMinutes;
    List<ShiftChunk> shiftChunks = extractShiftChunks(solution, employeeId);
//    List<ShiftChunk> shiftChunks = extractBigShiftChunks(originalShiftChunks, employee.getMinConsecutiveShifts());
    while (!shiftChunks.isEmpty() && workingMinutesDiff > 0) {
      int chunkCount = shiftChunks.size();
      int weekendChunkIndex = -1;
      for (int i = chunkCount-1; i >= 0; i--) {
        if (shiftChunks.get(i).isBorderWeekend()) {
          weekendChunkIndex = i;
          break;
        }
      }
      if (weekendChunkIndex < 0) {
        // none of the chunks border is weekend
        weekendChunkIndex = rand.nextInt(chunkCount);
      }
      ShiftChunk shiftChunk = shiftChunks.get(weekendChunkIndex);
      int day;
      if (Utils.isWeekend(shiftChunk.getL())) {
        day = shiftChunk.getL();
        shiftChunk.lTrim();
      } else if (Utils.isWeekend(shiftChunk.getR())) {
        day = shiftChunk.getR();
        shiftChunk.rTrim();
      } else {
        if (rand.nextBoolean()) {
          day = shiftChunk.getL();
          shiftChunk.lTrim();
        } else {
          day = shiftChunk.getR();
          shiftChunk.rTrim();
        }
      }
      String deletedShiftId = solution.getShift(employeeId, day);
      solution.setShift(employeeId, day, null);
      workingMinutesDiff -= instance.getShifts().get(deletedShiftId).getLength();
      if (shiftChunk.length() < minConsecutiveShifts) {
        for (int j = shiftChunk.getL(); j <= shiftChunk.getR(); j++) {
          deletedShiftId = solution.getShift(employeeId, j);
          solution.setShift(employeeId, j, null);
          workingMinutesDiff -= instance.getShifts().get(deletedShiftId).getLength();
        }
        shiftChunks.remove(weekendChunkIndex);
      }
    }
  }


  private static List<ShiftChunk> extractBigShiftChunks(List<ShiftChunk> shiftChunks, int minConsecutiveShifts) {
    List<ShiftChunk> list = new ArrayList<>(shiftChunks.size());
    for (ShiftChunk chunk : shiftChunks) {
      if (chunk.length() > minConsecutiveShifts) {
        list.add(chunk);
      }
    }
    return list;
  }


  private static class ShiftChunk {
    private int l;
    private int r;

    public ShiftChunk(int l, int r) {
      this.l = l;
      this.r = r;
      if (r < l) {
        throw new IllegalArgumentException("r < l");
      }
    }

    public int getL() {
      return l;
    }

    public int getR() {
      return r;
    }

    public int length() {
      return r - l + 1;
    }

    public void lTrim() {
      l++;
    }

    public void rTrim() {
      r--;
    }

    public boolean isBorderWeekend() {
      return Utils.isWeekend(l) || Utils.isWeekend(r);
    }
  }

  private static List<ShiftChunk> extractShiftChunks(Solution solution, String employeeId) {
    List<ShiftChunk> shiftChunks = new ArrayList<>();
    int start = 0;
    while (solution.getShift(employeeId, start) == null) {
      start++;
    }
    int horizon = solution.getHorizon();
    for (int i = start; i < horizon; i++) {
      String shiftId = solution.getShift(employeeId, i);
      if (shiftId == null) {
        // first day off in series
        ShiftChunk shiftChunk = new ShiftChunk(start, i-1);
        shiftChunks.add(shiftChunk);
        i++;

        // move to next start of chunk
        while (i < horizon && solution.getShift(employeeId, i) == null) {
          i++;
        }
        start = i;
      }
    }
    if (start < horizon) {
      shiftChunks.add(new ShiftChunk(start, horizon-1));
    }
    return shiftChunks;
  }


  private static int listSum(List<Integer> list) {
    int sum = 0;
    for (Integer i : list) {
      sum += i;
    }
    return sum;
  }


  private void setSolution(Solution solution, String employeeId, int firstDay, int daysOn, String shiftId) {
    for (int i = 0; i < daysOn; i++) {
      solution.setShift(employeeId, firstDay+i, shiftId);
    }
  }


  private static class Weekend {
    // true if employee is working on that day
    public boolean saturday;
    public boolean sunday;
    public int saturdayIndex;

    public Weekend(boolean saturday, boolean sunday, int saturdayIndex) {
      this.saturday = saturday;
      this.sunday = sunday;
      this.saturdayIndex = saturdayIndex;
    }

    public boolean isWorking() {
      return saturday || sunday;
    }
  }


  private List<Integer> modifyDaysOff(Instance instance, List<Integer> daysOff, String employeeId) {
    int maxWeekends = instance.getEmployees().get(employeeId).getMaxWeekends();
    int horizon = instance.getHorizon();
    Set<Integer> weekendDaysOff = new HashSet<>();
    for (Integer dayOff : daysOff) {
      int mod = dayOff % 7;
      if (mod >= 5) {
        // it's weekend
        weekendDaysOff.add(dayOff);
      }
    }

    List<Weekend> workingWeekends1 = new ArrayList<>();
    List<Weekend> workingWeekends2 = new ArrayList<>();
    for (int saturday = 5; saturday < horizon; saturday+=7) {
      boolean workingSaturday = !weekendDaysOff.contains(saturday);
      boolean workingSunday = !weekendDaysOff.contains(saturday + 1);
      if (workingSaturday || workingSunday) {
        // working on weekend
        Weekend weekend = new Weekend(workingSaturday, workingSunday, saturday);
        if (workingSaturday ^ workingSunday) {
          // works only one day
          workingWeekends1.add(weekend);
        } else {
          // works both days
          workingWeekends2.add(weekend);
        }
      }
    }
    int weekendsToRemove = workingWeekends1.size() + workingWeekends2.size() - maxWeekends;
    if (weekendsToRemove <= 0) {
      return daysOff;
    }
    TreeSet<Integer> sortedSet = new TreeSet<>(daysOff);
    weekendsToRemove = removeWeekends(sortedSet, workingWeekends1, weekendsToRemove);
    removeWeekends(sortedSet, workingWeekends2, weekendsToRemove);
    return new ArrayList<>(sortedSet);
  }

  private static int removeWeekends(TreeSet<Integer> sortedSet, List<Weekend> workingWeekends, int weekendsToRemove) {
    Random rand = new Random();
    while (workingWeekends.size() > 0 && weekendsToRemove > 0) {
      int i = rand.nextInt(workingWeekends.size());
      Weekend weekend = workingWeekends.get(i);
      int saturday = weekend.saturdayIndex;
      sortedSet.add(saturday);
      sortedSet.add(saturday + 1);
      workingWeekends.remove(i);
      weekendsToRemove--;
    }
    return weekendsToRemove;
  }


  public static void main(String[] args) throws FileNotFoundException {
    runSmartGenerator(true);
  }

  private static void runSmartGenerator(boolean print) throws FileNotFoundException {
    SmartSolutionGenerator smartSolutionGenerator = new SmartSolutionGenerator();
    Instance instance = InstanceParser.parse("instance.txt");
    List<String> employeeIds = instance.getEmployeeIds();
    Validator validator = new Validator();
    Solution best = null;
    for (int iter = 0; iter < 100; iter++) {
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
  }
}
