package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import java.util.regex.Matcher;

/**
 *  A built-in function that will return character based on ASCII number.  To use this function, use the following
 *  expression: <code>%{[number]}</code>.
 *
 *  <p>Examples:
 *
 *  <p>To print character for ASCII code 127, use: <code>%{127}</code>.
 */
public class AsciiFunction extends Function {

    public static final int REPEAT_VALUE_GROUP = 3;

    /**
     * Create new instance of this function.
     */
    public AsciiFunction() {
        super("%\\{\\s*(\\d+)\\s*(R\\s*(\\d+))?\\s*\\}");
    }
    
    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String ch = Character.toString((char) Integer.valueOf(matcher.group(1)).intValue());
        StringBuffer result = new StringBuffer(ch);
        if (matcher.group(REPEAT_VALUE_GROUP) != null) {
            int count = Integer.valueOf(matcher.group(REPEAT_VALUE_GROUP));
            for (int i = 1; i < count; i++) {
                result.append(ch);
            }
        }
        return result.toString();
    }

    @Override
    public void reset() {
        // Do nothing.
    }
}
