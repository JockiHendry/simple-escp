package simple.escp.placeholder;

import simple.escp.data.DataSource;
import simple.escp.exception.InvalidPlaceholder;
import simple.escp.util.StringUtil;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.logging.Logger;

/**
 *  <code>Placeholder</code> represent a placeholder in template, such as <code>${name}</code> or
 *  <code>{{salary * 0.5}}</code>.
 *
 *  <p>Placeholder may have an optional formatting, such as <code>${salary:currency}</code>.  The following
 *  format options are available on number value: <code>number</code>, <code>integer</code> and
 *  <code>currency</code>.  The following options are available on date value: <code>date_full</code>,
 *  <code>date_long</code>, <code>date_medium</code>, and <code>date_short</code>.
 *
 *  <p>All value types support a number as options that will limit the result of this placeholder if number
 *  of resulting characters more than this number.  For example: <code>${salary:currency:10}</code> will always take
 *  10 characters.  If the actual value is greater than the width, it will be truncated.  If the actual value is
 *  less than the width, spaces will be appended to it.
 *
 *  <p>If placeholders has more than one part separated by semicolon (<code>:</code>), the first part should always
 *  be name of the placeholder.
 *
 */
public abstract class Placeholder {

    private static final Logger LOG = Logger.getLogger("simple.escp");

    protected String text;
    protected Format format;
    protected int width = 0;
    protected boolean sum;
    protected boolean count;
    protected StringUtil.ALIGNMENT alignment;

    /**
     * Create a new instance of placeholder.
     *
     * @param text a string that defines this placeholder.
     */
    protected Placeholder(String text) {
        this.text = text.trim();
    }

    /**
     * Get the text of this placeholder.  All placeholder will be identified in template
     * by their text.  For example, placeholder text for <code>${name}</code> is <code>name</code>.
     *
     * @return text of this placeholder.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of this placeholder.
     *
     * @param text the text for this placeholder.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieve the maximal width allowed for the value of this placeholder.
     *
     * @return maximal number of characters for this placeholder.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the maximal number of characters for this placeholder.
     *
     * @param width new maximal number of characters (width) for this placeholder, or <code>0</code> if it is
     *              unlimited.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get a <code>Format</code> for this placeholder.
     *
     * @return an instance of <code>Format</code> for this placeholder.  If no <code>format</code> is defined,
     *         this method will return <code>null</code>.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Set a new <code>Format</code> for this placeholder.
     *
     * @param format new format for this placeholder.
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * Determine if this placeholder is for displaying sum of value.
     *
     * @return <code>true</code> if this placeholder is for displaying sum of value.
     */
    public boolean isSum() {
        return sum;
    }

    /**
     * Set this placeholder to display sum of value instead of the value.
     *
     * @param sum if <code>true</code>, this placeholder will return sum of value.
     */
    public void setSum(boolean sum) {
        this.sum = sum;
    }

    /**
     * Determine if this placeholder is for displaying count of value.
     *
     * @return <code>true</code> if this placeholder is for displaying count of value.
     */
    public boolean isCount() {
        return count;
    }

    /**
     * Set this placeholder to display count of value instead of the value.
     *
     * @param count if <code>true</code>, this placeholder will return count of value.
     */
    public void setCount(boolean count) {
        this.count = count;
    }

    /**
     * Get the alignment for this placeholder.
     *
     * @return alignment for this placeholder or <code>null</code> if no alignment is specified for this
     *         placeholder.
     */
    public StringUtil.ALIGNMENT getAlignment() {
        return alignment;
    }

    /**
     * Set the new alignment for this placeholder.
     *
     * @param alignment the new alignment for this placeholder.
     */
    public void setAlignment(StringUtil.ALIGNMENT alignment) {
        this.alignment = alignment;
    }

    /**
     * Calculate the sum of <code>Collection</code>.
     *
     * @param value a <code>Collection</code> to sum.
     * @return sum of all values in the <code>Collection</code>.
     */
    private BigDecimal getSumValue(Collection value) {
        BigDecimal result = BigDecimal.ZERO;
        for (Object v : value) {
            if (v instanceof Number) {
                result = result.add(BigDecimal.valueOf(((Number) v).doubleValue()));
            } else {
                throw new IllegalArgumentException("sum operation require number: " + v);
            }
        }
        return result;
    }

    /**
     * Calculate the count of elements in a <code>Collection</code>.
     *
     * @param value a <code>Collection</code> to count for.
     * @return count of elements inside this <code>Collection</code>.
     */
    public Object getCountValue(Collection value) {
        return value.size();
    }

    /**
     * Get a formatted version, including width limit, of a value.
     *
     * @param value the value passed to this placehoder.
     * @return the formatted value.  If <code>value</code> is formatted, it will be returned as <code>String</code>.
     *         If no formatting is specified for this placeholder, the <code>value</code> will be returned as is.
     */
    public Object getFormatted(Object value) {
        Object result = value;
        LOG.fine("Formatting [" + value + "]");
        if (value != null) {
            if (isSum()) {
                LOG.fine("Calculating sum for [" + value + "]");
                if (!(value instanceof Collection)) {
                    LOG.warning("Can't calculate sum for [" + value + "] because it is not a Collection.");
                    throw new InvalidPlaceholder("Expected collection for placeholder [" + getText() + "] for " +
                        "sum operation but received value [" + value + "].");
                } else {
                    result = getSumValue((Collection) value);
                }
            } else if (isCount()) {
                LOG.fine("Calculating count for [" + value + "]");
                if (!(value instanceof Collection)) {
                    LOG.warning("Can't calculate count for [" + value + "] because it is not a Collection.");
                    throw new InvalidPlaceholder("Expected collection for placeholder [" + getText() + "] for " +
                        "count operation but received value [" + value + "].");
                } else {
                    result = getCountValue((Collection) value);
                }
            }

            if (getFormat() != null) {
                try {
                    LOG.fine("Formatting [" + result + "] as [" + getFormat() + "]");
                    result = getFormat().format(result);
                } catch (IllegalArgumentException e) {
                    LOG.warning("Can't format [" + result + "] as [" + getFormat() + "]");
                    throw new InvalidPlaceholder("Can't format value [" + result + "] for placeholder [" +
                            getText() + "].", e);
                }
            }
        }

        if (getWidth() > 0) {
            result = (result != null) ? result : "";
            if (getAlignment() == null) {
                LOG.fine("Left-align for [" + result + "] in width [" + getWidth() + "]");
                result = StringUtil.alignLeft(result.toString(), getWidth());
            } else {
                LOG.fine(getAlignment() + " for [" + result + "] in width [" + getWidth() + "]");
                result = StringUtil.align(result.toString(), getWidth(), getAlignment());
            }
        }

        return (result != null) ? result : "";
    }

    /**
     * Parse aggregation formula such as <code>"sum"</code> and <code>"count"</code> in placeholder text.
     *
     * @param text part of text for this placeholder.
     */
    protected void parseFormula(String text) {
        if ("sum".equals(text)) {
            setSum(true);
        } else if ("count".equals(text)) {
            setCount(true);
        }
    }

    /**
     * Parse formatter such as <code>number</code>, <code>date_full</code>, etc.
     *
     * @param text part of text for this placeholder.
     */
    protected void parseFormatter(String text) {
        if ("number".equals(text)) {
            format = NumberFormat.getNumberInstance();
        } else if ("integer".equals(text)) {
            format = NumberFormat.getIntegerInstance();
        } else if ("currency".equals(text)) {
            format = NumberFormat.getCurrencyInstance();
        } else if ("date_full".equals(text)) {
            format = DateFormat.getDateInstance(DateFormat.FULL);
        } else if ("date_long".equals(text)) {
            format = DateFormat.getDateInstance(DateFormat.LONG);
        } else if ("date_medium".equals(text)) {
            format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        } else if ("date_short".equals(text)) {
            format = DateFormat.getDateInstance(DateFormat.SHORT);
        }
    }

    /**
     * Parse width for a placeholder.  By default, <code>width</code> is 0 and no restriction will be applied.
     *
     * @param text part of text for this placeholder.
     */
    protected void parseWidth(String text) {
        try {
            width = Integer.valueOf(text);
        } catch (NumberFormatException e) {
            LOG.fine("Can't convert [" + text + "] to number.");
        }
    }

    /**
     * Parse alignment for this placeholder, such as <code>"left"</code>, <code>"right"</code>, or
     * <code>"center"</code>.
     *
     * @param text part of text for this placeholder.
     */
    protected void parseAlignment(String text) {
        if ("left".equals(text)) {
            setAlignment(StringUtil.ALIGNMENT.LEFT);
        } else if ("right".equals(text)) {
            setAlignment(StringUtil.ALIGNMENT.RIGHT);
        } else if ("center".equals(text)) {
            setAlignment(StringUtil.ALIGNMENT.CENTER);
        }
    }

    /**
     * Parse an expression in placeholder expression.  The expression should be broken into multiple keywords
     * that are stored in <code>text</code>.
     *
     * @param text an array of string that represent keywords that should be parsed.
     */
    protected void parseText(String[] text) {
        for (String part: text) {
            part = part.trim();
            LOG.fine("Processing part [" + part + "]");
            parseFormula(part);
            parseFormatter(part);
            parseWidth(part);
            parseAlignment(part);
        }
    }

    /**
     * Retrieve a value from specified <code>DataSource</code>.
     *
     * @param dataSources the data sources from where this placeholder retrieves its value.
     * @return the value for the member name.
     * @throws simple.escp.exception.InvalidPlaceholder if can't find the value for the member name in
     *         data source.
     */
    public abstract Object getValue(DataSource[] dataSources);

    /**
     * Retrieve a value for a <code>Placeholder</code> in form of <code>String</code>.
     *
     * @param dataSources the data sources from where this placeholder retrieves its value.
     * @return the value for the <code>placeholder</code> in form of <code>String</code>.
     * @throws simple.escp.exception.InvalidPlaceholder if can't find the value for <code>placeholder</code> is
     *         data source.
     */
    public String getValueAsString(DataSource[] dataSources) {
        return getFormattedValue(dataSources).toString();
    }

    /**
     * Get a formatted version of {@link #getValue(simple.escp.data.DataSource[])} .
     *
     * @param dataSources the data sources from where this placeholder retrieves its value.
     * @return the formatted value.  If <code>value</code> is formatted, it will be returned as <code>String</code>.
     *         If no formatting is specified for this placeholder, the <code>value</code> will be returned as is.
     */
    public Object getFormattedValue(DataSource[] dataSources) {
        return getFormatted(getValue(dataSources));
    }

}
