package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.util.EscpUtil;
import java.util.regex.Matcher;

/**
 * A built-in function that will generate ESC/P to toggle italic font style.  If current font style is in default
 * font style, <code>%{ITALIC}</code> will generate ESC/P to switch to italic font style.  If current font style is
 * already in italic font style (as a result of previous invocation), <code>%{ITALIC}</code> will switch to default
 * font style.
 */
public class ItalicFunction extends Function {

    private boolean italic;

    /**
     * Create new instance of this function.
     */
    public ItalicFunction() {
        super("%\\{\\s*(ITALIC)\\s*\\}");
        italic = false;
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String result = italic ? EscpUtil.escCancelItalicFont() : EscpUtil.escSelectItalicFont();
        italic = !italic;
        return result;
    }

    @Override
    public void reset() {
        italic = false;
    }
}
