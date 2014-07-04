package simple.escp;

import simple.escp.util.EscpUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOM class to represent a print result in form of collection of empty, one or more <code>Page</code>.
 * <code>Report</code> is usually the output of parsing stage.  To initiate filling stage,
 * call <code>fill()</code> method of this class.
 */
public class Report {

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9_]+)\\}");
    public static final Pattern FUNCTION_PATTERN = Pattern.compile("%\\{([a-zA-Z0-9_]+)\\}");

    private List<Page> pages = new ArrayList<>();
    private int lastPageNumber = 0;
    private PageFormat pageFormat;
    private Page currentPage;
    private TextLine[] header;
    private TextLine[] footer;
    private boolean lineBreak;
    protected Map<String, Placeholder> placeholders = new HashMap<>();

    /**
     * Create a new instance of <code>Report</code>.
     *
     * @param pageFormat the <code>PageFormat</code> for this <code>Report</code>.
     * @param header header for all of pages in this <code>Report</code>.
     * @param footer footer for all of pages in this <code>Report</code>.
     */
    public Report(PageFormat pageFormat, TextLine[] header, TextLine[] footer) {
        this.pageFormat = pageFormat;
        if ((pageFormat.getPageLength() == null) && (!pageFormat.isUsePageLengthFromPrinter())) {
            throw new IllegalArgumentException("Invalid page format with pageLength undefined when " +
                "isUsePageLengthFromPrinter is false.");
        }
        this.header = (header == null) ? new TextLine[0] : header;
        this.footer = (footer == null) ? new TextLine[0] : footer;
        this.lineBreak = false;
    }

    /**
     * Get the header for this page.
     *
     * @return header for this page.
     */
    public TextLine[] getHeader() {
        return Arrays.copyOf(header, header.length);
    }

    /**
     * Get the footer for this page.
     *
     * @return footer for this page.
     */
    public TextLine[] getFooter() {
        return Arrays.copyOf(footer, footer.length);
    }

    /**
     * Get all of <code>Page</code> in this report.
     *
     * @return list of available <code>Page</code> in this report.
     */
    public List<Page> getPages() {
        return pages;
    }

    /**
     * Get current page number for this report.
     *
     * @return current page number in this report starting from 1.
     */
    public int getLastPageNumber() {
        return lastPageNumber;
    }

    /**
     * Get current page in this report.
     *
     * @return the latest <code>Page</code> for this report.
     */
    public Page getCurrentPage() {
        return currentPage;
    }

    /**
     * Create a new page for this report.
     *
     * @param plain if <code>true</code> will ignore header and footer.  Set this to <code>false</code> to create
     *              a <code>Page</code> that doesn't have header and footer.
     * @return the created <code>Page</code>.
     */
    public Page newPage(boolean plain) {
        lineBreak = false;
        lastPageNumber++;
        Page page;
        if (plain) {
            page = new Page(new ArrayList<Line>(), null, null, lastPageNumber, pageFormat.getPageLength());
        } else {
            page = new Page(new ArrayList<Line>(), header, footer, lastPageNumber, pageFormat.getPageLength());
        }
        pages.add(page);
        currentPage = page;
        return page;
    }

    /**
     * Start a line break.  The next call of <code>append()</code> will create a new page and write to this new
     * page instead of current page.
     */
    public void lineBreak() {
        this.lineBreak = true;
    }

    /**
     * Add a new single page to the last page of this report.
     *
     * @param content the content of the page.
     * @param plain set <code>true</code> to include header and footer, or <code>false</code> if otherwise.
     * @return the created <code>Page</code>.
     */
    public Page appendSinglePage(List<Line> content, boolean plain) {
        Page page = newPage(plain);
        page.setContent(content);
        return page;
    }

    /**
     * Add a new single page to the last page of this report.
     *
     * @param content the content of the page.
     * @param plain set <code>true</code> to include header and footer, or <code>false</code> if otherwise.
     * @return the created <code>Page</code>.
     */

    public Page appendSinglePage(Line[] content, boolean plain) {
        List<Line> contentInList = new ArrayList<>(content.length);
        for (Line s : content) {
            contentInList.add(s);
        }
        return appendSinglePage(contentInList, plain);
    }

    /**
     * Add a new line to this report.  If current page is full, this method will create a new page and write
     * to the new page.
     *
     * @param line a new line to be inserted to the last page of this report.
     * @param plain set <code>true</code> to include header and footer, or <code>false</code> if otherwise.
     */
    public void append(Line line, boolean plain) {
        if (lineBreak || (currentPage == null) || currentPage.isFull()) {
            newPage(plain);
        }
        currentPage.append(line);
    }

    /**
     * This method will fill placeholders with value from both supplied <code>Map</code> and Java Bean object.
     *
     * @param text the source text that has placeholders.
     * @param mapSource value for placeholder in form of <code>Map</code>.  Set <code>null</code> if no value
     *                  is in <code>Map</code>.
     * @param objectSource value for placeholder in form of Java Bean object.  Set <code>null</code> if no value
     *                     is in a Java Bean object.
     * @return source with placeholders replaced by actual value.
     */
    private String fillPlaceholder(String text, Map mapSource, Object objectSource) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String placeholderText = matcher.group(1);
            Placeholder placeholder = placeholders.get(placeholderText);
            if (placeholder == null) {
                placeholder = new Placeholder(placeholderText, mapSource, objectSource);
                placeholders.put(placeholderText, placeholder);
            }
            matcher.appendReplacement(result, placeholder.value());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * This method will evaluate functions.
     *
     * @param text the source text that has functions.
     * @param page the associated <code>Page</code> for source text.
     * @return source with functions replaced by evaluated value.
     */
    private String fillFunction(String text, Page page) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = FUNCTION_PATTERN.matcher(text);
        while (matcher.find()) {
            String function = matcher.group(1);

            // PAGE_NO
            if ("PAGE_NO".equals(function)) {
                matcher.appendReplacement(result, String.valueOf(page.getPageNumber()));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Perform the filling stage of this report.  This method will fill placeholders with value from both
     * supplied <code>Map</code> and Java Bean object.
     *
     * @param mapSource <code>Map</code> as data source.  Set <code>null</code> if no data source is defined in
     *                  <code>Map</code>.
     * @param objectSource a Java Bean object as data source.  Set <code>null</code> if no data source is defined as
     *                     Java Bean object.
     * @return a string with ESC/P commands that can be printed to printer.
     */
    public String fill(Map mapSource, Object objectSource) {
        StringBuffer result = new StringBuffer();
        result.append(pageFormat.build());
        for (Page page : pages) {
            String pageText = page.convertToString(pageFormat.isAutoLineFeed(), pageFormat.isAutoFormFeed());
            pageText = fillPlaceholder(pageText, mapSource, objectSource);
            pageText = fillFunction(pageText, page);
            result.append(pageText);
        }
        if (pageFormat.isAutoFormFeed() && !result.toString().endsWith(EscpUtil.CRFF)) {
            result.append(EscpUtil.CRFF);
        }
        result.append(EscpUtil.escInitalize());
        return result.toString();
    }

}
