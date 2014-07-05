package simple.escp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DOM class to represent table.  A table consists of one or more {@link simple.escp.TableColumn}.
 */
public class TableLine implements Line, Iterable<TableColumn> {

    private List<TableColumn> columns = new ArrayList<>();
    private String source;
    private Integer lineNumber;

    /**
     * Create a new <code>TableLine</code>.
     *
     * @param source a placeholder text to retrieve data source for this table.  It should be evaluated to a
     *               <code>Collection</code> during filling.
     */
    public TableLine(String source) {
        this.source = source;
    }

    /**
     * Get the placeholder text that represents data source for this table.
     *
     * @return data source for this table.
     */
    public String getSource() {
        return source;
    }

    /**
     * Add a new <code>TableColumn</code> to this table.
     *
     * @param column a new <code>TableColumn</code> that will be appended at the right-most position.
     * @return the new <code>TableColumn</code>.
     */
    public TableColumn addColumn(TableColumn column) {
        columns.add(column);
        return column;
    }

    /**
     * Add a new <code>TableColumn</code> to this table.
     *
     * @param text the text, may contains placeholder, for this column.
     * @param width width of this column in number of characters.
     * @return the new <code>TableColumn</code>.
     */
    public TableColumn addColumn(String text, int width) {
        TableColumn column = new TableColumn(text, width);
        return addColumn(column);
    }

    /**
     * Get number of columns in this table.
     *
     * @return number of columns in this table.
     */
    public int getNumberOfColumns() {
        return columns.size();
    }

    /**
     * Get a column based on index.
     *
     * @param index the position of column starting.  The left-most column has index <code>1</code>, the next column
     *              is <code>2</code>, and so on.
     * @return the <code>TableColumn</code> at <code>index</code> position.
     */
    public TableColumn getColumnAt(int index) {
        if ((index < 1) || (index > getNumberOfColumns())) {
            throw new IllegalArgumentException("Index [" + index + "] is not valid.");
        }
        return columns.get(index - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDynamic() {
        return true;
    }

    /**
     * Set a line number for this table line.  See also {@link #getLineNumber()}.
     *
     * @param lineNumber the line number starting from <code>1</code>.
     */
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Retrieve line number for this table line.
     *
     * @return line number starting from <code>1</code>.  The first line number is counted from header if it is
     *         exists.  This method will return <code>null</code> if line number hasn't been set previously.
     */
    public Integer getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public Iterator<TableColumn> iterator() {
        return columns.iterator();
    }

}
