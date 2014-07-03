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
        assertEquals(INIT + "This is the first line" + CRLF + "This is the second line" + CRLF + CRFF + INIT, jsonTemplate.parse().fill(null, null));
    }

    @Test
    public void pageFormatLineSpacing() {
        String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"lineSpacing\": \"1/8\"" +
                "}," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escOnePerEightInchLineSpacing() + "Your id is 007, Mr. Snake." + CRLF  + CRFF + INIT,
            jsonTemplate.parse().fill(source, null)
        );
    }

    @Test
    public void pageFormatCharacterPitch() {
        String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"characterPitch\": \"10\"" +
                "}," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escMasterSelect(EscpUtil.CHARACTER_PITCH.CPI_10) + "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            jsonTemplate.parse().fill(source, null)
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
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escPageLength(10) + "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            jsonTemplate.parse().fill(source, null)
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
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escPageLength(10) + "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            jsonTemplate.parse().fill(source, null)
        );
    }

    @Test
    public void pageFormatPageWidth() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"25\"" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escRightMargin(25) + "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            jsonTemplate.parse().fill(source, null)
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
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escLeftMargin(5) + EscpUtil.escRightMargin(27) + EscpUtil.escBottomMargin(70) +
            "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            jsonTemplate.parse().fill(source, null)
        );
    }

    @Test
    public void pageFormatTypeface() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"typeface\": \"sans-serif\"" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escSelectTypeface(EscpUtil.TYPEFACE.SANS_SERIF) +
                "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            jsonTemplate.parse().fill(source, null)
        );
    }

    @Test
    public void pageFormatAutoLineFeed() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"autoLineFeed\": true" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Your id is 007, Mr. Snake." + CR + CRFF + INIT,
            jsonTemplate.parse().fill(source, null)
        );
    }

    @Test
    public void pageFormatAutoFormFeed() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"autoFormFeed\": false" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Your id is 007, Mr. Snake." + CRLF + INIT,
            jsonTemplate.parse().fill(source, null)
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
        String result = jsonTemplate.fill(data, null);
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
        String result = jsonTemplate.fill(data, null);
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
