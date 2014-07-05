package simple.escp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A factory class to create <code>DataSource</code>.
 */
public abstract class DataSources {

    /**
     * Create a new <code>DataSource</code> from an <code>Object</code>.  This method will select the
     * appropriate <code>DataSouce</code> based on the class type of <code>Object</code>.
     *
     * @param object the data source value.
     * @return an implementation of <code>DataSource</code>.
     */
    public static DataSource from(Object object) {
        if (object instanceof Map) {
            return new MapDataSource((Map) object);
        } else {
            return new BeanDataSource(object);
        }
    }

    /**
     * Create an array of <code>DataSource</code> based on an array of <code>Object</code>.  This method will
     * select the appropriate <code>DataSource</code> based on the class type of <code>Object</code>.
     *
     * @param objects an array of data source values.
     * @return an array of implementation of <code>DataSource</code> in order of object's array.
     */
    public static DataSource[] from(Object[] objects) {
        List<DataSource> sources = new ArrayList<>();
        for (Object o : objects) {
            if (o != null) {
                sources.add(from(o));
            }
        }
        return sources.toArray(new DataSource[0]);
    }

    /**
     * Create an array of <code>DataSource</code> from a <code>Map</code> and JavaBean object.
     *
     * @param map the <code>Map</code> that contains value.  This will be the data source with the first priority.
     * @param object the object that contains value.
     * @return an array of implementation of <code>DataSource</code>.
     */
    public static DataSource[] from(Map map, Object object) {
        return from(new Object[] {map, object});
    }

}
