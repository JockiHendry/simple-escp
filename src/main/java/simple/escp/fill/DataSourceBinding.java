package simple.escp.fill;

import simple.escp.data.BeanDataSource;
import simple.escp.data.DataSource;
import javax.script.SimpleBindings;
import java.util.Arrays;

/**
 *  This class will provide value from <code>DataSource</code> as global variables that
 *  can be read inside script directly.
 */
public class DataSourceBinding extends SimpleBindings {

    private DataSource[] dataSources;

    /**
     * Create a new instance of <code>DataSourceBinding</code>.
     *
     * @param dataSources the data sources that provides value for this binding.
     */
    public DataSourceBinding(DataSource[] dataSources) {
        this.dataSources = Arrays.copyOf(dataSources, dataSources.length);
        init();
    }

    /**
     * Create the binding.
     */
    private void init() {
        put("dataSources", dataSources);

        for (DataSource dataSource : dataSources) {
            // register the first JavaBean as global variable 'bean'
            if ((dataSource instanceof BeanDataSource) && (get("bean") == null)) {
                put("bean", dataSource.getSource());
            }
            for (String key : dataSource.getMembers()) {
                if (get(key) == null) {
                    put(key, dataSource.get(key));
                }
            }
        }
    }

}
