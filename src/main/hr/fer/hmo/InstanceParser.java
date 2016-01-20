package hr.fer.hmo;

import hr.fer.hmo.data.DaysOff;
import hr.fer.hmo.data.Employee;
import hr.fer.hmo.data.Instance;
import hr.fer.hmo.data.Shift;
import hr.fer.hmo.data.ShiftCover;
import hr.fer.hmo.data.ShiftRequest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class InstanceParser {

  public static Instance parse(String filename) throws FileNotFoundException {

    Scanner in = new Scanner(new FileReader(filename));

    checkSection(nextLine(in), "SECTION_HORIZON");
    int horizon = parseHorizonSection(in);

    checkSection(nextLine(in), "SECTION_SHIFTS");
    Map<String, Shift> shifts = parseShifts(in);
    Map<String, Employee> employees = parseEmployees(in);
    Map<String, DaysOff> daysOff = parseDaysOff(in);

    List<ShiftRequest> shiftOnRequests = parseShiftRequests(in, "SECTION_SHIFT_OFF_REQUESTS");
    List<ShiftRequest> shiftOffRequests = parseShiftRequests(in, "SECTION_COVER");
    List<ShiftCover> shiftCovers = parseShiftCovers(in);

    return new Instance(horizon, shifts, employees, daysOff, shiftOnRequests, shiftOffRequests, shiftCovers);
  }

  private static List<ShiftCover> parseShiftCovers(Scanner in) {
    List<ShiftCover> shiftCovers = new ArrayList<>();

    while (true) {
      String line;
      try {
        line = nextLine(in);
      } catch (NoSuchElementException e) {
        break;
      }
      String[] lineTokens = line.trim().split(",");

      int day = Integer.parseInt(lineTokens[0]);
      String shiftId = lineTokens[1];
      int requirement = Integer.parseInt(lineTokens[2]);
      int weightUnder = Integer.parseInt(lineTokens[3]);
      int weightOver = Integer.parseInt(lineTokens[4]);

      shiftCovers.add(new ShiftCover(day, shiftId, requirement, weightUnder, weightOver));
    }

    return shiftCovers;
  }

  private static List<ShiftRequest> parseShiftRequests(Scanner in, String nextSection) {
    List<ShiftRequest> shiftRequests = new ArrayList<>();

    while (true) {
      String[] lineTokens = nextLine(in).trim().split(",");

      if (lineTokens.length == 1) {
        checkSection(lineTokens[0], nextSection);
        break;
      }

      String employeeId = lineTokens[0];
      int day = Integer.parseInt(lineTokens[1]);
      String shiftId = lineTokens[2];
      int weight = Integer.parseInt(lineTokens[3]);

      shiftRequests.add(new ShiftRequest(employeeId, day, shiftId, weight));
    }

    return shiftRequests;
  }

  private static Map<String, DaysOff> parseDaysOff(Scanner in) {
    Map<String, DaysOff> daysOffMap = new HashMap<>();

    while (true) {
      String[] lineTokens = nextLine(in).trim().split(",");

      if (lineTokens.length == 1) {
        checkSection(lineTokens[0], "SECTION_SHIFT_ON_REQUESTS");
        break;
      }

      String employeeId = lineTokens[0];
      List<Integer> daysOffList = new ArrayList<>(lineTokens.length - 1);
      for (int i = 1; i < lineTokens.length; i++) {
        daysOffList.add(Integer.parseInt(lineTokens[i]));
      }

      daysOffMap.put(employeeId, new DaysOff(employeeId, daysOffList));
    }

    return daysOffMap;
  }

  private static Map<String, Employee> parseEmployees(Scanner in) {
    Map<String, Employee> employees = new HashMap<>();

    while (true) {
      String[] lineTokens = nextLine(in).trim().split(",");

      if (lineTokens.length == 1) {
        checkSection(lineTokens[0], "SECTION_DAYS_OFF");
        break;
      }

      String employeeId = lineTokens[0];

      Map<String, Integer> maxShifts = new HashMap<>();
      String[] shiftIdMaxDaysTokens = lineTokens[1].split("\\|");
      for (String shiftIdMaxDaysToken : shiftIdMaxDaysTokens) {
        String[] tokens = shiftIdMaxDaysToken.split("=");
        maxShifts.put(tokens[0], Integer.parseInt(tokens[1]));
      }

      int maxTotalMinutes = Integer.parseInt(lineTokens[2]);
      int minTotalMinutes = Integer.parseInt(lineTokens[3]);
      int maxConsecutiveShifts = Integer.parseInt(lineTokens[4]);
      int minConsecutiveShifts = Integer.parseInt(lineTokens[5]);
      int minConsecutiveDaysOff = Integer.parseInt(lineTokens[6]);
      int maxWeekends = Integer.parseInt(lineTokens[7]);

      Employee employee = new Employee(employeeId, maxShifts, maxTotalMinutes, minTotalMinutes, maxConsecutiveShifts, minConsecutiveShifts, minConsecutiveDaysOff, maxWeekends);
      employees.put(employeeId, employee);
    }

    return employees;
  }

  private static Map<String, Shift> parseShifts(Scanner in) {
    Map<String, Shift> shifts = new HashMap<>();

    while (true) {
      String[] lineTokens = nextLine(in).trim().split(",");

      if (lineTokens.length == 1) {
        checkSection(lineTokens[0], "SECTION_STAFF");
        break;
      }

      String shiftId = lineTokens[0];
      int length = Integer.parseInt(lineTokens[1]);
      Set<String> notFollowingShifts = Collections.emptySet();

      if (lineTokens.length == 3) {
        notFollowingShifts = new HashSet<>(Arrays.asList(lineTokens[2].split("\\|")));
      }

      Shift shift = new Shift(shiftId, length, notFollowingShifts);
      shifts.put(shiftId, shift);
    }

    return shifts;
  }

  private static int parseHorizonSection(Scanner in) {
    return Integer.parseInt(nextLine(in));
  }


  /**
   * Method checks that correct section is being parsed
   */
  private static void checkSection(String line, String sectionName) {
    if (!sectionName.equals(line)) {
      throw new IllegalStateException();
    }
  }

  /**
   * Method is returning next valid line.
   * Invalid line is comment line or new line line
   */
  private static String nextLine(Scanner in) {
    String line;

    while (true) {
      line = in.nextLine().trim();

      if (!line.startsWith("#") && !line.isEmpty()) {
        break;
      }
    }
    return line;
  }

}
