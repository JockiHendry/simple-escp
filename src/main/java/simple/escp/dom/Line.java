package simple.escp.dom;

/**
 * DOM class to represent each line in <code>Page</code>.
 */
public abstract class Line {

    private Integer lineNumber;
    private Integer globalLineNumber;

    /**
     * Defines wether this line is a dynamic line or not.  A dynamic line is a line that will be expanded
     * later when filling this line with data.  For example, a table line is a dynamic line because number
     * of lines for that table can't be defined in parsing step.  It requires the actual value to define
     * the actual number of lines.
     *
     * @return <code>true</code> if this is a dynamic line or <code>false</code> if otherwise.
     */
    public abstract  boolean isDynamic();

    /**
     * Set a line number for this line.  See also {@link #getLineNumber()}.
     *
     * <p>Implementations are not required to provide line number value.
     *
     * @param lineNumber the line number starting from <code>1</code>.
     */
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Retrieve line number for this line.
     *
     * <p>Implementations are not required to provide global line number value.
     *
     * @return line number starting from <code>1</code>.  The first line number is counted from header if it is
     *         exists.  This method will return <code>null</code> if line number hasn't been set previously.
     */
    public Integer getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Set a global line number for this line.  The difference between global line number and
     * {@link #setLineNumber(Integer)} is global line number will never restart to <code>1</code> while
     * {@link #setLineNumber(Integer)} should restart to <code>1</code> when starting a new page.
     *
     * <p>Implementations are not required to provide global line number value.
     *
     * @param globalLineNumber the global line number starting from <code>1</code>.
     */
    public void setGlobalLineNumber(Integer globalLineNumber) {
        this.globalLineNumber = globalLineNumber;
    }

    /**
     * Get a global line number for this line.  Global line number will be incremented by <code>1</code> until
     * end of report is reached.
     *
     * <p>Implementations are not required to provide global line number value.
     *
     * @return line number starting from <code>1</code>.
     */
    public Integer getGlobalLineNumber() {
        return this.globalLineNumber;
    }

}
