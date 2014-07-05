package simple.escp.data;

import org.junit.Test;
import simple.escp.exception.InvalidPlaceholder;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

public class MapDataSourceTest {

    @Test
    public void hasMember() {
        Map<String, String> source = new HashMap<>();
        source.put("name", "Solid Snake");
        MapDataSource ds = new MapDataSource(source);
        assertTrue(ds.has("name"));
        assertFalse(ds.has("nickname"));
    }

    @Test
    public void getMember() {
        Map<String, String> source = new HashMap<>();
        source.put("name", "Solid Snake");
        MapDataSource ds = new MapDataSource(source);
        assertEquals("Solid Snake", ds.get("name"));
    }

    @Test(expected = InvalidPlaceholder.class)
    public void getInvalidMember() {
        Map<String, String> source = new HashMap<>();
        source.put("name", "Solid Snake");
        MapDataSource ds = new MapDataSource(source);
        assertEquals("Solid Snake", ds.get("nickname"));
    }

    @Test
    public void getSource() {
        Map<String, String> source = new HashMap<>();
        source.put("name", "Solid Snake");
        MapDataSource ds = new MapDataSource(source);
        assertEquals(source, ds.getSource());
    }

}
