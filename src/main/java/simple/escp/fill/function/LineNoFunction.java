package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import java.util.regex.Matcher;

/**
 * A built-in function that will return the current line number.  To use this function, use the following
 * expression: <code>%{LINE_NO}</code>.
 */
public class LineNoFunction extends Function {

    /**
     * Create a new instance of this function.
     */
    public LineNoFunction() {
        super("%\\{\\s*(LINE_NO)\\s*\\}");
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        return line.getLineNumber().toString();
    }

    @Override
    public void reset() {
        // Do nothing
    }
}
