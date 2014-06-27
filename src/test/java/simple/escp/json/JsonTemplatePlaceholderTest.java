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
import simple.escp.exception.InvalidPlaceholder;
import static org.junit.Assert.*;

public class JsonTemplatePlaceholderTest {

    private String jsonString;
    private String invalidJsonString;

    @Before
    public void setup() {
        jsonString = "{" +
            "\"placeholder\": [" +
                "{\"name\": \"id\"}," +
                "{\"name\": \"nickname\"}" +
            "]," +
                "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";

        invalidJsonString = "{" +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"foobar\"" +
            "]," +
                "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
    }

    @Test
    public void objectPlaceholders() {
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertTrue(jsonTemplate.getPlaceholders().isEmpty());
        jsonTemplate.parse();

        assertTrue(!jsonTemplate.getPlaceholders().isEmpty());
        assertEquals(2, jsonTemplate.getPlaceholders().size());
        assertEquals("id", jsonTemplate.getPlaceholders().get("id").getName());
        assertEquals("nickname", jsonTemplate.getPlaceholders().get("nickname").getName());
    }

    @Test
    public void stringPlaceholders() {
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertTrue(jsonTemplate.getPlaceholders().isEmpty());
        jsonTemplate.parse();

        assertTrue(!jsonTemplate.getPlaceholders().isEmpty());
        assertEquals(2, jsonTemplate.getPlaceholders().size());
        assertEquals("id", jsonTemplate.getPlaceholders().get("id").getName());
        assertEquals("nickname", jsonTemplate.getPlaceholders().get("nickname").getName());
    }

    @Test(expected = InvalidPlaceholder.class)
    public void invalidPlaceholders() {
        JsonTemplate jsonTemplate = new JsonTemplate(invalidJsonString);
        assertTrue(jsonTemplate.getPlaceholders().isEmpty());
        jsonTemplate.parse();
    }

}
