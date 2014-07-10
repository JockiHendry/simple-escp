package simple.escp.fill;

import simple.escp.data.DataSource;
import simple.escp.data.DataSources;
import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.dom.line.ListLine;
import simple.escp.dom.line.TextLine;
import simple.escp.placeholder.ScriptPlaceholder;
import javax.script.ScriptContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <code>ListFillJob</code> represent the process of filling a <code>ListLine</code> with its source in form of
 * a <code>Collection</code>.
 */
public class ListFillJob extends FillJob {

    /**
     * Create a new instance of <code>ListFillJob</code>.
     *
     * @param report the target destination of this <code>FillJob</code>.  This <code>FillJob</code> will not
     *               return any value but directly modify destination <code>report</code>.
     * @param dataSources  global data source for this <code>FillJob</code>.
     */
    public ListFillJob(Report report, DataSource[] dataSources) {
        super(report, dataSources);
    }

    /**
     * Fill <code>ListLine</code>.
     *
     * @param listLine process this <code>ListLine</code>.
     * @param source data source for this <code>ListLine</code>.
     * @return result in form of <code>List</code> of <code>Line</code>.
     */
    private List<Line> fillListLine(ListLine listLine, Collection source) {
        Report subreport = new Report(report.getContentLinesPerPage(), listLine.getHeader(), listLine.getFooter());
        int listLineNumber = listLine.getLineNumber() == null ? 1 : listLine.getLineNumber();
        int startLines = listLine.getHeader().length + listLineNumber - report.getHeader().length;
        if (startLines > subreport.getStartOfFooter()) {
            throw new IllegalArgumentException("The rest of lines is not enough to store this list without " +
                    "creating a new page. (" + startLines + " > " + subreport.getStartOfFooter() + ")");

        }
        subreport.newPage(false, startLines);

        for (Object entry: source) {
            dataSources = new DataSource[] {DataSources.from(entry)};
            DataSourceBinding lineContext = new DataSourceBinding(dataSources);
            scriptEngine.setBindings(lineContext, ScriptContext.ENGINE_SCOPE);
            String result = fillBasicPlaceholder(listLine.getLineSource());
            result = fillScriptPlaceholder(result);
            subreport.append(new TextLine(result), false);
        }

        return subreport.getFlatLines();
    }

    /**
     * Execute the <code>ListFillJob</code>.  This <code>FillJob</code> will make changes directly to
     * <code>Report</code> and doesn't return anything.
     *
     * @return always return <code>null</code>.
     */
    @Override
    public String fill() {
        Page page;
        DataSource[] globalDataSources = Arrays.copyOf(dataSources, dataSources.length);
        while ((page = report.getFirstPageWithListLines()) != null) {
            ListLine listLine = page.getListLines().get(0);
            Collection dataSource = (Collection) (new ScriptPlaceholder(listLine.getSource(), scriptEngine)).
                getValue(globalDataSources);
            List<Line> results = fillListLine(listLine, dataSource);
            Collections.reverse(results);
            page.removeLine(listLine);
            for (Line result : results) {
                report.insert(result, page.getPageNumber(), listLine.getLineNumber());
            }
        }
        return null;
    }

}
