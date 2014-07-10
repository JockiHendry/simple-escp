package simple.escp.dom.line;

import simple.escp.dom.Line;
import simple.escp.dom.TableColumn;
import simple.escp.placeholder.BasicPlaceholder;
import simple.escp.util.EscpUtil;
import simple.escp.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * DOM class to represent table.  A table consists of one or more {@link simple.escp.dom.TableColumn}.
 */
public class TableLine implements Line, Iterable<TableColumn> {

    private List<TableColumn> columns = new ArrayList<>();
    private String source;
    private Integer lineNumber;
    private boolean drawBorder;
    private TextLine[] header;
    private TextLine[] footer;

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

    /**
     * Determine if this table line should have border drawn around it.  Drawing border will reduce the
     * width of every columns by one character.  It also will increase number of lines required by the table.
     *
     * @return <code>true</code> if this <code>TableLine</code> has border.
     */
    public boolean isDrawBorder() {
        return drawBorder;
    }

    /**
     * Set wether to enable drawing solid border for this table line or not.
     *
     * @param drawBorder <code>true</code> to enable border for this table.
     */
    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    /**
     * Get width of lines in this table in number of characters.
     *
     * @return width in number of characters.
     */
    public int getWidth() {
        int result = 0;
        for (TableColumn column : columns) {
            result += column.getWidth();
        }
        return result;
    }

    /**
     * Retrieve the header for this table.  If border for this table is disabled, header will only be a row that
     * contains column's caption.  If border is enabled, the header is three lines with CP347 pseudo-graphic
     * characters to simulate a border.
     *
     * @return the definition of header.
     */
    public TextLine[] getHeader() {
        if (header == null) {
            List<TextLine> tmp = new ArrayList<>();
            StringBuffer line = new StringBuffer();

            // draw header upper border if necessary
            if (isDrawBorder()) {
                line.append(EscpUtil.CP347_LIGHT_DOWN_RIGHT);
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableColumn column = columns.get(columnIndex);
                    for (int i = 0; i < column.getWidth() - 1; i++) {
                        line.append(EscpUtil.CP347_LIGHT_HORIZONTAL);
                    }
                    if (columnIndex == (columns.size() - 1)) {
                        line.append(EscpUtil.CP347_LIGHT_DOWN_LEFT);
                    } else {
                        line.append(EscpUtil.CP347_LIGHT_DOWN_HORIZONTAL);
                    }
                }
                tmp.add(new TextLine(line.toString()));
            }

            // draw column name
            line = new StringBuffer();
            if (isDrawBorder()) {
                line.append(EscpUtil.CP347_LIGHT_VERTICAL);
            }
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                TableColumn column = columns.get(columnIndex);
                int width = column.getWidth() - (isDrawBorder() ? 1 : 0);
                StringUtil.ALIGNMENT alignment = (new BasicPlaceholder(column.getText())).getAlignment();
                if (alignment == null) {
                    alignment = StringUtil.ALIGNMENT.LEFT;
                }
                line.append(StringUtil.align(column.getCaption(), width, alignment));
                if (isDrawBorder()) {
                    line.append(EscpUtil.CP347_LIGHT_VERTICAL);
                }
            }
            tmp.add(new TextLine(line.toString()));

            // draw lower border if necessary
            line = new StringBuffer();
            if (isDrawBorder()) {
                line.append(EscpUtil.CP347_LIGHT_VERTICAL_RIGHT);
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableColumn column = columns.get(columnIndex);
                    for (int i = 0; i < column.getWidth() - 1; i++) {
                        line.append(EscpUtil.CP347_LIGHT_HORIZONTAL);
                    }
                    if (columnIndex == (columns.size() - 1)) {
                        line.append(EscpUtil.CP347_LIGHT_VERTICAL_LEFT);
                    } else {
                        line.append(EscpUtil.CP347_LIGHT_VERTICAL_HORIZONTAL);
                    }
                }
                tmp.add(new TextLine(line.toString()));
            }
            header = tmp.toArray(new TextLine[0]);
        }
        return Arrays.copyOf(header, header.length);
    }

    /**
     * Retrieve the footer for this table.
     *
     * @return the definition of footer.
     */
    public TextLine[] getFooter() {
        if (footer == null) {
            List<TextLine> tmp = new ArrayList<>();
            StringBuffer line = new StringBuffer();

            // draw lower border if necessary
            if (isDrawBorder()) {
                line.append(EscpUtil.CP347_LIGHT_UP_RIGHT);
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableColumn column = columns.get(columnIndex);
                    for (int i = 0; i < column.getWidth() - 1; i++) {
                        line.append(EscpUtil.CP347_LIGHT_HORIZONTAL);
                    }
                    if (columnIndex == (columns.size() - 1)) {
                        line.append(EscpUtil.CP347_LIGHT_UP_LEFT);
                    } else {
                        line.append(EscpUtil.CP347_LIGHT_UP_HORIZONTAL);
                    }
                }
                tmp.add(new TextLine(line.toString()));
            }
            footer = tmp.toArray(new TextLine[0]);
        }
        return Arrays.copyOf(footer, footer.length);
    }

    @Override
    public Iterator<TableColumn> iterator() {
        return columns.iterator();
    }

}
