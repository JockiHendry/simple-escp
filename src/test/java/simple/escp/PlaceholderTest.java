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

package simple.escp;

import org.junit.Before;
import org.junit.Test;
import simple.escp.Placeholder;
import simple.escp.exception.InvalidPlaceholder;
import simple.escp.json.JsonTemplate;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class PlaceholderTest {

    @Test(expected = InvalidPlaceholder.class)
    public void invalidPlaceholders() {
        Map<String, String> data = new HashMap<>();
        data.put("id", "007");
        Placeholder placeholder = new Placeholder("name", data, null);
        placeholder.value();
    }

    @Test
    public void executeObjectMethod() {
        String text = "@takeHomePay";
        Employee emp = new Employee();
        emp.setName("Snake");
        emp.setSalary(1000);
        emp.setTaxes(2.5);
        Placeholder placeholder = new Placeholder(text, null, emp);
        assertEquals("975.0", placeholder.value());
    }

    @Test
    public void valueAsString() {
        Employee emp = new Employee();
        emp.setName("Snake");
        emp.setSalary(1000);
        emp.setTaxes(2.5);
        Placeholder placeholder = new Placeholder("salary", null, emp);
        assertEquals("1000.0", placeholder.value());
    }

    @Test
    public void valueAsObject() {
        Employee emp = new Employee();
        emp.setName("Snake");
        emp.setSalary(1000);
        emp.setTaxes(2.5);
        Placeholder placeholder = new Placeholder("salary", null, emp);
        assertEquals(1000.0, placeholder.valueAsObject());
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
