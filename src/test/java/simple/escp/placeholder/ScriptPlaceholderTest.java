package simple.escp.placeholder;

import org.junit.Test;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    public void getFormattedNullValue() {
        assertEquals("", new ScriptPlaceholder("name", null).getFormatted(null));
        assertEquals("          ", new ScriptPlaceholder("name::10", null).getFormatted(null));
    }

    @Test
    public void formattedValueSum() {
        List<Integer> data = new ArrayList<>();
        data.add(10);
        data.add(20);
        data.add(30);
        assertEquals(new BigDecimal("60.0"), new BasicPlaceholder("total::sum").getFormatted(data));

        List<BigDecimal> data2 = new ArrayList<>();
        data2.add(new BigDecimal("10.25"));
        data2.add(new BigDecimal("20.75"));
        assertEquals(NumberFormat.getCurrencyInstance().format(31), new BasicPlaceholder("total::sum::currency").getFormatted(data2));
    }

    @Test
    public void formattedValueCount() {
        List<Integer> data = new ArrayList<>();
        data.add(10);
        data.add(20);
        data.add(30);
        assertEquals(3, new BasicPlaceholder("total::count").getFormatted(data));

        List<BigDecimal> data2 = new ArrayList<>();
        data2.add(new BigDecimal("10.25"));
        data2.add(new BigDecimal("20.75"));
        assertEquals(NumberFormat.getCurrencyInstance().format(2), new BasicPlaceholder("total::count::currency").getFormatted(data2));
    }

}
