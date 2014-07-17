package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.util.EscpUtil;
import java.util.regex.Matcher;

/**
 * A built-in function that will generate ESC/P to toggle bold font style.  If current font style is in default
 * font style, <code>%{BOLD}</code> will generate ESC/P to switch to bold font style.  If current font style is
 * already in bold font style (as a result of previous invocation), <code>%{BOLD}</code> will switch to default
 * font style.
 */
public class BoldFunction extends Function {

    private boolean bold;

    /**
     * Create new instance of this function.
     */
    public BoldFunction() {
        super("%\\{\\s*(BOLD)\\s*\\}");
        bold = false;
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String result = bold ? EscpUtil.escCancelBoldFont() : EscpUtil.escSelectBoldFont();
        bold = !bold;
        return result;
    }

    @Override
    public void reset() {
        bold = false;
    }

}
