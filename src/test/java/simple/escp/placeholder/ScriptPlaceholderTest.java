package simple.escp.placeholder;

import org.junit.Test;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static org.junit.Assert.*;

public class ScriptPlaceholderTest {

    @Test
    public void getText() {
        String text = "rate * 0.5 :: number :: 10";
        ScriptPlaceholder placeholder = new ScriptPlaceholder(text);
        assertEquals("rate * 0.5 :: number :: 10", placeholder.getText());
    }

    @Test
    public void getName() {
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::currency").getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::10").getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::10         ").getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5 ::10").getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder(" rate * 0.5 :: 10").getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::currency::20").getScript());
    }

    @Test
    public void getWidth() {
        assertEquals(0, new ScriptPlaceholder("rate * 0.5::currency").getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5::10").getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5::10         ").getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5 ::10").getWidth());
        assertEquals(10, new ScriptPlaceholder(" rate * 0.5 :: 10").getWidth());
        assertEquals(20, new ScriptPlaceholder("rate * 0.5::currency:20").getWidth());
    }

    @Test
    public void getFormat() {
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::number::10").getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::number ::10     ").getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::  number   ::  10").getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::integer").getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::currency").getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_full::20").getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_long").getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_medium").getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_short").getFormat().getClass());
    }

    @Test
    public void formattedValueFormat() {
        assertEquals(NumberFormat.getNumberInstance().format(10000), new ScriptPlaceholder("rate::number").getFormatted(10000));
        assertEquals(NumberFormat.getIntegerInstance().format(10.55), new ScriptPlaceholder("rate::integer").getFormatted(10.55));
        assertEquals(DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()),
                new ScriptPlaceholder("birthDate::date_full").getFormatted(Calendar.getInstance().getTime()));
    }

    @Test
    public void formattedValueWidth() {
        assertEquals("TheSolidSn", new ScriptPlaceholder("name::10").getFormatted("TheSolidSnake"));
        assertEquals("124", new ScriptPlaceholder("result::integer::3").getFormatted(123.55));
        assertEquals("Snack     ", new ScriptPlaceholder("name::10").getFormatted("Snack"));
    }

}
