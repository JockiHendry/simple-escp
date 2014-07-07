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

package simple.escp.json;

import org.junit.Before;
import org.junit.Test;
import simple.escp.fill.FillJob;
import simple.escp.data.BeanDataSource;
import simple.escp.data.DataSources;
import simple.escp.data.MapDataSource;
import simple.escp.util.EscpUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import static simple.escp.util.EscpUtil.*;

public class JsonTemplateFillTest {

    private String jsonStringBasic, jsonStringScriptMap, jsonStringScriptBean;
    private final String INIT = EscpUtil.escInitalize();

    @Before
    public void setup() {
        this.jsonStringBasic = "{" +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        this.jsonStringScriptMap = "{" +
            "\"template\": [" +
                "\"Your id is {{ id }}, Mr. {{ nickname }}.\"" +
            "]" +
        "}";
        this.jsonStringScriptBean = "{" +
            "\"template\": [" +
                "\"Your id is {{ bean.id }}, Mr. {{ bean.nickname }}.\"" +
            "]" +
        "}";
    }

    @Test
    public void fillMap() {
        JsonTemplate jsonTemplate = new JsonTemplate(jsonStringBasic);
        Map<String, String> dataSource = new HashMap<>();
        dataSource.put("id", "007");
        dataSource.put("nickname", "Solid Snake");
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(dataSource)).fill());

        JsonTemplate jsonTemplateScriptMap = new JsonTemplate(jsonStringScriptMap);
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplateScriptMap.parse(), new MapDataSource(dataSource)).fill());
    }

    @Test
    public void fillObject() {
        JsonTemplate jsonTemplate = new JsonTemplate(jsonStringBasic);
        Person person = new Person("Solid Snake", null, null);
        person.setId("007");
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new BeanDataSource(person)).fill());

        JsonTemplate jsonTemplateScriptBean = new JsonTemplate(jsonStringScriptBean);
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplateScriptBean.parse(), new BeanDataSource(person)).fill());
    }

    @Test
    public void fillObjectWithMethod() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"Your first name is ${ firstName } and your last name is ${lastName}.\"," +
                "\"I know you, ${@name}!\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Person person = new Person("Snake", "David", "None");
        assertEquals(INIT + "Your first name is David and your last name is None." + CRLF +
            "I know you, David None alias Snake!" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new BeanDataSource(person)).fill());

        String jsonStringScript =
        "{" +
            "\"template\": [" +
                "\"Your first name is {{ firstName }} and your last name is {{ lastName }}.\"," +
                "\"I know you, {{ bean.name() }}!\"" +
            "]" +
        "}";
        jsonTemplate = new JsonTemplate(jsonStringScript);
        assertEquals(INIT + "Your first name is David and your last name is None." + CRLF +
            "I know you, David None alias Snake!" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new BeanDataSource(person)).fill());
    }

    @Test
    public void fillOneTable() throws URISyntaxException, IOException {
        JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table.json").toURI());
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("None", "David", "None"));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
                INIT + escPageLength(3) +
                        "This is detail 1." + CRLF +
                        "David     None                None      " + CRLF +
                        "Solid     Snake               David     " + CRLF + CRFF +
                        "Jocki     Hendry              Snake     " + CRLF +
                        "This is detail 2." + CRLF +
                        CRFF + INIT,
                result
        );
    }

    @Test
    public void fillTwoTable() throws URISyntaxException, IOException {
        JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/multiple_table.json").toURI());
        List<Person> persons1 = new ArrayList<>();
        persons1.add(new Person("None", "David", "None"));
        persons1.add(new Person("David", "Solid", "Snake"));
        persons1.add(new Person("Snake", "Jocki", "Hendry"));
        List<Person> persons2 = new ArrayList<>();
        persons2.add(new Person("FooBar", "Foo", "Bar"));
        persons2.add(new Person("BarFoo", "Bar", "Foo"));
        Map<String, Object> source = new HashMap<>();
        source.put("persons1", persons1);
        source.put("persons2", persons2);
        String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
                INIT + escPageLength(3) +
                        "This is detail 1." + CRLF +
                        "David     None                None      " + CRLF +
                        "Solid     Snake               David     " + CRLF + CRFF +
                        "Jocki     Hendry              Snake     " + CRLF +
                        "This is detail 2." + CRLF +
                        "Foo       Bar                 FooBar    " + CRLF + CRFF +
                        "Bar       Foo                 BarFoo    " + CRLF +
                        "This is detail 3." + CRLF +
                        CRFF + INIT,
                result
        );
    }

    @Test
    public void placeholderFormatting() {
         String jsonString =
        "{" +
            "\"template\": [" +
                "\"Your first name is ${firstName:10} and your last name is ${lastName:5}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, Object> source = new HashMap<>();
        source.put("firstName", "Jocki");
        source.put("lastName", "Hendry");
        String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT +
            "Your first name is Jocki      and your last name is Hendr." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void placeholderFormattingForScript() {
        String jsonString =
        "{" +
            "\"template\": [" +
                "\"Your first name is {{firstName::10}} and your last name is {{lastName::5}}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, Object> source = new HashMap<>();
        source.put("firstName", "Jocki");
        source.put("lastName", "Hendry");
        String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT +
            "Your first name is Jocki      and your last name is Hendr." + CRLF +
            CRFF + INIT,
            result
        );
    }


    public static class Person {
        private String id;
        private String nickname;
        private String firstName;
        private String lastName;

        public Person(String nickname, String firstName, String lastName) {
            this.nickname = nickname;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String name() {
            return firstName + " " + lastName + " alias " + nickname;
        }
    }

}
