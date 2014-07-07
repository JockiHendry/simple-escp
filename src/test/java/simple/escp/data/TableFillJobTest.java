package simple.escp.data;

import org.junit.Test;
import simple.escp.TableColumn;
import simple.escp.fill.TableFillJob;
import simple.escp.TableLine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class TableFillJobTest {

    @Test
    public void fillFromMap() {
        TableLine tableLine = new TableLine("emps");
        tableLine.addColumn(new TableColumn("name", 10));
        tableLine.addColumn(new TableColumn("hourRate", 6));
        tableLine.addColumn(new TableColumn("workHours", 6));
        tableLine.addColumn(new TableColumn("grossIncome", 10));

        List<Map> sources = new ArrayList<>();
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("name", "emp1");
        emp1.put("hourRate", 10.0);
        emp1.put("workHours", 5.0);
        emp1.put("grossIncome", 10.0 * 5.0);
        sources.add(emp1);
        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("name", "emp2");
        emp2.put("hourRate", 5.0);
        emp2.put("workHours", 4.0);
        emp2.put("grossIncome", 5.0 * 4.0);
        sources.add(emp2);
        Map<String, Object> emp3 = new HashMap<>();
        emp3.put("name", "emp3");
        emp3.put("hourRate", 3.0);
        emp3.put("workHours", 2.0);
        emp3.put("grossIncome", 3.0 * 2.0);
        sources.add(emp3);

        TableFillJob job = new TableFillJob(tableLine, sources);
        List<String> results = job.fill();
        assertEquals("emp1        10,0   5,0      50,0", results.get(0));
        assertEquals("emp2         5,0   4,0      20,0", results.get(1));
        assertEquals("emp3         3,0   2,0       6,0", results.get(2));
    }

    @Test
    public void fillFromObject() {
        TableLine tableLine = new TableLine("emps");
        tableLine.addColumn(new TableColumn("name", 10));
        tableLine.addColumn(new TableColumn("hourRate", 6));
        tableLine.addColumn(new TableColumn("workHours", 6));
        tableLine.addColumn(new TableColumn("@grossIncome", 10));

        List<BeanDataSourceTest.Employee> sources = new ArrayList<>();
        sources.add(new BeanDataSourceTest.Employee("emp1", 10.0, 5.0));
        sources.add(new BeanDataSourceTest.Employee("emp2", 5.0, 4.0));
        sources.add(new BeanDataSourceTest.Employee("emp3", 3.0, 2.0));

        TableFillJob job = new TableFillJob(tableLine, sources);
        List<String> results = job.fill();
        assertEquals("emp1        10,0   5,0      50,0", results.get(0));
        assertEquals("emp2         5,0   4,0      20,0", results.get(1));
        assertEquals("emp3         3,0   2,0       6,0", results.get(2));    }

}
