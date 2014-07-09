package simple.escp.fill;

import simple.escp.dom.Line;
import simple.escp.dom.Report;
import simple.escp.dom.TableColumn;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;
import simple.escp.data.DataSources;
import simple.escp.placeholder.BasicPlaceholder;
import simple.escp.placeholder.Placeholder;
import simple.escp.util.EscpUtil;
import java.util.Collection;
import java.util.List;

/**
 * <code>TableFillJob</code> represent the process of filling a <code>TableLine</code> with its source in form
 * of a <code>Collection</code>.
 */
public class TableFillJob {

    protected TableLine tableLine;
    protected Collection source;

    /**
     * Create a new instance of <code>TableFillJob</code>.
     *
     * @param tableLine the <code>TableLine</code> that need to be filled.
     * @param source each entry of this <code>Collection</code> will represent a line in the table.
     */
    public TableFillJob(TableLine tableLine, Collection source) {
        this.tableLine = tableLine;
        this.source = source;
    }

    /**
     * Execute the <code>TableFillJob</code>.
     *
     * @param report the source <code>Report</code> in which the table will be placed.  This new table can be seen
     *               as a subreport for this <code>report</code>.
     * @return a <code>TextLine</code> that may contains ESC/P commands and can be printed.
     */
    public List<Line> fill(Report report) {

        Report subreport = new Report(report.getContentLinesPerPage(), tableLine.getHeader(), tableLine.getFooter());
        int tableLineNumber = tableLine.getLineNumber() == null ? 1 : tableLine.getLineNumber();
        int startLines = tableLine.getHeader().length + tableLineNumber - report.getHeader().length;
        if (startLines > subreport.getStartOfFooter()) {
            throw new IllegalArgumentException("The rest of lines is not enough to store this table without " +
                "creating a new page. (" + startLines + " > " + subreport.getStartOfFooter() + ")");

        }
        subreport.newPage(false, startLines);

        // Save all placeholders first.
        Placeholder[] placeholders = new BasicPlaceholder[tableLine.getNumberOfColumns()];
        for (int i = 0; i < tableLine.getNumberOfColumns(); i++) {
            placeholders[i] = new BasicPlaceholder(tableLine.getColumnAt(i + 1).getText());
        }
        for (Object entry: source) {
            StringBuffer text = new StringBuffer();
            for (int i = 0; i < tableLine.getNumberOfColumns(); i++) {
                TableColumn column = tableLine.getColumnAt(i + 1);
                Object value = placeholders[i].getValue(DataSources.from(new Object[]{entry}));
                int width = column.getWidth() - (tableLine.isDrawBorder() ? 1 : 0);
                if (i == 0 && tableLine.isDrawBorder()) {
                    text.append(EscpUtil.CP347_LIGHT_VERTICAL);
                }
                if ((value instanceof Integer) || (value instanceof Long)) {
                    text.append(String.format("%" + width + "d", value));
                } else if ((value instanceof Float) || (value instanceof  Double)) {
                    text.append(String.format("%" + width + ".1f", value));
                } else {
                    text.append(String.format("%-" + width + "s", value.toString()));
                }
                if (tableLine.isDrawBorder()) {
                    text.append(EscpUtil.CP347_LIGHT_VERTICAL);
                }
            }
            subreport.append(new TextLine(text.toString()), false);
        }

        return subreport.getFlatLines();
    }
}
