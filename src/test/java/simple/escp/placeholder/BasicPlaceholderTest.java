/*
 * Copyright 2014 Jocki Hendry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package simple.escp.placeholder;

import org.junit.Test;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

public class BasicPlaceholderTest {

    @Test
    public void getText() {
        String text = "@takeHomePay";
        BasicPlaceholder placeholder = new BasicPlaceholder(text);
        assertEquals("@takeHomePay", placeholder.getText());
    }

    @Test
    public void getName() {
        assertEquals("payment", new BasicPlaceholder("payment:currency").getName());
        assertEquals("payment", new BasicPlaceholder("payment:10").getName());
        assertEquals("payment", new BasicPlaceholder("payment:10         ").getName());
        assertEquals("payment", new BasicPlaceholder("payment :10").getName());
        assertEquals("payment", new BasicPlaceholder(" payment : 10").getName());
        assertEquals("payment", new BasicPlaceholder("payment:currency:20").getName());
    }

    @Test
    public void getWidth() {
        assertEquals(0, new BasicPlaceholder("payment:currency").getWidth());
        assertEquals(10, new BasicPlaceholder("payment:10").getWidth());
        assertEquals(10, new BasicPlaceholder("payment:10         ").getWidth());
        assertEquals(10, new BasicPlaceholder("payment :10").getWidth());
        assertEquals(10, new BasicPlaceholder(" payment : 10").getWidth());
        assertEquals(20, new BasicPlaceholder("payment:currency:20").getWidth());
    }

    @Test
    public void getFormat() {
        assertEquals(DecimalFormat.class, new BasicPlaceholder("payment:number:10").getFormat().getClass());
        assertEquals(DecimalFormat.class, new BasicPlaceholder("payment:number :10     ").getFormat().getClass());
        assertEquals(DecimalFormat.class, new BasicPlaceholder("payment:  number   :  10").getFormat().getClass());
        assertEquals(DecimalFormat.class, new BasicPlaceholder("payment:integer").getFormat().getClass());
        assertEquals(DecimalFormat.class, new BasicPlaceholder("payment:currency").getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new BasicPlaceholder("birthDate:date_full:20").getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new BasicPlaceholder("birthDate:date_long").getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new BasicPlaceholder("birthDate:date_medium").getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new BasicPlaceholder("birthDate:date_short").getFormat().getClass());
    }

    @Test
    public void formattedValueFormat() {
        assertEquals(NumberFormat.getNumberInstance().format(10000), new BasicPlaceholder("payment:number").getFormatted(10000));
        assertEquals(NumberFormat.getIntegerInstance().format(10.55), new BasicPlaceholder("rate:integer").getFormatted(10.55));
        assertEquals(DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime()),
            new BasicPlaceholder("birthDate:date_full").getFormatted(Calendar.getInstance().getTime()));
    }

    @Test
    public void formattedValueWidth() {
        assertEquals("TheSolidSn", new BasicPlaceholder("name:10").getFormatted("TheSolidSnake"));
        assertEquals("124", new BasicPlaceholder("result:integer:3").getFormatted(123.55));
        assertEquals("Snack     ", new BasicPlaceholder("name:10").getFormatted("Snack"));
    }

    public static class Employee {
        private String id;
        private String name;
        private double salary;
        private double taxes;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getSalary() {
            return salary;
        }

        public void setSalary(double salary) {
            this.salary = salary;
        }

        public double getTaxes() {
            return taxes;
        }

        public void setTaxes(double taxes) {
            this.taxes = taxes;
        }

        public double takeHomePay() {
            return salary - (salary * taxes / 100);
        }
    }
}
