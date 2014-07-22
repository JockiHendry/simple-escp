package simple.escp.data;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import simple.escp.fill.FillJob;
import simple.escp.json.JsonTemplate;
import simple.escp.util.EscpUtil;
import javax.json.JsonObject;
import java.util.List;
import static org.junit.Assert.*;
import static simple.escp.util.EscpUtil.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomDataSourceTest {

    private final String INIT = EscpUtil.escInitalize();

    @Test
    public void customDataSource_01() {
        String jsonTemplate = "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 90" +
            "}," +
            "\"template\": [" +
                "\"Value: ${line1}\"," +
                "{" +
                    "\"table\": \"tables\"," +
                    "\"columns\": [" +
                        "{\"source\": \"data\", \"width\": 13}" +
                    "]" +
                "}" +
            "]" +
        "}";

        String jsonData = "{" +
            "\"line1\": \"This is line1\"," +
            "\"tables\": [" +
                "{\"data\": \"This is data1\"}," +
                "{\"data\": \"This is data2\"}," +
                "{\"data\": \"This is data3\"}" +
            "]" +
        "}";

        DataSources.register(String.class, CustomJsonDataSource.class);
        DataSources.register(JsonObject.class, CustomJsonDataSource.class);

        String result = new FillJob(new JsonTemplate(jsonTemplate).parse(), DataSources.from(jsonData)).fill();
        assertEquals( INIT +
            "Value: This is line1" + CRLF +
            "data         " + CRLF +
            "This is data1" + CRLF +
            "This is data2" + CRLF +
            "This is data3" + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void customDataSource_02() {
        DataSources.register(String.class, CustomJsonDataSource.class);
    }

    @Test
    public void customDataSource_03() {
        DataSources.unregister(CustomJsonDataSource.class);
        List<DataSources.DataSourceEntry> dataSources = DataSources.DATA_SOURCES;
        assertEquals(2, dataSources.size());
        assertEquals(BeanDataSource.class, dataSources.get(0).getDataSourceType());
        assertEquals(MapDataSource.class, dataSources.get(1).getDataSourceType());
    }

}
