package simple.escp;

import simple.escp.data.DataSource;
import simple.escp.exception.InvalidPlaceholder;
import simple.escp.util.EscpUtil;
import java.util.Arrays;
import java.util.HashMap;
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

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9_@]+)\\}");
    public static final Pattern FUNCTION_PATTERN = Pattern.compile("%\\{([a-zA-Z0-9_]+)\\}");

    protected Report report;
    protected DataSource[] dataSources;
    protected Map<String, Placeholder> placeholders = new HashMap<>();

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
     * Retrieve a value for a <code>Placeholder</code> in form of <code>String</code>.  See also
     * {@link #getValue(Placeholder)}.
     *
     * @param placeholder the <code>Placeholder</code> whose value will be retrieved.
     * @return the value for the <code>placeholder</code>.
     * @throws simple.escp.exception.InvalidPlaceholder if can't find the value for <code>placeholder</code> is
     *         data source.
     */
    public String getValueAsString(Placeholder placeholder) {
        return getValue(placeholder).toString();
    }

    /**
     * Retrieve a value for a <code>Placeholder</code> from <code>DataSource</code>.
     *
     * @param placeholder the <code>Placeholder</code> whose value will be retrieved.
     * @return the value for the <code>placeholder</code>.
     * @throws simple.escp.exception.InvalidPlaceholder if can't find the value for <code>placeholder</code> in
     *         data source.
     */
    public Object getValue(Placeholder placeholder) {
        return getValue(placeholder.getText());
    }

    /**
     * Retrieve a value for a member name from <code>DataSource</code>.
     *
     * @param name the member name that is in <code>DataSource</code>.
     * @return the value for the member name.
     * @throws simple.escp.exception.InvalidPlaceholder if can't find the value for the member name in
     *         data source.
     */
    public Object getValue(String name) {
        for (DataSource dataSource: dataSources) {
            if (dataSource.has(name)) {
                return dataSource.get(name);
            }
        }
        throw new InvalidPlaceholder("Can't find data source's member for [" + name + "]");
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
    private String fillPlaceholder(String text) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String placeholderText = matcher.group(1);
            Placeholder placeholder = placeholders.get(placeholderText);
            if (placeholder == null) {
                placeholder = new Placeholder(placeholderText);
                placeholders.put(placeholderText, placeholder);
            }
            matcher.appendReplacement(result, getValueAsString(placeholder));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Execute this <code>FillJob</code> action.  This will perform the action of filling <code>Report</code> with
     * one or more <code>DataSource</code>.  This method will not modify the original <code>Report</code>.
     *
     * @return a <code>String</code> that may contains ESC/P commands and can be printed.
     */
    public String fill() {
        StringBuffer result = new StringBuffer();
        boolean isAutoLineFeed = report.getPageFormat().isAutoLineFeed();
        boolean isAutoFormFeed = report.getPageFormat().isAutoFormFeed();
        result.append(report.getPageFormat().build());
        for (Page page : report) {
            String pageText = page.convertToString(isAutoLineFeed, isAutoFormFeed);
            pageText = fillPlaceholder(pageText);
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
