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

import static org.junit.Assert.*;
import org.junit.Test;
import simple.escp.util.EscpUtil;

import java.util.List;

public class JsonTemplateBasicTest {

    private final String INIT = EscpUtil.escInitalize();

    @Test
    public void parseString() {
        String jsonString = "{\"template\": [" +
            "\"This is the first line\"," +
            "\"This is the second line\"" +
            "]}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        jsonTemplate.parse();
        assertEquals(jsonString, jsonTemplate.getOriginalText());
        assertEquals(INIT + "This is the first line\nThis is the second line\n" + INIT, jsonTemplate.getParsedText());
    }

    @Test
    public void findPlaceholderName() {
        String jsonString =
            "{" +
                "\"placeholder\": [" +
                    "\"id\"," +
                    "\"nickname\"" +
                "]," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);

        List<String> results = jsonTemplate.findPlaceholderIn("Your id is ${id}");
        assertEquals(1, results.size());
        assertEquals("id", results.get(0));

        results = jsonTemplate.findPlaceholderIn("Your id is ${id} and your name is ${name}");
        assertEquals(2, results.size());
        assertTrue(results.contains("id"));
        assertTrue(results.contains("name"));
    }

    @Test
    public void pageFormatLineSpacing() {
        String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"lineSpacing\": \"1/8\"" +
                "}," +
                "\"placeholder\": [" +
                    "\"id\"," +
                    "\"nickname\"" +
                "]," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + EscpUtil.escOnePerEightInchLineSpacing() + "Your id is ${id}, Mr. ${nickname}.\n" + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatCharacterPitch() {
        String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"characterPitch\": \"10\"" +
                "}," +
                "\"placeholder\": [" +
                    "\"id\"," +
                    "\"nickname\"" +
                "]," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + EscpUtil.escMasterSelect(EscpUtil.CHARACTER_PITCH.CPI_10) + "Your id is ${id}, Mr. ${nickname}.\n" + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatPageLengthInString() {
        String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"pageLength\": \"10\"" +
                    "}," +
                "\"placeholder\": [" +
                    "\"id\"," +
                    "\"nickname\"" +
                "]," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + EscpUtil.escPageLength(10) + "Your id is ${id}, Mr. ${nickname}.\n" + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatPageLengthInNumber() {
        String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"pageLength\": 10" +
                "}," +
                "\"placeholder\": [" +
                    "\"id\"," +
                    "\"nickname\"" +
                "]," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + EscpUtil.escPageLength(10) + "Your id is ${id}, Mr. ${nickname}.\n" + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatPageWidth() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"25\"" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + EscpUtil.escRightMargin(25) + "Your id is ${id}, Mr. ${nickname}.\n" + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatLeftAndRightAndBottomMargin() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"30\"," +
                "\"leftMargin\": \"5\"," +
                "\"rightMargin\": \"3\"," +
                "\"bottomMargin\": \"70\"" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + EscpUtil.escLeftMargin(5) + EscpUtil.escRightMargin(27) + EscpUtil.escBottomMargin(70) +
            "Your id is ${id}, Mr. ${nickname}.\n" + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatTypeface() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"typeface\": \"sans-serif\"" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + EscpUtil.escSelectTypeface(EscpUtil.TYPEFACE.SANS_SERIF) +
                "Your id is ${id}, Mr. ${nickname}.\n" + INIT,
            jsonTemplate.parse()
        );
    }

}
