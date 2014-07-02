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

import org.junit.Test;
import simple.escp.util.EscpUtil;
import static org.junit.Assert.assertEquals;
import static simple.escp.util.EscpUtil.CRFF;
import static simple.escp.util.EscpUtil.CRLF;

public class JsonTemplateSectionTest {

    private final String INIT = EscpUtil.escInitalize();

    @Test(expected = IllegalArgumentException.class)
    public void parseNoPageLength() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"autoFormFeed\": false" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": {" +
                "\"detail\": [\"Your id is ${id}\",  \"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        jsonTemplate.parse();
    }

    @Test
    public void parseDetail() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 10" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": {" +
                "\"detail\": [\"Your id is ${id}\",  \"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + "Your id is ${id}" + CRLF + "Mr. ${nickname}." + CRLF + CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void parseFirstPage() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": {" +
                "\"firstPage\": [\"This should appear in first page only\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + "This should appear in first page only" + CRLF + "Line1" + CRLF +
                   "Line2" + CRLF + CRFF + "Line3" + CRLF +  "Your id is ${id}" + CRLF + "Mr. ${nickname}." + CRLF + CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void parseLastPage() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": {" +
                "\"lastPage\": [\"This should appear in last page only\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF+ "Line3" + CRLF + CRFF +
            "Your id is ${id}" + CRLF + "Mr. ${nickname}." + CRLF +
            "This should appear in last page only" + CRLF + CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void parseHeader() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": {" +
                "\"header\": [\"This is header.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "This is header." + CRLF + "Line1" + CRLF + "Line2" + CRLF + CRFF +
            "This is header." + CRLF + "Line3" + CRLF +  "Your id is ${id}" + CRLF + CRFF +
            "This is header." + CRLF + "Mr. ${nickname}." + CRLF +
            CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void parseFirstPageAndHeader() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": {" +
                "\"firstPage\": [\"This is first page only.\"]," +
                "\"header\": [\"This is header.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + "This is first page only." + CRLF + "This is header." + CRLF + "Line1" + CRLF + CRFF +
            "This is header." + CRLF + "Line2" + CRLF+ "Line3" + CRLF + CRFF +
            "This is header." + CRLF + "Your id is ${id}" + CRLF + "Mr. ${nickname}." + CRLF +
            CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void parseFooter() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": {" +
                "\"footer\": [\"This is footer.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Line1" + CRLF + "Line2" + CRLF + "This is footer."  + CRLF + CRFF +
            "Line3" + CRLF +  "Your id is ${id}" + CRLF + "This is footer." + CRLF + CRFF +
            "Mr. ${nickname}." + CRLF + CRLF + "This is footer." + CRLF +
            CRFF + INIT,
            jsonTemplate.parse()
        );
    }

    @Test
    public void parseLastPageAndFooter() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": {" +
                "\"lastPage\": [\"This is last page only.\"]," +
                "\"footer\": [\"This is footer.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF + "This is footer." + CRLF + CRFF +
            "Line3" + CRLF + "Your id is ${id}" + CRLF + "This is footer." + CRLF + CRFF +
            "Mr. ${nickname}." + CRLF + CRLF + "This is footer." + CRLF + CRFF +
            "This is last page only." + CRLF + CRFF + INIT,
            jsonTemplate.parse()
        );
    }


}
