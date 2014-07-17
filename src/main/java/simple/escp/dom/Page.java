package simple.escp.dom;

import simple.escp.dom.line.EmptyLine;
import simple.escp.dom.line.ListLine;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;
import simple.escp.util.EscpUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * DOM class to represent one page.  A <code>Page</code> may contains header and footer.
 * A page also has its page number.
 */
public class Page {

    private static final Logger LOG = Logger.getLogger("simple.escp");

    private TextLine[] header;
    private TextLine[] footer;
    private List<Line> content;
    private Integer pageNumber;
    private Integer pageLength;

    /**
     * Create a clone from another Page.
     *
     * @param anotherPage a <code>Page</code> to clone.
     * @param pageLength maximum number of lines for this page.  Set <code>null</code> for unlimited lines in this
     *                   page.
     */
    public Page(Page anotherPage, Integer pageLength) {
        content = new ArrayList<>();
        for (Line line : anotherPage.content) {
            content.add(line);
        }
        header = Arrays.copyOf(anotherPage.getHeader(), anotherPage.getHeader().length);
        footer = Arrays.copyOf(anotherPage.getFooter(), anotherPage.getFooter().length);
        pageNumber = anotherPage.getPageNumber();
        this.pageLength = pageLength;
    }

    /**
     * Create a new <code>Page</code>.
     *
     * @param content the content of this <code>Page</code>.
     * @param header the header for this <code>Page</code>.  Set <code>null</code> if this page doesn't have
     *               header.
     * @param footer the footer for this <code>Page</code>.  Set <code>null</code> if this page doesn't have
     *               footer.
     * @param pageNumber the page number for this page.  The page number for first page is 1, the second page is 2,
     *                   and so on.
     * @param pageLength maximum number of lines for this page.  Set <code>null</code> for unlimited lines in this
     *                   page.
     */
    public Page(List<Line> content, TextLine[] header, TextLine[] footer, Integer pageNumber, Integer pageLength) {
        this.content = content;
        this.header = header == null ? new TextLine[0] : header;
        this.footer = footer == null ? new TextLine[0] : footer;
        this.pageNumber = pageNumber;
        this.pageLength = pageLength;
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
     * Get the content of this page.
     *
     * @return content of this page.
     */
    public List<Line> getContent() {
        return content;
    }

    /**
     * Set the new content for this page.
     *
     * @param content the new content for this page.
     */
    public void setContent(List<Line> content) {
        if (pageLength != null) {
            int numberOfLines = header.length + footer.length;
            if (numberOfLines + content.size() > pageLength) {
                throw new IllegalArgumentException("Page overflow.");
            }
        }
        this.content = content;
    }

    /**
     * Get the page number for this page.
     *
     * @return page number for this page.
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * Set a new page number for this page.
     *
     * @param pageNumber new page number for this page.
     */
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Get maximum number of lines for this page.
     *
     * @return maximum number of lines for this page.
     */
    public Integer getPageLength() {
        return pageLength;
    }

    /**
     * Check if this page is full and no new line can be written anymore.
     *
     * @return <code>true</code> if this page is full and no write operation is supported, or <code>false</code> if
     *         this page is not full.
     */
    public boolean isFull() {
        if (pageLength == null) {
            return false;
        } else {
            return (header.length + footer.length + content.size()) >= pageLength;
        }
    }

    /**
     * Check if this page is overflow.  The difference with this method and {@link #isFull()} is that this method
     * will return <code>true</code> only if number of lines is more than allowed length.
     *
     * @return <code>true</code> if this page is overflowed or <code>false</code> if otherwise.
     */
    public boolean isOverflow() {
        if (pageLength == null) {
            return false;
        } else {
            return (header.length + footer.length + content.size()) > pageLength;
        }
    }

    /**
     * Add a new line to this page from a string.  The string will be converted to a line.
     * See also {@link #append(Line)}.
     *
     * @param text the string that will be added to this page.
     */
    public void append(String text) {
        if (isFull()) {
            throw new IllegalStateException("Page is full.");
        }
        content.add(new TextLine(text));
    }

    /**
     * Add a new line to this page.  The line will be inserted after the last line of this page.
     *
     * @param line the line that will be added to this page.
     */
    public void append(Line line) {
        if (isFull()) {
            throw new IllegalStateException("Page is full.");
        }
        content.add(line);
    }

    /**
     * Add multipe lines to this page.  The lines will be inserted after the last line of this page.
     *
     * @param lines the lines that will be added to this page.
     */
    public void append(List<? extends Line> lines) {
        for (Line line : lines) {
            append(line);
        }
    }

    /**
     * Add empty line to this page.
     */
    public void appendEmptyLine() {
        append(new EmptyLine());
    }

    /**
     * Add empty lines from current line to the specified line number.
     *
     * @param lineNumber destination line number.  This line is exclusive (the lines before this line will be
     *                   empty line but not including this line).
     */
    public void appendEmptyLineUntil(int lineNumber) {
        if (lineNumber <= header.length) {
            throw new IllegalArgumentException("Can't append empty line before header: " + lineNumber);
        }
        if (lineNumber > (pageLength - footer.length)) {
            throw new IllegalArgumentException("Can't append empty line after footer: " + lineNumber);
        }
        int currentLine = header.length + content.size();
        if (currentLine >= lineNumber) {
            throw new IllegalArgumentException("Destination line number is less than current (" + currentLine + ")");
        }
        for (int i = currentLine + 1; i < lineNumber; i++) {
            appendEmptyLine();
        }
    }

    /**
     * Insert a new <code>Line</code> at the specified <code>lineNumber</code> position.  If the page is full
     * after insertion, the last line of the content (<strong>not</strong> including footer) will be removed
     * and returned.
     *
     * @param line the <code>Line</code> that will be inserted.
     * @param lineNumber the line number position in which the new line will be inserted.
     * @return discarded <code>Line</code> if insertion causes overflow and a line is removed, or
     *         <code>null</code> if no line is discarded.
     */
    public Line insert(Line line, int lineNumber) {
        Line result = null;
        if (lineNumber < header.length) {
            throw new IllegalArgumentException("Line number can't be inserted before header: " + lineNumber);
        }
        if ((pageLength != null) && (lineNumber > pageLength)) {
            throw new IllegalArgumentException("Invalid line number: " + lineNumber);
        }
        content.add(lineNumber - header.length - 1, line);
        if (isOverflow()) {
            result = content.get(content.size() - 1);
            LOG.fine("Content overflow and the last line will be removed [" + result + "]");
            content.remove(content.size() - 1);
        }
        return result;
    }

    /**
     * Change the content of a line.  Line number for the first line (starting from header) is <code>1</code>.
     *
     * @param lineNumber the line number position in which the new line will be replaced.
     * @param line a new line to replace old line.
     */
    public void setLine(int lineNumber, Line line) {
        if (lineNumber < 1 || lineNumber > getNumberOfLines()) {
            throw new IllegalArgumentException("Invalid line number: " + lineNumber);
        }
        if (lineNumber <= header.length) {
            header[lineNumber - 1] = (TextLine) line;
        } else if (lineNumber > header.length + content.size()) {
            footer[lineNumber - header.length - content.size() - 1] = (TextLine) line;
        } else {
            content.set(lineNumber - header.length - 1, line);
        }
    }

    /**
     * Get current number of lines in this page that have been written.
     *
     * @return number of lines of this page (excluding empty lines).
     */
    public int getNumberOfLines() {
        return header.length + content.size() + footer.length;
    }

    /**
     * Get all lines in this page.
     *
     * @return all lines of this page (excluding empty lines).
     */
    public Line[] getLines() {
        Line[] result = new Line[getNumberOfLines()];
        int index = 0;
        for (TextLine line : header) {
            line.setLineNumber(index + 1);
            result[index++] = line;
        }
        for (Line line : content) {
            line.setLineNumber(index + 1);
            result[index++] = line;
        }
        for (Line line : footer) {
            line.setLineNumber(index + 1);
            result[index++] = line;
        }
        return result;
    }

    /**
     * Get the text of certain line number in this page.
     *
     * @param lineNumber a line number starting from 1.
     * @return the text for the specified line number.
     */
    public Line getLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber > getNumberOfLines()) {
            throw new IllegalArgumentException("Number of lines [" + lineNumber + "] is out of range.");
        }
        if (lineNumber <= header.length) {
            return header[lineNumber - 1];
        }
        lineNumber = lineNumber - header.length;
        if (lineNumber <= content.size()) {
            return content.get(lineNumber - 1);
        }
        lineNumber = lineNumber - content.size();
        return footer[lineNumber - 1];
    }

    /**
     * Remove a <code>Line</code> from this <code>Page</code>.
     *
     * @param line a <code>Line</code> that will be removed.
     * @return <code>true</code> if this page contained the specified line
     */
    public boolean removeLine(Line line) {
        return content.remove(line);
    }

    /**
     * Remove a <code>Line</code> by its line number.  This method will only delete line located at detail (content).
     * Header and footer can't be deleted by using this method.
     *
     * @param lineNumber a line number starting from 1 starting from header (if exists).  The line number
     *                   <strong>should not</strong> be part of header or footer.
     * @return the deleted <code>Line</code> or <code>null</code> if nothing is deleted.
     */
    public Line removeLine(int lineNumber) {
        if ((lineNumber <= header.length) || (lineNumber > header.length + content.size())) {
            throw new IllegalArgumentException("Line number is not valid: [" + lineNumber + "]");
        }
        int index = lineNumber - header.length - 1;
        return content.remove(index);
    }

    /**
     * Convert this page into ESC/P string that can be printed.  This is <strong>>not</strong> including filling
     * operation to substitute placeholder with values from data source or evaluating functions.
     *
     * @param autoLinefeed set <code>true</code> if auto line-feed is enabled (CR will be used as line separator) or
     *                     set <code>false</code> if auto line-feed is disabled (CRLR will be used as line separator).
     * @param autoFormfeed set <code>true</code> if auto form-feed is enabled (CRFF will be added automatically at
     *                     the end of page) or set <code>false</code> to not add CRFF automatically at the end of page.
     * @return conversion result that may contains ESC/P string.
     */
    public String convertToString(boolean autoLinefeed, boolean autoFormfeed) {
        StringBuffer result = new StringBuffer();
        for (Line line: getLines()) {
            if (line instanceof TextLine) {
                result.append(((TextLine) line).getText());
                result.append(autoLinefeed ? EscpUtil.CR : EscpUtil.CRLF);
            }
        }
        if (autoFormfeed) {
            result.append(EscpUtil.CRFF);
        }
        return result.toString();
    }

    /**
     * Check if this page contains one or more dynamic lines.
     *
     * @return <code>true</code> if this page contains dynamic line or <code>false</code> if otherwise.
     */
    public boolean hasDynamicLine() {
        for (Line line: content) {
            if (line.isDynamic()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all <code>TableLine</code> in this page.  This method also stores line number for each returned
     * <code>TableLine</code>.  To inspect line number for <code>TableLine</code>,
     * use <code>TableLine.getLineNumber()</code> method.
     *
     * @return <code>List</code> that contains <code>TableLine</code> in this page.  If no <code>TableLine</code>
     *         exists in this page, it will return an empty <code>List</code>.
     */
    public List<TableLine> getTableLines() {
        List<TableLine> result = new ArrayList<>();
        int offset = header.length + 1;
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i) instanceof TableLine) {
                TableLine tableLine = (TableLine) content.get(i);
                tableLine.setLineNumber(offset + i);
                result.add(tableLine);
            }
        }
        return result;
    }

    /**
     * Get all <code>ListLine</code> in this page.  This method also stores line number for each returned
     * <code>ListLine</code>.  To inspect line number for <code>ListLine</code>,
     * use <code>ListLine.getLineNumber()</code> method.
     *
     * @return <code>List</code> that contains <code>TableLine</code> in this page.  If no <code>TableLine</code>
     *         exists in this page, it will return an empty <code>List</code>.
     */
    public List<ListLine> getListLines() {
        List<ListLine> result = new ArrayList<>();
        int offset = header.length + 1;
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i) instanceof ListLine) {
                ListLine listLine = (ListLine) content.get(i);
                listLine.setLineNumber(offset + i);
                result.add(listLine);
            }
        }
        return result;
    }

}
