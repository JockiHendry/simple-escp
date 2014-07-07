package simple.escp.data;

import simple.escp.exception.InvalidPlaceholder;

/**
 * A <code>DataSource</code> is used to supply value when filling a <code>Report</code>.
 */
public interface DataSource {

    /**
     * Check if this <code>DataSource</code> contains certain member name.
     *
     * @param member the member that will be verified.
     * @return <code>true</code> if this <code>DataSource</code> contains member.
     */
    public boolean has(String member);

    /**
     * Retrieve value for a member from this <code>DataSource</code>.
     *
     * @param member the member that will be retrieved.  This <code>DataSource</code> must have the specified
     *               member.
     * @return an object that represent the member value.
     * @throws simple.escp.exception.InvalidPlaceholder if member doesn't exists or can't retrieve the member value.
     */
    public Object get(String member) throws InvalidPlaceholder;

    /**
     * Retrieve the source value of this <code>DataSource</code>.
     *
     * @return source value of this <code>DataSource</code>.
     */
    public Object getSource();

    /**
     * Retrieve all member of this <code>DataSource</code>.
     *
     * @return all member of this <code>DataSource</code> or empty array if no member is available.
     */
    public String[] getMembers();

}
