package simple.escp.data;

import org.junit.Before;
import org.junit.Test;
import javax.json.JsonObject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JsonDataSourceTest {

    private String jsonString;

    @Before
    public void setup() {
        jsonString = "{" +
            "\"name\": \"Steven\"," +
            "\"age\": 28," +
            "\"registered\": true," +
            "\"address\": {" +
                "\"line1\": \"address line 1\"," +
                "\"line2\": \"address line 2\"" +
            "}," +
            "\"history\": [" +
                "{ \"date\": 1, \"value\": 10 }," +
                "{ \"date\": 2, \"value\": 20 }," +
                "{ \"date\": 3, \"value\": 30 }" +
            "]" +
        "}";
    }

    @Test
    public void hasMember() {
        JsonDataSource ds  = new JsonDataSource(jsonString);
        assertTrue(ds.has("name"));
        assertTrue(ds.has("age"));
        assertTrue(ds.has("registered"));
        assertTrue(ds.has("address"));
        assertTrue(ds.has("history"));
        assertFalse(ds.has("unknown"));
    }

    @Test
    public void getMember() {
        JsonDataSource ds  = new JsonDataSource(jsonString);
        assertEquals("Steven", ds.get("name"));
        assertEquals(new BigDecimal("28"), ds.get("age"));
        assertEquals(true, ds.get("registered"));
        assertEquals("address line 1", ((JsonObject)ds.get("address")).getString("line1"));
        assertEquals("address line 2", ((JsonObject)ds.get("address")).getString("line2"));
        assertEquals(3, ((List)ds.get("history")).size());
    }

    @Test
    public void getMembers() {
        JsonDataSource ds = new JsonDataSource(jsonString);
        List<String> result = Arrays.asList(ds.getMembers());
        assertTrue(result.contains("name"));
        assertTrue(result.contains("age"));
        assertTrue(result.contains("registered"));
        assertTrue(result.contains("address"));
        assertTrue(result.contains("history"));
    }

}
