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
import static simple.escp.util.EscpUtil.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        assertEquals(INIT + "This is the first line" + CRLF + "This is the second line" + CRLF + CRFF + INIT, jsonTemplate.getParsedText());
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
            INIT + EscpUtil.escOnePerEightInchLineSpacing() + "Your id is ${id}, Mr. ${nickname}." + CRLF  + CRFF + INIT,
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
            INIT + EscpUtil.escMasterSelect(EscpUtil.CHARACTER_PITCH.CPI_10) + "Your id is ${id}, Mr. ${nickname}." + CRLF + CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatPageLengthInString() {
        String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"pageLength\": \"10\"," +
                    "\"usePageLengthFromPrinter\": false" +
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
            INIT + EscpUtil.escPageLength(10) + "Your id is ${id}, Mr. ${nickname}." + CRLF + CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatPageLengthInNumber() {
        String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"pageLength\": 10," +
                    "\"usePageLengthFromPrinter\": false" +
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
            INIT + EscpUtil.escPageLength(10) + "Your id is ${id}, Mr. ${nickname}." + CRLF + CRFF + INIT,
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
            INIT + EscpUtil.escRightMargin(25) + "Your id is ${id}, Mr. ${nickname}." + CRLF + CRFF + INIT,
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
            "Your id is ${id}, Mr. ${nickname}." + CRLF + CRFF + INIT,
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
                "Your id is ${id}, Mr. ${nickname}." + CRLF + CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatAutoLineFeed() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"autoLineFeed\": true" +
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
            INIT + "Your id is ${id}, Mr. ${nickname}." + CR + CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void pageFormatAutoFormFeed() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"autoFormFeed\": false" +
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
            INIT + "Your id is ${id}, Mr. ${nickname}." + CRLF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void fromFile() throws IOException {
        File file = Paths.get("src/test/resources/user.json").toFile();
        JsonTemplate jsonTemplate = new JsonTemplate(file);
        String LS = System.getProperty("line.separator");
        assertEquals("{" + LS +
            "    \"pageFormat\": {" + LS +
            "        \"pageLength\": 30," + LS +
            "        \"pageWith\": 20" + LS +
            "    }," + LS +
            "    \"placeholder\": [" + LS +
            "        \"id\", \"nickname\"" + LS +
            "    ]," + LS +
            "    \"template\": [" + LS +
            "        \"User Report\"," + LS +
            "        \"===========\"," + LS +
            "        \"ID    : ${id}\"," + LS +
            "        \"Name  : ${nickname}\"" + LS +
            "    ]" + LS +
            "}", jsonTemplate.getOriginalText());

        Map<String, String> data = new HashMap<>();
        data.put("id", "007");
        data.put("nickname", "The Solid Snake");
        String result = jsonTemplate.fill(data);
        assertEquals(
            EscpUtil.escInitalize() +
            "User Report" + CRLF +
            "===========" + CRLF +
            "ID    : 007" + CRLF +
            "Name  : The Solid Snake" + CRLF +
            CRFF + EscpUtil.escInitalize(),
            result
        );
    }

    @Test
    public void fromURI() throws IOException, URISyntaxException {
        JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/user.json").toURI());
        String LS = System.getProperty("line.separator");
        assertEquals("{" + LS +
                "    \"pageFormat\": {" + LS +
                "        \"pageLength\": 30," + LS +
                "        \"pageWith\": 20" + LS +
                "    }," + LS +
                "    \"placeholder\": [" + LS +
                "        \"id\", \"nickname\"" + LS +
                "    ]," + LS +
                "    \"template\": [" + LS +
                "        \"User Report\"," + LS +
                "        \"===========\"," + LS +
                "        \"ID    : ${id}\"," + LS +
                "        \"Name  : ${nickname}\"" + LS +
                "    ]" + LS +
                "}", jsonTemplate.getOriginalText());

        Map<String, String> data = new HashMap<>();
        data.put("id", "007");
        data.put("nickname", "The Solid Snake");
        String result = jsonTemplate.fill(data);
        assertEquals(
                EscpUtil.escInitalize() +
                        "User Report" + CRLF +
                        "===========" + CRLF +
                        "ID    : 007" + CRLF +
                        "Name  : The Solid Snake" + CRLF +
                        CRFF + EscpUtil.escInitalize(),
                result
        );
    }

}
