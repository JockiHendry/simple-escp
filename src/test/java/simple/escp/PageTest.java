package simple.escp;

import static org.junit.Assert.*;
import static simple.escp.util.EscpUtil.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class PageTest {

    @Test
    public void append() {
        List<Line> content = new ArrayList<>();
        Page page = new Page(content, null, null, 1, 3);
        page.append("This is line 1");
        assertEquals(1, page.getContent().size());
        assertEquals("This is line 1", page.getContent().get(0).toString());
        assertEquals("This is line 1", page.get(1).toString());
        page.append("This is line 2");
        assertEquals(2, page.getContent().size());
        assertEquals("This is line 2", page.getContent().get(1).toString());
        assertEquals("This is line 2", page.get(2).toString());
        page.append("This is line 3");
        assertEquals(3, page.getContent().size());
        assertEquals("This is line 3", page.getContent().get(2).toString());
        assertEquals("This is line 3", page.get(3).toString());

    }

    @Test(expected = IllegalStateException.class)
    public void appendFull() {
        List<Line> content = new ArrayList<>();
        Page page = new Page(content, null, null, 1, 3);
        page.append("This is line 1");
        page.append("This is line 2");
        page.append("This is line 3");
        page.append("This is line 4");
    }

    @Test(expected = IllegalStateException.class)
    public void appendFullWithHeaderAndFooter() {
        List<Line> content = new ArrayList<>();
        TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        Page page = new Page(content, header, footer, 1, 3);
        page.append("This is line 1");
        page.append("This is line 2");
    }

    @Test
    public void getWithHeaderAndFooter() {
        List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        Page page = new Page(content, header, footer, 1, 3);
        assertEquals("This is header 1", page.get(1).toString());
        assertEquals("This is content", page.get(2).toString());
        assertEquals("This is footer 1", page.get(3).toString());
    }


    @Test
    public void getNumberOfLines() {
        // With content only
        List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        Page page = new Page(content, null, null, 1, 3);
        assertEquals(1, page.getNumberOfLines());

        // With header and footer
        content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        page = new Page(content, header, footer, 1, 3);
        assertEquals(3, page.getNumberOfLines());
    }

    @Test
    public void getLines() {
        // With content only
        List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        Page page = new Page(content, null, null, 1, 3);
        assertEquals(1, page.getLines().length);
        assertEquals("This is content", page.getLines()[0].toString());

        // With header and footer
        content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        page = new Page(content, header, footer, 1, 3);
        assertEquals(3, page.getLines().length);
        assertEquals("This is header 1", page.getLines()[0].toString());
        assertEquals("This is content", page.getLines()[1].toString());
        assertEquals("This is footer 1", page.getLines()[2].toString());
    }

    @Test
    public void convertToString() {
        List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        Page page = new Page(content, header, footer, 1, 3);
        assertEquals("This is header 1" + CRLF + "This is content" + CRLF + "This is footer 1" + CRLF + CRFF,
            page.convertToString(false, true));
        assertEquals("This is header 1" + CR + "This is content" + CR + "This is footer 1" + CR + CRFF,
            page.convertToString(true, true));
    }

}
