package simple.escp;

/**
 * This class represents a column in {@link simple.escp.TableLine}.
 */
public class TableColumn {

    private String text;
    private int width;

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

}
