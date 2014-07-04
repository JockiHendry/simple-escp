package simple.escp;

import org.junit.Test;
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
            report.fill(mapSource, null)
        );
    }

}
