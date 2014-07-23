package simple.escp.fill;

import simple.escp.data.DataSource;
import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.dom.line.TableLine;
import simple.escp.exception.InvalidPlaceholder;
import simple.escp.placeholder.ScriptPlaceholder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * <code>TableFillJob</code> represent the process of filling a <code>TableLine</code> with its source in form
 * of a <code>Collection</code>.
 */
public class TableFillJob extends FillJob {

    private static final Logger LOG = Logger.getLogger("simple.escp");

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
        if (source == null) {
            throw new InvalidPlaceholder("Source for table can't be null.");
        }
        Report subreport = new Report(report.getContentLinesPerPage(), tableLine.getHeader(), tableLine.getFooter());
        int tableLineNumber = tableLine.getLineNumber() == null ? 1 : tableLine.getLineNumber();
        int startLines = tableLine.getHeader().length + tableLineNumber - report.getHeader().length;
        if (startLines > subreport.getStartOfFooter()) {
            throw new IllegalArgumentException("The rest of lines is not enough to store this table without " +
                    "creating a new page. (" + startLines + " > " + subreport.getStartOfFooter() + ")");

        }
        LOG.fine("Table start at line [" + startLines + "]");
        subreport.newPage(false, startLines);
        TableFillHelper helper = new TableFillHelper(subreport, scriptEngine, tableLine, source);
        return helper.process();
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
            page.removeLine(tableLine);
            Object dataSource = (new ScriptPlaceholder(tableLine.getSource(), scriptEngine)).getValue(dataSources);
            if (dataSource instanceof Collection) {
                LOG.fine("Datasource is [" + dataSource + "]");
                List<Line> results = fillTableLine(tableLine, (Collection) dataSource);
                Collections.reverse(results);
                for (Line result : results) {
                    LOG.fine("Add new line [" + result.toString() + "]");
                    report.insert(result, page.getPageNumber(), tableLine.getLineNumber());
                }
            } else if (dataSource == null) {
                LOG.warning("Table was skipped because data source was null.");
            } else {
                throw new InvalidPlaceholder("Data source must be a Collection but found [" + dataSource +
                        "] as a [" + dataSource.getClass() + "]");
            }
        }
        return null;
    }
}
