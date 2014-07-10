package simple.escp.fill;

import simple.escp.data.DataSource;
import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.dom.TableColumn;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;
import simple.escp.data.DataSources;
import simple.escp.placeholder.BasicPlaceholder;
import simple.escp.placeholder.Placeholder;
import simple.escp.util.EscpUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <code>TableFillJob</code> represent the process of filling a <code>TableLine</code> with its source in form
 * of a <code>Collection</code>.
 */
public class TableFillJob extends FillJob {

    /**
     * Create a new instance of <code>TableFillJob</code>.
     *
     * @param report the target destination of this <code>FillJob</code>.  This <code>FillJob</code> will not
     *               return any value but directly modify destination <code>report</code>.
     * @param dataSources  global data source for this <code>FillJob</code>.
     */
    public TableFillJob(Report report, DataSource[] dataSources) {
        super(report, dataSources);
    }

    /**
     * Fill <code>TableLine</code>.
     *
     * @param tableLine process this <code>TableLine</code>.
     * @param source data source for this <code>TableLine</code>.
     * @return result in form of <code>List</code> of <code>Line</code>.
     */
    private List<Line> fillTableLine(TableLine tableLine, Collection source) {
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
                String valueAsString = "";
                if ((value instanceof Integer) || (value instanceof Long)) {
                    valueAsString = String.format("%" + width + "d", value);
                } else if ((value instanceof Float) || (value instanceof  Double)) {
                    valueAsString = String.format("%" + width + ".1f", value);
                } else {
                    valueAsString = String.format("%-" + width + "s", value.toString());
                }
                if (valueAsString.length() > width) {
                    valueAsString = valueAsString.substring(0, width);
                }
                text.append(valueAsString);
                if (tableLine.isDrawBorder()) {
                    text.append(EscpUtil.CP347_LIGHT_VERTICAL);
                }
            }
            subreport.append(new TextLine(text.toString()), false);
        }

        return subreport.getFlatLines();

    }

    /**
     * Execute the <code>TableFillJob</code>.  This <code>FillJob</code> will make changes directly to
     * <code>Report</code> and doesn't return anything.
     *
     * @return always return <code>null</code>.
     */
    @Override
    public String fill() {
        Page page;
        while ((page = report.getFirstPageWithTableLines()) != null) {
            TableLine tableLine = page.getTableLines().get(0);
            Collection dataSource = (Collection) (new BasicPlaceholder(tableLine.getSource())).getValue(dataSources);
            List<Line> results = fillTableLine(tableLine, dataSource);
            Collections.reverse(results);
            page.removeLine(tableLine);
            for (Line result : results) {
                report.insert(result, page.getPageNumber(), tableLine.getLineNumber());
            }
        }
        return null;
    }
}
