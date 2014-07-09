package simple.escp.fill;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.dom.line.TableLine;
import simple.escp.data.DataSource;
import simple.escp.placeholder.BasicPlaceholder;
import simple.escp.placeholder.Placeholder;
import simple.escp.placeholder.ScriptPlaceholder;
import simple.escp.util.EscpUtil;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>FillJob</code> represent the process of filling a <code>Report</code> with one or more
 * <code>DataSource</code>.  The result of this process is a <code>String</code> that may contains ESC/P commands
 * for printing.
 *
 * <p>A new instance of <code>FillJob</code> should be created for every process of <code>Report</code>'s filling.
 * The new instance can reuse existing <code>Report</code> or <code>DataSource</code>.
 */
public class FillJob {

    public static final Pattern BASIC_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
    public static final Pattern SCRIPT_PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.+?)\\}\\}");
    public static final Pattern FUNCTION_PATTERN = Pattern.compile("%\\{(.+?)\\}");

    protected Report report;
    protected DataSource[] dataSources;
    protected Map<String, Placeholder> placeholders = new HashMap<>();
    protected ScriptEngine scriptEngine;

    /**
     * Create a new <code>FillJob</code> with empty data source.
     *
     * @param report the <code>Report</code> that will be filled.
     */
    public FillJob(Report report) {
        this(report, new DataSource[0]);
    }

    /**
     * Create a new <code>FillJob</code> with single <code>DataSource</code>.
     *
     * @param report the <code>Report</code> that will be filled.
     * @param dataSource the <code>DataSource</code> that contains values for filling.
     */
    public FillJob(Report report, DataSource dataSource) {
        this(report, new DataSource[] {dataSource});
    }

    /**
     * Create a new <code>FillJob</code> with multipe <code>DataSource</code>.
     *
     * @param report the <code>Report</code> that will be filled.
     * @param dataSources array that contains <code>DataSource</code> as the source values for filling.
     */
    public FillJob(Report report, DataSource[] dataSources) {
        this.report = report;
        this.dataSources = Arrays.copyOf(dataSources, dataSources.length);

        // Create script engine for ScriptPlaceholder
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngineManager.setBindings(new DataSourceBinding(this.dataSources));
        this.scriptEngine = scriptEngineManager.getEngineByName("groovy");
        if (this.scriptEngine == null) {
            this.scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        }
    }

    /**
     * Retrieve the report that will be filled by this <code>FillJob</code>.
     *
     * @return an instance of <code>Report</code>.
     */
    public Report getReport() {
        return report;
    }

    /**
     * Retrieve all data source for this <code>FillJob</code>.
     *
     * @return an array of <code>DataSource</code>.
     */
    public DataSource[] getDataSources() {
        return Arrays.copyOf(dataSources, dataSources.length);
    }

    /**
     * Get available <code>Placeholder</code> in this report.
     *
     * @return a <code>Map</code> that contains all <code>Placeholder</code> in this report.
     */
    public Map<String, Placeholder> getPlaceholders() {
        return placeholders;
    }

    /**
     * Find and return a <code>Placeholder</code> by its text.
     * @param text the placeholder's text.  A placeholder text appears as is in template.  For example,
     *             text for <code>${\@name}</code> is <code>"@name"</code>.
     * @return a <code>Placeholder</code> if it is found, or <code>null</code> if no placeholder with the specified
     *         text is exists in this report.
     */
    public Placeholder getPlaceholder(String text) {
        return placeholders.get(text);
    }

    /**
     * This method will evaluate functions.
     *
     * @param text the source text that has functions.
     * @param page the associated <code>Page</code> for source text.
     * @return source with functions replaced by evaluated value.
     */
    private String fillFunction(String text, Page page) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = FUNCTION_PATTERN.matcher(text);
        while (matcher.find()) {
            String function = matcher.group(1);

            // PAGE_NO
            if ("PAGE_NO".equals(function)) {
                matcher.appendReplacement(result, String.valueOf(page.getPageNumber()));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * This method will fill placeholders with value from both supplied <code>Map</code> and Java Bean object.
     *
     * @param text the source text that has placeholders.
     * @return source with placeholders replaced by actual value.
     */
    private String fillBasicPlaceholder(String text) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = BASIC_PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String placeholderText = matcher.group(1);
            Placeholder placeholder = placeholders.get(placeholderText);
            if (placeholder == null) {
                placeholder = new BasicPlaceholder(placeholderText);
                placeholders.put(placeholderText, placeholder);
            }
            matcher.appendReplacement(result, placeholder.getValueAsString(dataSources));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * This method will fill placeholders by executing the script inside that placeholder.
     *
     * @param text the source text that has placeholders.
     * @return source with placeholders replaced by actual value.
     */
    private String fillScriptPlaceholder(String text) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = SCRIPT_PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String placeholderText = matcher.group(1);
            Placeholder placeholder = placeholders.get(placeholderText);
            if (placeholder == null) {
                placeholder = new ScriptPlaceholder(placeholderText, scriptEngine);
                placeholders.put(placeholderText, placeholder);
            }
            matcher.appendReplacement(result, placeholder.getValueAsString(dataSources));
        }
        matcher.appendTail(result);
        return result.toString();

    }

    /**
     * Fill <code>TableLine</code>.
     *
     * @param report the <code>Report</code> that will be filled.  This is not necessary the same as the
     *               original report becauses <code>FillJob</code> shouldn't change original <code>Report</code>
     *               so that it can be reused in the next filling.
     */
    private void fillTableLine(Report report) {
        Page page;
        while ((page = report.getFirstPageWithTableLines()) != null) {
            TableLine tableLine = page.getTableLines().get(0);
            Placeholder placeholder = new BasicPlaceholder(tableLine.getSource());
            TableFillJob tableFillJob = new TableFillJob(tableLine, (Collection) placeholder.getValue(dataSources));
            List<Line> results = tableFillJob.fill(report);
            Collections.reverse(results);
            page.removeLine(tableLine);
            for (Line result : results) {
                report.insert(result, page.getPageNumber(), tableLine.getLineNumber());
            }
        }
    }

    /**
     * Execute this <code>FillJob</code> action.  This will perform the action of filling <code>Report</code> with
     * one or more <code>DataSource</code>.  This method will not modify the original <code>Report</code>.
     *
     * @return a <code>String</code> that may contains ESC/P commands and can be printed.
     */
    public String fill() {
        Report parsedReport = report;
        if (parsedReport.hasDynamicLine()) {
            parsedReport = new Report(report);
            fillTableLine(parsedReport);
        }
        StringBuffer result = new StringBuffer();
        boolean isAutoLineFeed = parsedReport.getPageFormat().isAutoLineFeed();
        boolean isAutoFormFeed = parsedReport.getPageFormat().isAutoFormFeed();
        result.append(parsedReport.getPageFormat().build());
        for (Page page : parsedReport) {
            String pageText = page.convertToString(isAutoLineFeed, isAutoFormFeed);
            pageText = fillBasicPlaceholder(pageText);
            pageText = fillScriptPlaceholder(pageText);
            pageText = fillFunction(pageText, page);
            result.append(pageText);
        }
        if (isAutoFormFeed && !result.toString().endsWith(EscpUtil.CRFF)) {
            result.append(EscpUtil.CRFF);
        }
        result.append(EscpUtil.escInitalize());
        return result.toString();
    }

}
