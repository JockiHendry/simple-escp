package simple.escp.json;

import org.junit.Test;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.dom.TableColumn;
import simple.escp.dom.line.ListLine;
import simple.escp.dom.line.TableLine;
import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void parseTable() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": [" +
                "\"This is line 1.\"," +
                "{" +
                    "\"table\": \"sources\"," +
                    "\"columns\":" +
                        "[" +
                            "{ \"source\": \"field1\", \"width\": 10 }," +
                            "{ \"source\": \"field2\", \"width\": 15 }," +
                            "{ \"source\": \"field3\", \"width\": 8 }" +
                        "]" +
                "}," +
                "\"This is line 2.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Report report = jsonTemplate.parse();
        assertEquals(1, report.getNumberOfPages());
        Page page = report.getPage(1);
        assertEquals(3, page.getNumberOfLines());
        assertEquals("This is line 1.", page.getLine(1).toString());
        assertFalse(page.getLine(1).isDynamic());
        assertEquals("This is line 2.", page.getLine(3).toString());
        assertFalse(page.getLine(3).isDynamic());
        assertTrue(page.getLine(2).isDynamic());
        assertTrue(page.getLine(2) instanceof TableLine);

        TableLine tableLine = (TableLine) page.getLine(2);
        assertEquals("sources", tableLine.getSource());
        assertEquals(3, tableLine.getNumberOfColumns());

        TableColumn column = tableLine.getColumnAt(1);
        assertEquals("field1", column.getText());
        assertEquals(10, column.getWidth());
        column = tableLine.getColumnAt(2);
        assertEquals("field2", column.getText());
        assertEquals(15, column.getWidth());
        column = tableLine.getColumnAt(3);
        assertEquals("field3", column.getText());
        assertEquals(8, column.getWidth());
    }

    @Test
    public void parseTableInDetail() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"This is header.\"]," +
                "\"detail\": [{" +
                    "\"table\": \"sources\"," +
                    "\"columns\":" +
                    "[" +
                        "{ \"source\": \"field1\", \"width\": 10 }," +
                        "{ \"source\": \"field2\", \"width\": 15 }," +
                        "{ \"source\": \"field3\", \"width\": 8 }" +
                    "]" +
                "}]," +
                "\"footer\": [\"This is footer.\"]" +
            "}" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Report report = jsonTemplate.parse();
        assertEquals(1, report.getNumberOfPages());
        Page page = report.getPage(1);
        assertEquals(3, page.getNumberOfLines());
        assertEquals("This is header.", page.getLine(1).toString());
        assertFalse(page.getLine(1).isDynamic());
        assertEquals("This is footer.", page.getLine(3).toString());
        assertFalse(page.getLine(3).isDynamic());
        assertTrue(page.getLine(2).isDynamic());
        assertTrue(page.getLine(2) instanceof TableLine);

        TableLine tableLine = (TableLine) page.getLine(2);
        assertEquals("sources", tableLine.getSource());
        assertEquals(3, tableLine.getNumberOfColumns());

        TableColumn column = tableLine.getColumnAt(1);
        assertEquals("field1", column.getText());
        assertEquals("field1", column.getCaption());
        assertEquals(10, column.getWidth());
        column = tableLine.getColumnAt(2);
        assertEquals("field2", column.getText());
        assertEquals("field2", column.getText());
        assertEquals(15, column.getWidth());
        column = tableLine.getColumnAt(3);
        assertEquals("field3", column.getText());
        assertEquals("field3", column.getText());
        assertEquals(8, column.getWidth());
    }

    @Test
    public void parseTableWithColumnCaption() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"This is header.\"]," +
                "\"detail\": [{" +
                    "\"table\": \"sources\"," +
                    "\"columns\":" +
                        "[" +
                            "{ \"source\": \"field1\", \"width\": 10, \"caption\": \"Column A\" }," +
                            "{ \"source\": \"field2\", \"width\": 15, \"caption\": \"Column B\" }," +
                            "{ \"source\": \"field3\", \"width\": 8, \"caption\": \"Column C\" }" +
                        "]" +
                "}]," +
                "\"footer\": [\"This is footer.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Report report = jsonTemplate.parse();
        assertEquals(1, report.getNumberOfPages());
        Page page = report.getPage(1);
        assertEquals(3, page.getNumberOfLines());
        assertEquals("This is header.", page.getLine(1).toString());
        assertFalse(page.getLine(1).isDynamic());
        assertEquals("This is footer.", page.getLine(3).toString());
        assertFalse(page.getLine(3).isDynamic());
        assertTrue(page.getLine(2).isDynamic());
        assertTrue(page.getLine(2) instanceof TableLine);

        TableLine tableLine = (TableLine) page.getLine(2);
        assertEquals("sources", tableLine.getSource());
        assertEquals(3, tableLine.getNumberOfColumns());

        TableColumn column = tableLine.getColumnAt(1);
        assertEquals("field1", column.getText());
        assertEquals("Column A", column.getCaption());
        assertEquals(10, column.getWidth());
        column = tableLine.getColumnAt(2);
        assertEquals("field2", column.getText());
        assertEquals("Column B", column.getCaption());
        assertEquals(15, column.getWidth());
        column = tableLine.getColumnAt(3);
        assertEquals("field3", column.getText());
        assertEquals("Column C", column.getCaption());
        assertEquals(8, column.getWidth());
    }

    @Test
    public void parseListLine() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"This is header.\"]," +
                "\"detail\": [{" +
                    "\"list\": \"sources\"," +
                    "\"line\": \"${field1} + ${field2} = ${field3}\"," +
                    "\"header\":" +
                    "[" +
                        "\"This is list header1\"," +
                        "\"This is list header2\"" +
                    "]," +
                    "\"footer\":" +
                    "[" +
                        "\"This is list footer1\"," +
                        "\"This is list footer2\"" +
                    "]" +
                "}]," +
                "\"footer\": [\"This is footer.\"]" +
            "}" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Report report = jsonTemplate.parse();
        assertEquals(1, report.getNumberOfPages());
        Page page = report.getPage(1);
        assertEquals(3, page.getNumberOfLines());
        assertEquals("This is header.", page.getLine(1).toString());
        assertFalse(page.getLine(1).isDynamic());
        assertEquals("This is footer.", page.getLine(3).toString());
        assertFalse(page.getLine(3).isDynamic());
        assertTrue(page.getLine(2).isDynamic());
        assertTrue(page.getLine(2) instanceof ListLine);

        ListLine listLine = (ListLine) page.getLine(2);
        assertEquals("sources", listLine.getSource());
        assertEquals("${field1} + ${field2} = ${field3}", listLine.getLineSource());
        assertEquals(2, listLine.getHeader().length);
        assertEquals("This is list header1", listLine.getHeader()[0].getText());
        assertEquals("This is list header2", listLine.getHeader()[1].getText());
        assertEquals(2, listLine.getFooter().length);
        assertEquals("This is list footer1", listLine.getFooter()[0].getText());
        assertEquals("This is list footer2", listLine.getFooter()[1].getText());
    }

}
