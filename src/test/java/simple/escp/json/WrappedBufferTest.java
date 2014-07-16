package simple.escp.json;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import simple.escp.data.DataSource;
import simple.escp.data.DataSources;
import simple.escp.dom.Report;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;
import simple.escp.fill.DataSourceBinding;
import simple.escp.fill.TableFillHelper;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrappedBufferTest {

    private TableFillHelper.WrappedBuffer wrappedBuffer;
    private Report generatedReport;

    @Before
    public void setup() throws URISyntaxException, IOException {
        JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table_wrap.json").toURI());
        List<JsonTemplateFillTest.Person> persons = new ArrayList<>();
        persons.add(new JsonTemplateFillTest.Person("None12345678901234567890", "David12345678901234567890", "None12345678901234567890"));
        persons.add(new JsonTemplateFillTest.Person("David12345678901234567890", "Solid", "Snake12345678901234567890"));
        persons.add(new JsonTemplateFillTest.Person("Snake12345678901234567890", "Jocki", "Hendry12345678901234567890"));
        Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        DataSource ds = DataSources.from(source);
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngineManager.setBindings(new DataSourceBinding(new DataSource[]{ds}));
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        Report report = jsonTemplate.parse();
        generatedReport = new Report(3, null, null);
        TableLine tableLine = report.getFirstPageWithTableLines().getTableLines().get(0);
        TableFillHelper tableFillHelper = new TableFillHelper(generatedReport, scriptEngine, tableLine, persons);
        wrappedBuffer = tableFillHelper.getWrappedBuffer();
    }

    @Test
    public void width() {
        assertEquals(10, wrappedBuffer.getWidth(0));
        assertEquals(20, wrappedBuffer.getWidth(1));
        assertEquals(10, wrappedBuffer.getWidth(2));
    }

    @Test
    public void isEmpty() {
        assertTrue(wrappedBuffer.isEmpty());
        wrappedBuffer.add(0, "01234567900123456790");
        assertFalse(wrappedBuffer.isEmpty());
        wrappedBuffer.clear();
        assertTrue(wrappedBuffer.isEmpty());
    }

    @Test
    public void add() {
        String result = wrappedBuffer.add(0, "123456789012345");
        assertEquals("1234567890", result);
        assertEquals("12345", wrappedBuffer.getBuffer(0));
    }

    @Test
    public void consume() {
        String result = wrappedBuffer.add(0, "1234567890ABCDEFGHIJ12345");
        assertEquals("1234567890", result);
        result = wrappedBuffer.consume(0);
        assertEquals("ABCDEFGHIJ", result);
        assertEquals("12345", wrappedBuffer.getBuffer(0));
        result = wrappedBuffer.consume(0);
        assertEquals("12345     ", result);
        assertNull(wrappedBuffer.getBuffer(0));
        assertTrue(wrappedBuffer.isEmpty());
    }

    @Test
    public void consumeEmpty() {
        assertEquals("          ", wrappedBuffer.consume(0));
        assertEquals("                    ", wrappedBuffer.consume(1));
        assertEquals("          ", wrappedBuffer.consume(2));
    }

    @Test
    public void flush() {
        wrappedBuffer.add(0, "1234567890ABCDEFGHIJ12345");
        wrappedBuffer.add(2, "123456789012345");
        wrappedBuffer.flush();
        assertEquals(1, generatedReport.getNumberOfPages());
        assertEquals("ABCDEFGHIJ                    12345     ", ((TextLine)generatedReport.getPage(1).getLine(1)).getText());
        assertEquals("12345                                   ", ((TextLine)generatedReport.getPage(1).getLine(2)).getText());
        assertTrue(wrappedBuffer.isEmpty());
    }
}
