package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.util.EscpUtil;
import java.util.regex.Matcher;

/**
 * A built-in function that will generate ESC/P to toggle underline.  If the current status of underline printing
 * is off, <code>%{UNDERLINE}</code> will generate ESC/P to switch on underline printing.
 * If underline is already turned on as a result of previous invocation),
 * <code>%{UNDERLINE}</code> will turn off underline printing.
 */
public class UnderlineFunction extends Function {

    private boolean underline;

    /**
     * Create new instance of this function.
     */
    public UnderlineFunction() {
        super("%\\{\\s*(UNDERLINE)\\s*\\}");
        underline = false;
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String result = underline ? EscpUtil.escCancelUnderline() : EscpUtil.escSelectUnderline();
        underline = !underline;
        return result;
    }

    @Override
    public void reset() {
        underline = false;
    }

}
