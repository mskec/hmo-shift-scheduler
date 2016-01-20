package hr.fer.hmo.problem;

import hr.fer.hmo.data.Employee;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by dstankovic on 1/20/16.
 */
public class WorkingDaysDistributerTest {

  @Test
  public void testDistribute() throws Exception {
    WorkingDaysDistributer workingDaysDistributer = new WorkingDaysDistributer();
    Employee employee = new Employee(null, null, 0, 0, 5, 2, 3, 0);
    int num = 50;
    for (int iter = 1000; iter >= 0; iter--) {
      List<Integer> list = workingDaysDistributer.distribute(employee, num);
//    System.out.println(list);
      int totalCount = 0;
      for (int i = list.size() - 2; i >= 0; i--) {
        totalCount += list.get(i);
      }
      Assert.assertTrue(totalCount <= num);
    }
//    System.out.println(totalCount);
  }
}