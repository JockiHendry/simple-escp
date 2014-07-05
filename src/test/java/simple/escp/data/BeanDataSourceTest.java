package simple.escp.data;

import org.junit.Test;
import simple.escp.exception.InvalidPlaceholder;
import static org.junit.Assert.*;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

public class BeanDataSourceTest {

    @Test
    public void getMethod() {
        Employee emp = new Employee("test", 10.0, 24.0);
        BeanDataSource ds = new BeanDataSource(emp);
        MethodDescriptor md = ds.getMethod("grossIncome");
        assertEquals("grossIncome", md.getName());
    }

    @Test
    public void getProperty() {
        Employee emp = new Employee("test", 10.0, 24.0);
        BeanDataSource ds = new BeanDataSource(emp);
        PropertyDescriptor pd = ds.getProperty("name");
        assertEquals("name", pd.getName());
        pd = ds.getProperty("hourRate");
        assertEquals("hourRate", pd.getName());
        pd = ds.getProperty("workHours");
        assertEquals("workHours", pd.getName());
    }

    @Test
    public void hasMember() {
        Employee emp = new Employee("test", 10.0, 24.0);
        BeanDataSource ds = new BeanDataSource(emp);
        assertTrue(ds.has("name"));
        assertTrue(ds.has("hourRate"));
        assertTrue(ds.has("workHours"));
        assertTrue(ds.has("@grossIncome"));
        assertFalse(ds.has("nickname"));
        assertFalse(ds.has("@tax"));
    }

    @Test
    public void getMember() {
        Employee emp = new Employee("test", 10.0, 24.0);
        BeanDataSource ds = new BeanDataSource(emp);
        assertEquals("test", ds.get("name"));
        assertEquals(10.0, ds.get("hourRate"));
        assertEquals(24.0, ds.get("workHours"));
        assertEquals(240.0, ds.get("@grossIncome"));
    }

    @Test(expected = InvalidPlaceholder.class)
    public void getInvalidMember() {
        Employee emp = new Employee("test", 10.0, 24.0);
        BeanDataSource ds = new BeanDataSource(emp);
        ds.get("invalid");
    }

    @Test
    public void getSource() {
        Employee emp = new Employee("test", 10.0, 24.0);
        BeanDataSource ds = new BeanDataSource(emp);
        assertEquals(emp, ds.getSource());
    }

    public static class Employee {
        private String name;
        private double hourRate;
        private double workHours;

        public Employee(String name, double hourRate, double workHours) {
            this.name = name;
            this.hourRate = hourRate;
            this.workHours = workHours;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getHourRate() {
            return hourRate;
        }

        public void setHourRate(double hourRate) {
            this.hourRate = hourRate;
        }

        public double getWorkHours() {
            return workHours;
        }

        public void setWorkHours(double workHours) {
            this.workHours = workHours;
        }

        public double grossIncome() {
            return hourRate * workHours;
        }
    }
}
