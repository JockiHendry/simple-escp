package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.util.EscpUtil;
import java.util.regex.Matcher;

/**
 * A built-in function that will generate ESC/P to toggle superscript printing.
 * If the current status of superscript printing is off, <code>%{SUPER}</code> will generate ESC/P to switch on
 * superscript printing.  If superscript printing is already turned on (as a result of previous invocation),
 * <code>%{SUPER}</code> will turn off superscript printing.  It will also turn off subscript printing.
 */
public class SuperscriptFunction extends Function {

    private boolean superscript;

    /**
     * Create new instance of this function.
     */
    public SuperscriptFunction() {
        super("%\\{\\s*(SUPER)\\s*\\}");
        superscript = false;
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String result = superscript ? EscpUtil.escCancelSuperscriptOrSubscript() : EscpUtil.escSelectSuperscript();
        superscript = !superscript;
        return result;
    }

}
