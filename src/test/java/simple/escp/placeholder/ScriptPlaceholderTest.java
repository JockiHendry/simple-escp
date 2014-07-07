package simple.escp.placeholder;

import org.junit.Test;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import static org.junit.Assert.*;

public class ScriptPlaceholderTest {

    @Test
    public void getText() {
        String text = "rate * 0.5 :: number :: 10";
        ScriptPlaceholder placeholder = new ScriptPlaceholder(text, null);
        assertEquals("rate * 0.5 :: number :: 10", placeholder.getText());
    }

    @Test
    public void getName() {
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::currency", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::10", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::10         ", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5 ::10", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder(" rate * 0.5 :: 10", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::currency::20", null).getScript());
    }

    @Test
    public void getWidth() {
        assertEquals(0, new ScriptPlaceholder("rate * 0.5::currency", null).getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5::10", null).getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5::10         ", null).getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5 ::10", null).getWidth());
        assertEquals(10, new ScriptPlaceholder(" rate * 0.5 :: 10", null).getWidth());
        assertEquals(20, new ScriptPlaceholder("rate * 0.5::currency:20", null).getWidth());
    }

    @Test
    public void getFormat() {
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::number::10", null).getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::number ::10     ", null).getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::  number   ::  10", null).getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::integer", null).getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::currency", null).getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_full::20", null).getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_long", null).getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_medium", null).getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_short", null).getFormat().getClass());
    }

}
