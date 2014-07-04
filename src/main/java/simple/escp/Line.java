package simple.escp;

/**
 * DOM class to represent each line in <code>Page</code>.
 */
public interface Line {

    /**
     * Defines wether this line is a dynamic line or not.  A dynamic line is a line that will be expanded
     * later when filling this line with data.  For example, a table line is a dynamic line because number
     * of lines for that table can't be defined in parsing step.  It requires the actual value to define
     * the actual number of lines.
     *
     * @return <code>true</code> if this is a dynamic line or <code>false</code> if otherwise.
     */
    public boolean isDynamic();

}
