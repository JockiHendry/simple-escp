package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import java.util.regex.Matcher;

/**
 * A built-in function that will return the global line number.  To use this function, use the following
 * expression: <code>%{GLOBAL_LINE_NO}</code>.  Unlike <code>%{LINE_NO}</code> that will reset line number when
 * page changes, this function will continue increase line number until the end of report.
 */
public class GlobalLineNoFunction extends Function {

    /**
     * Create a new instance of this function.
     */
    public GlobalLineNoFunction() {
        super("%\\{\\s*(GLOBAL_LINE_NO)\\s*\\}");
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        return line.getGlobalLineNumber().toString();
    }

    @Override
    public void reset() {
        // Do nothing
    }
}
