package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.util.EscpUtil;
import java.util.regex.Matcher;

/**
 * A built-in function that will generate ESC/P to toggle subscript printing.
 * If the current status of subscript printing is off, <code>%{SUB}</code> will generate ESC/P to switch on
 * subscript printing.  If subscript printing is already turned on (as a result of previous invocation),
 * <code>%{SUB}</code> will turn off subscript printing.  It will also turn off superscript printing.
 */
public class SubscriptFunction extends Function {

    private boolean subscript;

    /**
     * Create new instance of this function.
     */
    public SubscriptFunction() {
        super("%\\{\\s*(SUB)\\s*\\}");
        subscript = false;
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String result = subscript ? EscpUtil.escCancelSuperscriptOrSubscript() : EscpUtil.escSelectSubscript();
        subscript = !subscript;
        return result;
    }

    @Override
    public void reset() {
        subscript = false;
    }

}
