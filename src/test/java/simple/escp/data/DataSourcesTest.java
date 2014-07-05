package simple.escp.data;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

public class DataSourcesTest {

    @Test
    public void fromObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", "solid");
        map.put("lastName", "snake");
        DataSource ds = DataSources.from(map);
        assertEquals(MapDataSource.class, ds.getClass());
        assertEquals(map, ds.getSource());

        BeanDataSourceTest.Employee emp = new BeanDataSourceTest.Employee("test", 10.0, 20.0);
        ds = DataSources.from(emp);
        assertEquals(BeanDataSource.class, ds.getClass());
        assertEquals(emp, ds.getSource());
    }

    @Test
    public void fromObjectArray() {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", "solid");
        map.put("lastName", "snake");
        BeanDataSourceTest.Employee emp = new BeanDataSourceTest.Employee("test", 10.0, 20.0);
        DataSource[] ds = DataSources.from(new Object[]{map, emp});

        assertEquals(MapDataSource.class, ds[0].getClass());
        assertEquals(map, ds[0].getSource());
        assertEquals(BeanDataSource.class, ds[1].getClass());
        assertEquals(emp, ds[1].getSource());
    }

    @Test
    public void fromMapAndBean() {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", "solid");
        map.put("lastName", "snake");
        BeanDataSourceTest.Employee emp = new BeanDataSourceTest.Employee("test", 10.0, 20.0);
        DataSource[] ds = DataSources.from(map, emp);

        assertEquals(MapDataSource.class, ds[0].getClass());
        assertEquals(map, ds[0].getSource());
        assertEquals(BeanDataSource.class, ds[1].getClass());
        assertEquals(emp, ds[1].getSource());
    }

}
