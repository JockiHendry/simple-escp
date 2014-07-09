package simple.escp.dom.line;

import simple.escp.dom.Line;
import java.util.Arrays;

/**
 * DOM class to represent list.  A list is something like <code>{@link simple.escp.dom.line.TableLine}</code> but
 * doesn't have columnar layout.
 */
public class ListLine implements Line {

    private String source;
    private String lineSource;
    private TextLine[] header;
    private TextLine[] footer;
    private Integer lineNumber;

    /**
     * Create a new <code>ListLine</code>.
     *
     * @param source a placeholder text to retrieve data source for this list.  It should be evaluated to a
     *               <code>Collection</code> during filling.
     * @param lineSource a placeholder text for every line in this list.  It will be used to translate every
     *                   elements in <code>Collection</code> source into a text.
     * @param header header for this list.  Set to <code>null</code> if this list doesn't have header.
     * @param footer footer for this list.  Set to <code>null</code> if this list doesn't have footer.
     */
    public ListLine(String source, String lineSource, TextLine[] header, TextLine[] footer) {
        this.source = source;
        this.lineSource = lineSource;
        this.header = (header == null) ? new TextLine[0] : header;
        this.footer = (footer == null) ? new TextLine[0] : footer;
    }

    /**
     * Get the placeholder text that represents data source for this list.
     *
     * @return data source for this list.
     */
    public String getSource() {
        return source;
    }

    /**
     * Get the placeholder text that will be used to evaluate the text of every lines in this list.
     *
     * @return placeholder text for line.
     */
    public String getLineSource() {
        return lineSource;
    }

    /**
     * Get the header for this list.
     *
     * @return header for this list.
     */

    public TextLine[] getHeader() {
        return Arrays.copyOf(header, header.length);
    }

    /**
     * Get the footer for this list.
     *
     * @return footer for this list.
     */
    public TextLine[] getFooter() {
        return Arrays.copyOf(footer, footer.length);
    }

    /**
     * Set a line number for this list line.  See also {@link #getLineNumber()}.
     *
     * @param lineNumber the line number starting from <code>1</code>.
     */
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Retrieve line number for this list line.
     *
     * @return line number starting from <code>1</code>.  The first line number is counted from header if it is
     *         exists.  This method will return <code>null</code> if line number hasn't been set previously.
     */
    public Integer getLineNumber() {
        return this.lineNumber;
    }


    @Override
    public boolean isDynamic() {
        return true;
    }

}
