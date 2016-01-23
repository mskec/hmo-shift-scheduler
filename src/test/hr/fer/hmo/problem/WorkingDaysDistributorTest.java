package hr.fer.hmo.problem;

import hr.fer.hmo.data.Employee;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class WorkingDaysDistributorTest {

  @Test
  public void testDistribute() throws Exception {
    WorkingDaysDistributor workingDaysDistributor = new WorkingDaysDistributor();
    Employee employee = new Employee(null, null, 0, 0, 5, 2, 3, 0);
    int num = 50;
    for (int iter = 1000; iter >= 0; iter--) {
      List<Integer> list = workingDaysDistributor.distribute(employee, num);
    System.out.println(list);
      int totalCount = 0;
      for (Integer i : list) {
        totalCount += i;
      }
      Assert.assertTrue(totalCount <= num);
    }
  }
}