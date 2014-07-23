package simple.escp.data;

import simple.escp.exception.InvalidPlaceholder;

/**
 * An empty data source that does nothing excepts returning empty string.
 */
public class EmptyDataSource implements DataSource {

    @Override
    public boolean has(String member) {
        return false;
    }

    @Override
    public Object get(String member) throws InvalidPlaceholder {
        return "";
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public String[] getMembers() {
        return new String[0];
    }

}
