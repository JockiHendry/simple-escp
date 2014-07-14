package simple.escp.json;

import org.junit.Test;
import simple.escp.fill.FillJob;
import simple.escp.util.EscpUtil;
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

}
