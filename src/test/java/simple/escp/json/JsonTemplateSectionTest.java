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
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.dom.TableColumn;
import simple.escp.dom.line.TableLine;
import simple.escp.data.MapDataSource;
import simple.escp.fill.FillJob;
import simple.escp.util.EscpUtil;
import java.util.HashMap;
import java.util.Map;
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
            "\"template\": {" +
                "\"detail\": [\"Your id is ${id}\",  \"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Your id is 007" + CRLF + "Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseFirstPage() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
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
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "This should appear in first page only" + CRLF + CRFF +
            "Line1" + CRLF +
            "Line2" + CRLF +
            "Line3" + CRLF +  CRFF +
            "Your id is 007" + CRLF +
            "Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseLastPage() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
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
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF+ "Line3" + CRLF + CRFF +
            "Your id is 007" + CRLF + "Mr. Snake." + CRLF + CRFF +
            "This should appear in last page only" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source))  .fill()
        );
    }

    @Test
    public void parseHeader() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
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
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT +
            "This is header." + CRLF + "Line1" + CRLF + "Line2" + CRLF + CRFF +
            "This is header." + CRLF + "Line3" + CRLF +  "Your id is 007" + CRLF + CRFF +
            "This is header." + CRLF + "Mr. Snake." + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseFirstPageAndHeader() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
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
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "This is first page only." + CRLF + CRFF +
            "This is header." + CRLF + "Line1" + CRLF + "Line2" + CRLF + CRFF +
            "This is header." + CRLF + "Line3" + CRLF+ "Your id is 007" + CRLF + CRFF +
            "This is header." + CRLF + "Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseFooter() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
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
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT +
            "Line1" + CRLF + "Line2" + CRLF + "This is footer."  + CRLF + CRFF +
            "Line3" + CRLF +  "Your id is 007" + CRLF + "This is footer." + CRLF + CRFF +
            "Mr. Snake." + CRLF + "This is footer." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseLastPageAndFooter() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
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
        Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF + "This is footer." + CRLF + CRFF +
            "Line3" + CRLF + "Your id is 007" + CRLF + "This is footer." + CRLF + CRFF +
            "Mr. Snake." + CRLF + "This is footer." + CRLF + CRFF +
            "This is last page only." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parsePageNumber() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"detail\": [" +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{PAGE_NO}\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + "Page 1" + CRLF + "Page 1" + CRLF + "Page 1" + CRLF + CRFF +
            "Page 2" + CRLF + "Page 2" + CRLF + "Page 2" + CRLF + CRFF +
            "Page 3" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void parsePageNumberWithHeader() {
        String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"Halaman %{PAGE_NO}\"]," +
                "\"detail\": [" +
                    "\"Detail 2\"," +
                    "\"Detail 3\"," +
                    "\"Detail 4\"," +
                    "\"Detail 5\"]" +
            "}" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT + "Halaman 1" + CRLF + "Detail 2" + CRLF + "Detail 3" + CRLF + CRFF +
            "Halaman 2" + CRLF + "Detail 4" + CRLF + "Detail 5" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

}
