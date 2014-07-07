package simple.escp.fill;

import simple.escp.TableColumn;
import simple.escp.TableLine;
import simple.escp.data.DataSources;
import simple.escp.placeholder.BasicPlaceholder;
import simple.escp.placeholder.Placeholder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <code>TableFillJob</code> represent the process of filling a <code>TableLine</code> with its source in form
 * of a <code>Collection</code>.
 */
public class TableFillJob {

    protected TableLine tableLine;
    protected Collection source;

    /**
     * Create a new instance of <code>TableFillJob</code>.
     *
     * @param tableLine the <code>TableLine</code> that need to be filled.
     * @param source each entry of this <code>Collection</code> will represent a line in the table.
     */
    public TableFillJob(TableLine tableLine, Collection source) {
        this.tableLine = tableLine;
        this.source = source;
    }

    /**
     * Execute the <code>TableFillJob</code>.
     *
     * @return a <code>String</code> that may contains ESC/P commands and can be printed.
     */
    public List<String> fill() {
        List<String> result = new ArrayList<>();
        // Save all placeholders first.
        Placeholder[] placeholders = new BasicPlaceholder[tableLine.getNumberOfColumns()];
        for (int i = 0; i < tableLine.getNumberOfColumns(); i++) {
            placeholders[i] = new BasicPlaceholder(tableLine.getColumnAt(i + 1).getText());
        }
        for (Object entry: source) {
            StringBuffer text = new StringBuffer();
            for (int i = 0; i < tableLine.getNumberOfColumns(); i++) {
                TableColumn column = tableLine.getColumnAt(i + 1);
                Object value = placeholders[i].getValue(DataSources.from(new Object[]{entry}));
                if ((value instanceof Integer) || (value instanceof Long)) {
                    text.append(String.format("%" + column.getWidth() + "d", value));
                } else if ((value instanceof Float) || (value instanceof  Double)) {
                    text.append(String.format("%" + column.getWidth() + ".1f", value));
                } else {
                    text.append(String.format("%-" + column.getWidth() + "s", value.toString()));
                }

            }
            result.add(text.toString());
        }
        return result;
    }
}
