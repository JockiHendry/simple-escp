package simple.escp.json;

import org.junit.Test;
import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.fill.FillJob;
import simple.escp.fill.function.Function;
import simple.escp.util.EscpUtil;
import java.util.regex.Matcher;
import static simple.escp.util.EscpUtil.*;
import static org.junit.Assert.*;

public class FunctionTest {

    private final String INIT = EscpUtil.escInitalize();

    @Test
    public void pageNo() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"detail\": [" +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{ PAGE_NO}\"," +
                    "\"Page %{PAGE_NO }\"," +
                    "\"Page %{ PAGE_NO }\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{   PAGE_NO   }\"]" +
                "}" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Page 1" + CRLF + "Page 1" + CRLF + "Page 1" + CRLF + CRFF +
            "Page 2" + CRLF + "Page 2" + CRLF + "Page 2" + CRLF + CRFF +
            "Page 3" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void pageNoWithHeader() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"Halaman %{PAGE_NO}\"]," +
                "\"detail\": [" +
                "\"Detail 2\"," +
                "\"Detail 3\"," +
                "\"Detail 4\"," +
                "\"Detail 5\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Halaman 1" + CRLF + "Detail 2" + CRLF + "Detail 3" + CRLF + CRFF +
            "Halaman 2" + CRLF + "Detail 4" + CRLF + "Detail 5" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void ascii() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"detail\": [" +
                    "\"Result: %{65}%{66}%{67}\"," +
                    "\"Result: %{176}%{177}%{178}\"]" +
                "}" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Result: ABC" + CRLF +
            "Result: " + (char) 176 + (char) 177 + (char) 178 + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void asciiWithRepeat() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"detail\": [" +
                    "\"Result: %{177 R10}\"," +
                    "\"Result: %{176 R 5}\"]" +
                "}" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Result: " + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + CRLF +
            "Result: " + (char) 176 + (char) 176 + (char) 176 + (char) 176 + (char) 176 + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void bold() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"%{BOLD}This is bold%{BOLD}\"," +
                "\"This is normal\"" +
            "]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            escSelectBoldFont() + "This is bold" + escCancelBoldFont() + CRLF +
            "This is normal" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void italic() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"%{ITALIC}This is italic%{ITALIC}\"," +
                "\"This is normal\"" +
            "]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            escSelectItalicFont() + "This is italic" + escCancelItalicFont() + CRLF +
            "This is normal" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void doubleStrike() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"%{DOUBLE}This is double-strike%{DOUBLE}\"," +
                "\"This is normal\"" +
            "]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            escSelectDoubleStrikeFont() + "This is double-strike" + escCancelDoubleStrikeFont() + CRLF +
            "This is normal" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void underline() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"%{UNDERLINE}This is underline%{UNDERLINE}\"," +
                "\"This is normal\"" +
            "]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            escSelectUnderline() + "This is underline" + escCancelUnderline() + CRLF +
            "This is normal" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void superscript() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"This is normal%{SUPER}This is superscript%{SUPER}\"" +
            "]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "This is normal" + escSelectSuperscript() + "This is superscript" + escCancelSuperscriptOrSubscript() +
            CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void subscript() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"This is normal%{SUB}This is subscript%{SUB}\"" +
            "]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "This is normal" + escSelectSubscript() + "This is subscript" + escCancelSuperscriptOrSubscript() +
            CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void autoIncrement() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_A}\"," +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_A}\"" +
            "]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Result: 1" + CRLF +
            "Result: 1" + CRLF +
            "Result: 2" + CRLF +
            "Result: 3" + CRLF +
            "Result: 2" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void autoIncrementDuplicate() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_A}\"," +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_A}\"" +
            "]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Result: 1" + CRLF +
            "Result: 1" + CRLF +
            "Result: 2" + CRLF +
            "Result: 3" + CRLF +
            "Result: 2" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void lineNo() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"Halaman %{PAGE_NO}\"]," +
                "\"detail\": [" +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Halaman 1" + CRLF + "Line 2" + CRLF + "Line 3" + CRLF + CRFF +
            "Halaman 2" + CRLF + "Line 2" + CRLF + "Line 3" + CRLF + CRFF +
            "Halaman 3" + CRLF + "Line 2" + CRLF + "Line 3" + CRLF + CRFF +
            INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void globalLineNo() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"Halaman %{PAGE_NO}\"]," +
                "\"detail\": [" +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Halaman 1" + CRLF + "Line 2" + CRLF + "Line 3" + CRLF + CRFF +
            "Halaman 2" + CRLF + "Line 5" + CRLF + "Line 6" + CRLF + CRFF +
            "Halaman 3" + CRLF + "Line 8" + CRLF + "Line 9" + CRLF + CRFF +
            INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void customFunction() {
        Function function = new CustomFunction();
        FillJob.addFunction(function);
        String jsonString =
        "{" +
            "\"template\": [\"Result: %{MY_CUSTOM}\"]" +
        "}";

        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(INIT + "Result: MyCustomResult" + CRLF + CRFF + INIT, new FillJob(jsonTemplate.parse()).fill());

        FillJob.removeFunction(function);
        assertEquals(INIT + "Result: %{MY_CUSTOM}" + CRLF + CRFF + INIT, new FillJob(jsonTemplate.parse()).fill());
    }

    private static class CustomFunction extends Function {

        public CustomFunction() {
            super("%\\{\\s*(MY_CUSTOM)\\s*\\}");
        }
        @Override
        public String process(Matcher matcher, Report report, Page page, Line line) {
            return "MyCustomResult";
        }

        @Override
        public void reset() {
            // do nothing
        }
    }

}
