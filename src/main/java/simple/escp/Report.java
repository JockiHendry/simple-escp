package simple.escp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * DOM class to represent a print result in form of collection of empty, one or more <code>Page</code>.
 * <code>Report</code> is usually the output of parsing stage.  To initiate filling stage,
 * call <code>fill()</code> method of this class.
 */
public class Report implements Iterable<Page> {

    private List<Page> pages = new ArrayList<>();
    private int lastPageNumber = 0;
    private PageFormat pageFormat;
    private Page currentPage;
    private TextLine[] header;
    private TextLine[] footer;
    private boolean lineBreak;

    /**
     * Create a clone from another report.
     *
     * @param report a <code>Report</code> to clone.
     */
    public Report(Report report) {
        this.pageFormat = report.getPageFormat();
        this.header = report.getHeader();
        this.footer = report.getFooter();
        this.lineBreak = false;

        for (Page page : report) {
            for (Line line : page.getContent()) {
                append(line, false);
            }
        }
    }

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
     * Get the <code>PageFormat</code> for this report.
     *
     * @return <code>PageFormat</code> for this report.
     */
    public PageFormat getPageFormat() {
        return pageFormat;
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
     * Get a <code>Page</code> based on its <code>pageNumber</code>.
     *
     * @param pageNumber the page number for the page that will be retrieved.
     * @return a <code>Page</code> or throws <code>IllegalArgumentException</code> if <code>pageNumber</code>
     *         is not valid.
     */
    public Page getPage(int pageNumber) {
        if (pageNumber < 1 || pageNumber > getLastPageNumber()) {
            throw new IllegalArgumentException("Invalid page: " + pageNumber);
        }
        return pages.get(pageNumber - 1);
    }

    /**
     * Get the first <code>Page</code> in this report that has contains <code>TableLine</code>.
     *
     * @return a <code>Page</code> that has at least one <code>TableLine</code>, or <code>null</code> if no page
     *         in this report has <code>TableLine</code>.
     */
    public Page getFirstPageWithTableLines() {
        for (Page page : pages) {
            if (!page.getTableLines().isEmpty()) {
                return page;
            }
        }
        return null;
    }

    /**
     * Get number of pages in this report.
     *
     * @return number of pages in this report.
     */
    public int getNumberOfPages() {
        return pages.size();
    }

    /**
     * Get next page of the specified page.
     *
     * @param  page find the next page of this <code>page</code>.
     * @return a <code>Page</code> or <code>null</code> if <code>page</code> is the last page.
     */
    public Page nextPage(Page page) {
        if (page.getPageNumber() == pages.size()) {
            return null;
        }
        // Note that page is 1-based while the list is 0-based.
        // page + 1 to list index is: page + 1 - 1 = page + 0.
        return pages.get(page.getPageNumber());
    }

    /**
     * Get previous page of the specified page.
     *
     * @param  page find the previous page of this <code>page</code>.
     * @return a <code>Page</code> or <code>null</code> if <code>page</code> is the first page.
     */
    public Page previousPage(Page page) {
        if (page.getPageNumber() == 1) {
            return null;
        }
        // Note that page is 1-based while the list is 0-based.
        // page - 1 to list index is: page - 1 - 1 = page - 2.
        return pages.get(page.getPageNumber() - 2);
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
     * Insert a new line at certain page and certain position.  This may causes a new page to be created if necessary.
     *
     * @param line a new line to be inserted to this report.
     * @param pageNumber the page number in which the new line will be inserted.
     * @param lineNumber the line number in the page where the new line will be inserted.
     * @param newPageFirstLines these lines will be added to the new page if this insertion creates new page.
     */
    public void insert(Line line, int pageNumber, int lineNumber, List<? extends Line> newPageFirstLines) {
        if (pageNumber < 1 || pageNumber > pages.size()) {
            throw new IllegalArgumentException("Invalid page number: " + pageNumber);
        }
        Page startPage = pages.get(pageNumber - 1);
        Line discardedLine = startPage.insert(line, lineNumber);
        Page currentPage = nextPage(startPage);
        while (discardedLine != null) {
            if (currentPage == null) {
                currentPage = newPage(false);
                if (newPageFirstLines != null) {
                    currentPage.append(newPageFirstLines);
                }
            }
            discardedLine = currentPage.insert(discardedLine, header.length + 1  +
                (newPageFirstLines == null ? 0 : newPageFirstLines.size()));
            currentPage = nextPage(currentPage);
        }
    }

    /**
     * Insert a new line at certain page and certain position.  This may causes a new page to be created if necessary.
     *
     * @param line a new line to be inserted to this report.
     * @param pageNumber the page number in which the new line will be inserted.
     * @param lineNumber the line number in the page where the new line will be inserted.
     */
    public void insert(Line line, int pageNumber, int lineNumber) {
        insert(line, pageNumber, lineNumber, null);
    }

    /**
     * Determine if one or more pages in this report has one or more dynamic lines.
     *
     * @return <code>true</code> if this report contains dynamic line or <code>false</code> if otherwise.
     */
    public boolean hasDynamicLine() {
        for (Page page : pages) {
            if (page.hasDynamicLine()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Page> iterator() {
        return pages.iterator();
    }
}
