package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.util.EscpUtil;
import java.util.regex.Matcher;

/**
 * A built-in function that will generate ESC/P to toggle double-strike font style.  If current font style is in
 * default font style, <code>%{DOUBLE}</code> will generate ESC/P to switch to double-strike printing.
 * If current font style is already in double strike printing (as a result of previous invocation),
 * <code>%{DOUBLE}</code> will switch to default font style.
 */
public class DoubleStrikeFunction extends Function {

    private boolean doubleStrike;

    /**
     * Create new instance of this function.
     */
    public DoubleStrikeFunction() {
        super("%\\{\\s*(DOUBLE)\\s*\\}");
        doubleStrike = false;
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String result = doubleStrike ? EscpUtil.escCancelDoubleStrikeFont() : EscpUtil.escSelectDoubleStrikeFont();
        doubleStrike = !doubleStrike;
        return result;
    }
}
