package simple.escp.dom;

/**
 * This class represents a column in {@link simple.escp.dom.line.TableLine}.
 */
public class TableColumn {

    private String text;
    private String caption;
    private int width;
    private boolean wrap;

    /**
     * Construct a new <code>TableColumn</code>.
     *
     * @param text a text that will be printed for this column.  This text may contains placeholder.
     * @param width number of characters for this column.
     */
    public TableColumn(String text, int width) {
        this.text = text;
        this.width = width;
    }

    /**
     * Get the text for this column.
     *
     * @return text for this column, may contains placeholder.
     */
    public String getText() {
        return text;
    }

    /**
     * Set a new text for this column.
     *
     * @param text a new text for this column, may contains placeholder.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get the width in number of characters for this column.
     *
     * @return column's width in number of characters.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set a new width for this column.
     *
     * @param width a new width in number of characters.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get the caption for this column.
     *
     * @return the caption of this column.  If no caption is defined in this column, this method will return
     *         column's text instead.
     */
    public String getCaption() {
        return caption != null ? caption : text;
    }

    /**
     * Set the caption for this column.
     *
     * @param caption new caption for this column.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Determine if the content of this column should be wrapped or truncated when the size of content is
     * more than column's width.
     *
     * @return <code>true</code> if content should be wrapped or <code>false</code> if content should be truncated.
     */
    public boolean isWrap() {
        return wrap;
    }

    /**
     * Enable or disable wrapping.
     *
     * @param wrap if <code>true</code> will wrap the overflowed content (move it to the next row).  If
     *             <code>wrap</code> is <code>false</code>, the overflowed content will be truncated.
     */
    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }
}
