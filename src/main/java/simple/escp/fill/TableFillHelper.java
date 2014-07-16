package simple.escp.fill;

import simple.escp.data.DataSource;
import simple.escp.data.DataSources;
import simple.escp.dom.Line;
import simple.escp.dom.Report;
import simple.escp.dom.TableColumn;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;
import simple.escp.placeholder.Placeholder;
import simple.escp.placeholder.ScriptPlaceholder;
import simple.escp.util.EscpUtil;
import simple.escp.util.StringUtil;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import java.util.Collection;
import java.util.List;

/**
 * A helper class used internally by {@link simple.escp.fill.TableFillJob}.
 */
public class TableFillHelper {

    private Report report;
    private TableLine tableLine;
    private Collection source;
    private ScriptEngine scriptEngine;
    private WrappedBuffer wrappedBuffer;
    private Placeholder[] placeholders;

    /**
     * Create a new instance of this helper class.
     *
     * @param report <code>flush()</code> method will add new <code>TextLine</code> to this <code>Report</code>.
     * @param scriptEngine the <code>ScriptEngine</code> for evaluating placeholders.
     * @param tableLine the <code>TableLine</code> to be filled.
     * @param source source for <code>tableLine</code>.
     */
    public TableFillHelper(Report report, ScriptEngine scriptEngine, TableLine tableLine, Collection source) {
        this.report = report;
        this.scriptEngine = scriptEngine;
        this.tableLine = tableLine;
        this.source = source;
        this.wrappedBuffer = new WrappedBuffer();
        preparePlaceholders();
    }

    /**
     * Read information from <code>tableLine</code> and creates instance of <code>ScriptPlaceholder</code>
     * for every columns.
     */
    private void preparePlaceholders() {
        placeholders = new ScriptPlaceholder[tableLine.getNumberOfColumns()];
        for (int i = 0; i < tableLine.getNumberOfColumns(); i++) {
            TableColumn column = tableLine.getColumnAt(i + 1);
            placeholders[i] = new ScriptPlaceholder(column.getText(), scriptEngine);
            if (!column.isWrap()) {
                placeholders[i].setWidth(column.getWidth() - (tableLine.isDrawBorder() ? 1 : 0));
            }
        }
    }

    /**
     * Add new text to a <code>StringBuffer</code> that represents the content of a line.
     *
     * @param result new text will be appended to this <code>StringBuffer</code>.
     * @param text the content that will be appended.
     * @param index the position of this column (start from <code>0</code> for the left-most column).  This value
     *              is required to determine what borders to print if table border is enabled.
     */
    private void appendLine(StringBuffer result, String text, int index) {
        if (index == 0 && tableLine.isDrawBorder()) {
            result.append(EscpUtil.CP347_LIGHT_VERTICAL);
        }
        result.append(text);
        if (tableLine.isDrawBorder()) {
            result.append(EscpUtil.CP347_LIGHT_VERTICAL);
        }
    }

    /**
     * Execute this helper function.
     *
     * @return a collection of <code>Line</code>.
     */
    public List<Line> process() {
        for (Object entry: source) {
            StringBuffer text = new StringBuffer();
            DataSource[] entryDataSources = DataSources.from(new Object[]{entry});
            DataSourceBinding lineContext = new DataSourceBinding(entryDataSources);
            scriptEngine.setBindings(lineContext, ScriptContext.ENGINE_SCOPE);
            for (int i = 0; i < tableLine.getNumberOfColumns(); i++) {
                TableColumn column = tableLine.getColumnAt(i + 1);
                String value = placeholders[i].getValueAsString(entryDataSources);
                if (column.isWrap()) {
                    appendLine(text, wrappedBuffer.add(i, value), i);
                } else {
                    appendLine(text, value, i);
                }
            }
            report.append(new TextLine(text.toString()), false);
            wrappedBuffer.flush();
        }
        return report.getFlatLines();
    }

    /**
     * Retrieve the <code>WrappedBuffer</code> for this helper.
     *
     * @return an instnace of <code>WrappedBuffer</code> used by this helper.
     */
    public WrappedBuffer getWrappedBuffer() {
        return wrappedBuffer;
    }

    /**
     * This is a helper class used internally by <code>TableFillHelper</code>.  This class will handle values
     * that need to be wrapped to the next line.
     */
    public class WrappedBuffer {

        private String[] buffer;
        private int[] width;

        /**
         * Create a new instance of <code>WrappedBuffer</code>.
         */
        public WrappedBuffer() {
            buffer = new String[tableLine.getNumberOfColumns()];
            width = new int[tableLine.getNumberOfColumns()];
            for (int i = 0; i < tableLine.getNumberOfColumns(); i++) {
                TableColumn column = tableLine.getColumnAt(i + 1);
                width[i] = column.getWidth() - (tableLine.isDrawBorder() ? 1 : 0);
            }
        }

        /**
         * Add a new text to a column.  If the new text need to be wrapped, this method will return
         * a truncated version of <code>value</code> that fits for current line.  It will store the rest part of
         * <code>value</code> to <code>buffer</code> that can be consumed later by calling {@link #consume(int)}.
         * If <code>value</code> fits for this column, it will return the left-aligned version of <code>value</code>.
         *
         * @param index the column index, starts from <code>0</code> for the left-most column.
         * @param value the value for the specified column.
         * @return the truncated or the left-aligned <code>value</code>.
         */
        public String add(int index, String value) {
            if (value.length() <= width[index]) {
                return StringUtil.alignLeft(value, width[index]);
            } else {
                buffer[index] = value.substring(width[index]);
                return value.substring(0, width[index]);
            }
        }

        /**
         * Get the content of buffer for a column.
         *
         * @param index the column index, starts from <code>0</code> for the left-most column.
         * @return content of buffer for column at <code>index</code>.
         */
        public String getBuffer(int index) {
            return buffer[index];
        }

        /**
         * Get the width of a column.
         *
         * @param index the column index, starts from <code>0</code> for the left-most column.
         * @return the width for column at <code>index</code>.
         */
        public int getWidth(int index) {
            return width[index];
        }

        /**
         * Read and remove the value in the buffer that fits for a line.  If buffer is empty, this method will
         * return empty spaces as much as the column's width.
         *
         * @param index the column index, starts from <code>0</code> for the left-most column.
         * @return the value from buffer that fits for a line.
         */
        public String consume(int index) {
            String value = getBuffer(index);
            if (value == null) {
                return StringUtil.alignLeft("", getWidth(index));
            }
            if (value.length() <= getWidth(index)) {
                buffer[index] = null;
                return StringUtil.alignLeft(value, getWidth(index));
            } else {
                return add(index, value);
            }
        }

        /**
         * Check if this buffer is empty.
         *
         * @return <code>true</code> if buffer is empty.
         */
        public boolean isEmpty() {
            for (String s : buffer) {
                if (s != null) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Remove all column's buffers.
         */
        public void clear() {
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = null;
            }
        }

        /**
         * Process all column's buffers and insert them as <code>TextLine</code> to current <code>report</code>.
         */
        public void flush() {
            while (!isEmpty()) {
                StringBuffer result = new StringBuffer();
                for (int i = 0; i < buffer.length; i++) {
                    appendLine(result, consume(i), i);
                }
                report.append(new TextLine(result.toString()), false);
            }
            clear();
        }

    }
}
