package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import java.util.regex.Matcher;

/**
 *  A built-in function that will return current page number.  To use this function, use the following
 *  expression: <code>%{PAGE_NO}</code>.
 */
public class PageNoFunction extends Function {

    /**
     * Create new instance of this function.
     */
    public PageNoFunction() {
        super("%\\{\\s*(PAGE_NO)\\s*\\}");
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        return String.valueOf(page.getPageNumber());
    }
}
