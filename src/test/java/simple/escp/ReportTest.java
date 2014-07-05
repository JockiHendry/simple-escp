package simple.escp;

import org.junit.Test;
import simple.escp.data.MapDataSource;
import static org.junit.Assert.*;
import static simple.escp.util.EscpUtil.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportTest {

    @Test
    public void appendPage() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);

        List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is detail 1."));
        Page page = report.appendSinglePage(content, false);
        assertEquals(1, report.getPages().size());
        assertEquals(1, page.getPageNumber().intValue());
        assertEquals(3, page.getNumberOfLines());

        page = report.appendSinglePage(content, false);
        assertEquals(2, report.getPages().size());
        assertEquals(2, page.getPageNumber().intValue());
        assertEquals(3, page.getNumberOfLines());

        page = report.appendSinglePage(new ArrayList<Line>(), false);
        assertEquals(3, report.getPages().size());
        assertEquals(3, page.getPageNumber().intValue());
        assertEquals(2, page.getNumberOfLines());

        page = report.appendSinglePage(content, true);
        assertEquals(4, report.getPages().size());
        assertEquals(4, page.getPageNumber().intValue());
        assertEquals(1, page.getNumberOfLines());
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendFullPage() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);

        List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is detail 1."));
        content.add(new TextLine("This is detail 2."));
        content.add(new TextLine("This is detail 3."));
        report.appendSinglePage(content, false);
    }

    @Test
    public void newPage() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);

        Page page = report.newPage(true);
        assertEquals(0, page.getNumberOfLines());
        assertEquals(1, page.getPageNumber().intValue());
        assertEquals(3, page.getPageLength().intValue());
        assertFalse(page.isFull());
        assertEquals(1, report.getPages().size());
        assertEquals(page, report.getCurrentPage());

        page = report.newPage(false);
        assertEquals(2, page.getNumberOfLines());
        assertEquals(2, page.getPageNumber().intValue());
        assertEquals(3, page.getPageLength().intValue());
        assertFalse(page.isFull());
        assertEquals(2, report.getPages().size());
        assertEquals(page, report.getCurrentPage());
    }

    @Test
    public void append() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);

        report.append(new TextLine("This is line 1"), false);
        assertEquals(1, report.getLastPageNumber());
        assertEquals(1, report.getPages().size());
        assertEquals(3, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 2"), false);
        assertEquals(2, report.getLastPageNumber());
        assertEquals(2, report.getPages().size());
        assertEquals(3, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 3"), false);
        assertEquals(3, report.getLastPageNumber());
        assertEquals(3, report.getPages().size());
        assertEquals(3, report.getCurrentPage().getNumberOfLines());
    }

    @Test
    public void appendBasic() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);

        report.append(new TextLine("This is line 1"), true);
        assertEquals(1, report.getLastPageNumber());
        assertEquals(1, report.getPages().size());
        assertEquals(1, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 2"), true);
        assertEquals(1, report.getLastPageNumber());
        assertEquals(1, report.getPages().size());
        assertEquals(2, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 3"), true);
        assertEquals(1, report.getLastPageNumber());
        assertEquals(1, report.getPages().size());
        assertEquals(3, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 4"), true);
        assertEquals(2, report.getLastPageNumber());
        assertEquals(2, report.getPages().size());
        assertEquals(1, report.getCurrentPage().getNumberOfLines());
    }

    @Test
    public void fill() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("Name: ${name}"), false);
        report.append(new TextLine("Result: ${score}"), false);

        Map<String, Object> mapSource = new HashMap<>();
        mapSource.put("name", "Solid Snake");
        mapSource.put("score", 80);

        assertEquals(escInitalize() + escPageLength(3) +
            "This is header." + CRLF +
            "Name: Solid Snake" + CRLF +
            "This is footer." + CRLF + CRFF +
            "This is header." + CRLF +
            "Result: 80" + CRLF +
            "This is footer." + CRLF + CRFF + escInitalize(),
            new FillJob(report, new MapDataSource(mapSource)).fill()
        );
    }

    @Test
    public void nextPage() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is in page 1."), false);
        report.lineBreak();
        report.append(new TextLine("This is in page 2."), false);
        report.lineBreak();
        report.append(new TextLine("This is in page 3."), false);

        assertEquals(3, report.getLastPageNumber());
        Page page1 = report.page(1);
        Page page2 = report.page(2);
        Page page3 = report.page(3);
        assertEquals(page2, report.nextPage(page1));
        assertEquals(page3, report.nextPage(page2));
    }

    @Test
    public void previousPage() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is in page 1."), false);
        report.lineBreak();
        report.append(new TextLine("This is in page 2."), false);
        report.lineBreak();
        report.append(new TextLine("This is in page 3."), false);

        assertEquals(3, report.getLastPageNumber());
        Page page1 = report.page(1);
        Page page2 = report.page(2);
        Page page3 = report.page(3);
        assertEquals(page1, report.previousPage(page2));
        assertEquals(page2, report.previousPage(page3));
    }

    @Test
    public void insert() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(5);
        pageFormat.setUsePrinterPageLength(false);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);
        report.append(new TextLine("This is line 2 in page 1"), false);
        report.lineBreak();
        report.append(new TextLine("This is line 1 in page 2"), false);
        report.append(new TextLine("This is line 2 in page 2"), false);
        report.append(new TextLine("This is line 3 in page 2"), false);

        report.insert(new TextLine("This is inserted line."), 1, 4);

        assertEquals(2, report.getPages().size());
        Page page1 = report.getPages().get(0);
        assertEquals(5, page1.getNumberOfLines());
        assertEquals("This is header.", page1.get(1).toString());
        assertEquals("This is line 1 in page 1", page1.get(2).toString());
        assertEquals("This is line 2 in page 1", page1.get(3).toString());
        assertEquals("This is inserted line.", page1.get(4).toString());
        assertEquals("This is footer.", page1.get(5).toString());
        Page page2 = report.getPages().get(1);
        assertEquals(5, page2.getNumberOfLines());
        assertEquals("This is header.", page2.get(1).toString());
        assertEquals("This is line 1 in page 2", page2.get(2).toString());
        assertEquals("This is line 2 in page 2", page2.get(3).toString());
        assertEquals("This is line 3 in page 2", page2.get(4).toString());
        assertEquals("This is footer.", page2.get(5).toString());
    }

    @Test
    public void insertLast() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(5);
        pageFormat.setUsePrinterPageLength(false);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);
        report.append(new TextLine("This is line 2 in page 1"), false);
        report.append(new TextLine("This is line 3 in page 1"), false);
        report.append(new TextLine("This is line 1 in page 2"), false);
        report.append(new TextLine("This is line 2 in page 2"), false);

        report.insert(new TextLine("This is inserted line."), 2, 4);

        assertEquals(2, report.getPages().size());
        Page page1 = report.getPages().get(0);
        assertEquals(5, page1.getNumberOfLines());
        assertEquals("This is header.", page1.get(1).toString());
        assertEquals("This is line 1 in page 1", page1.get(2).toString());
        assertEquals("This is line 2 in page 1", page1.get(3).toString());
        assertEquals("This is line 3 in page 1", page1.get(4).toString());
        assertEquals("This is footer.", page1.get(5).toString());
        Page page2 = report.getPages().get(1);
        assertEquals(5, page2.getNumberOfLines());
        assertEquals("This is header.", page2.get(1).toString());
        assertEquals("This is line 1 in page 2", page2.get(2).toString());
        assertEquals("This is line 2 in page 2", page2.get(3).toString());
        assertEquals("This is inserted line.", page2.get(4).toString());
        assertEquals("This is footer.", page2.get(5).toString());
    }

    @Test
    public void insertOverflow() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(5);
        pageFormat.setUsePrinterPageLength(false);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);
        report.append(new TextLine("This is line 2 in page 1"), false);
        report.append(new TextLine("This is line 3 in page 1"), false);
        report.append(new TextLine("This is line 1 in page 2"), false);
        report.append(new TextLine("This is line 2 in page 2"), false);
        report.append(new TextLine("This is line 3 in page 2"), false);

        report.insert(new TextLine("This is inserted line."), 1, 3);

        assertEquals(3, report.getPages().size());
        Page page1 = report.getPages().get(0);
        assertEquals(5, page1.getNumberOfLines());
        assertEquals("This is header.", page1.get(1).toString());
        assertEquals("This is line 1 in page 1", page1.get(2).toString());
        assertEquals("This is inserted line.", page1.get(3).toString());
        assertEquals("This is line 2 in page 1", page1.get(4).toString());
        assertEquals("This is footer.", page1.get(5).toString());
        Page page2 = report.getPages().get(1);
        assertEquals(5, page2.getNumberOfLines());
        assertEquals("This is header.", page2.get(1).toString());
        assertEquals("This is line 3 in page 1", page2.get(2).toString());
        assertEquals("This is line 1 in page 2", page2.get(3).toString());
        assertEquals("This is line 2 in page 2", page2.get(4).toString());
        assertEquals("This is footer.", page2.get(5).toString());
        Page page3 = report.getPages().get(2);
        assertEquals(3, page3.getNumberOfLines());
        assertEquals("This is header.", page3.get(1).toString());
        assertEquals("This is line 3 in page 2", page3.get(2).toString());
        assertEquals("This is footer.", page3.get(3).toString());
    }

    @Test
    public void insertCausesNewPage() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);

        report.insert(new TextLine("This is inserted line."), 1, 2);

        assertEquals(2, report.getPages().size());
        Page page1 = report.getPages().get(0);
        assertEquals(3, page1.getNumberOfLines());
        assertEquals("This is header.", page1.get(1).toString());
        assertEquals("This is inserted line.", page1.get(2).toString());
        assertEquals("This is footer.", page1.get(3).toString());
        Page page2 = report.getPages().get(1);
        assertEquals(3, page2.getNumberOfLines());
        assertEquals("This is header.", page2.get(1).toString());
        assertEquals("This is line 1 in page 1", page2.get(2).toString());
        assertEquals("This is footer.", page2.get(3).toString());
    }

    @Test
    public void dynamicLine() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);
        assertFalse(report.hasDynamicLine());
        report.append(new TableLine("test"), true);
        assertTrue(report.hasDynamicLine());
    }

}
