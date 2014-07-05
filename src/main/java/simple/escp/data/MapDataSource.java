package simple.escp.data;

import simple.escp.exception.InvalidPlaceholder;

import java.util.Map;

/**
 * A <code>MapDataSource</code> is a <code>DataSource</code> that obtains its value from a <code>Map</code>.
 */
public class MapDataSource implements DataSource {

    private Map source;

    /**
     * Create a new <code>MapDataSource</code>.
     *
     * @param source the <code>Map</code> that contains the value for this <code>DataSource</code>.
     */
    public MapDataSource(Map source) {
        this.source = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean has(String member) {
        return source.containsKey(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String member) throws InvalidPlaceholder {
        if (!has(member)) {
            throw new InvalidPlaceholder("Can't find [" + member + "] in data source.");
        }
        return source.get(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSource() {
        return source;
    }
}
