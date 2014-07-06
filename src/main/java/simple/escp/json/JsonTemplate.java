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

import simple.escp.Report;
import simple.escp.Template;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 *  This class represent a template in JSON format.
 *
 *  <p>Example of basic JSON template:
 *
 *  <pre>
 *      {
 *          "template": [
 *              "This is the first line",
 *              "This is the second line"
 *          ]
 *      }
 *  </pre>
 *
 *  <p>Example of JSON template with placeholder:
 *
 *  <pre>
 *      {
 *          "template": [
 *              "Your id is ${id}, Mr. ${nickname}."
 *          ]
 *      }
 *  </pre>
 *
 *  <p>Example of JSON template with page format:
 *
 *  <pre>
 *      {
 *          "pageFormat": {
 *              "lineSpacing": "1/8",
 *              "characterPitch": "17",
 *              "pageWidth": 30,
 *              "leftMargin": 5,
 *              "rightMargin": 5
 *          },
 *          "template": [
 *              "Your id is ${id}.",
 *              "Mr. ${nickname}."
 *          ]
 *      }
 *  </pre>
 *
 *  <p>The value for <code>"template"</code> can be an array or an object.  If it is an object,
 *  <code>"pageLength"</code> in <code>"pageFormat"</code> <strong>must</strong> be defined.
 *
 *  <p>Object for <code>"template"</code> accept of the following keys: <code>"firstPage"</code>.
 *
 *  <p>For example:
 *
 *  <pre>
 *      {
 *          "pageFormat": {
 *              "pageLength": 10,
 *          },
 *          "template": {
 *              "firstPage": [
 *                  "Welcome, ${nickname}. First-page only!"
 *              ],
 *              "detail": [
 *                  "Your id is ${id}.",
 *                  "Mr. ${nickname}."
 *              ]
 *          }
 *      }
 *  </pre>
 */
public class JsonTemplate extends Template {

    private static Logger logger = Logger.getLogger("simple.escp.json.JsonTemplate");

    private String originalText;

    /**
     * Create a new template from a string.
     *
     * @param json JSON formatted string
     */
    public JsonTemplate(String json) {
        this.originalText = json;
    }

    /**
     * Create a new template from a JSON file with UTF-8 character set.
     *
     * @param file the file that will be read.
     * @throws IOException if error occured when reading the file.
     */
    public JsonTemplate(File file) throws IOException {
        this.originalText = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    /**
     * Create a new template from a JSON file with custom character set.
     *
     * @param file the file that will be read.
     * @param charset character set of the file.
     * @throws IOException if error occured when reading the file.
     */
    public JsonTemplate(File file, Charset charset) throws IOException {
        this.originalText = new String(Files.readAllBytes(file.toPath()), charset);
    }

    /**
     * Create a new template from an URI.
     *
     * @param uri the URI to file that will be read.
     * @throws IOException if error occured when reading the file.
     */
    public JsonTemplate(URI uri) throws IOException {
        this(new File(uri));
    }

    /**
     * Create a new template from an <code>InputStream</code> that contains UTF-8 character set.
     *
     * @param inputStream the input stream that will be read.
     * @throws IOException if error occured when reading the input stream.
     */
    public JsonTemplate(InputStream inputStream) throws IOException {
        this(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Create a new template from an <code>InputStream</code>.
     *
     * @param inputStream the input stream that will be read.
     * @param charset character set of the input stream.
     * @throws IOException if error occured when reading the input stream.
     */
    public JsonTemplate(InputStream inputStream, Charset charset) throws IOException {
        InputStreamReader isr = new InputStreamReader(inputStream, charset);
        StringWriter sw = new StringWriter();
        int c;
        while ((c = isr.read()) != -1) {
            sw.write(c);
        }
        this.originalText = sw.getBuffer().toString();
    }

    /**
     * Get number from JSON number or JSON string.
     *
     * @param jsonValue <code>JsonValue</code> that will be parsed.
     * @return a number represented by <code>jsonValue</code>.
     */
    private Integer parseJsonNumber(JsonValue jsonValue) {
        if (jsonValue.getValueType() == JsonValue.ValueType.NUMBER) {
            return ((JsonNumber) jsonValue).intValue();
        } else if (jsonValue.getValueType() == JsonValue.ValueType.STRING) {
            return Integer.valueOf(((JsonString) jsonValue).getString());
        }
        throw new IllegalArgumentException("Can't convert " + jsonValue.toString() + " to number.");
    }

    /**
     * Parse <code>"pageFormat"</code> section from this JSON template.
     *
     * @param json the root JSON of this template.
     */
    private void parsePageFormat(JsonObject json) {
        JsonObject parsedPageFormat = json.getJsonObject("pageFormat");
        if (parsedPageFormat != null) {

            // Line spacing
            if (parsedPageFormat.containsKey("lineSpacing")) {
                pageFormat.setLineSpacing(parsedPageFormat.getString("lineSpacing"));
            }

            // Character pitch
            if (parsedPageFormat.containsKey("characterPitch")) {
                pageFormat.setCharacterPitch(parsedPageFormat.getString("characterPitch"));
            }

            // Page length
            if (parsedPageFormat.containsKey("pageLength")) {
                pageFormat.setPageLength(parseJsonNumber(parsedPageFormat.get("pageLength")));
            }

            // Page width
            if (parsedPageFormat.containsKey("pageWidth")) {
                pageFormat.setPageWidth(parseJsonNumber(parsedPageFormat.get("pageWidth")));
            }

            // Left margin
            if (parsedPageFormat.containsKey("leftMargin")) {
                pageFormat.setLeftMargin(parseJsonNumber(parsedPageFormat.get("leftMargin")));
            }

            // Right margin
            if (parsedPageFormat.containsKey("rightMargin")) {
                pageFormat.setRightMargin(parseJsonNumber(parsedPageFormat.get("rightMargin")));
            }

            // Bottom margin
            if (parsedPageFormat.containsKey("bottomMargin")) {
                pageFormat.setBottomMargin(parseJsonNumber(parsedPageFormat.get("bottomMargin")));
            }

            // Type face
            if (parsedPageFormat.containsKey("typeface")) {
                pageFormat.setTypeface(parsedPageFormat.getString("typeface"));
            }

            // Auto line-feed
            if (parsedPageFormat.containsKey("autoLineFeed")) {
                pageFormat.setAutoLineFeed(parsedPageFormat.getBoolean("autoLineFeed"));
            }

            // Auto form-feed
            if (parsedPageFormat.containsKey("autoFormFeed")) {
                pageFormat.setAutoFormFeed(parsedPageFormat.getBoolean("autoFormFeed"));
            }

            // Use page length from printer
            if (parsedPageFormat.containsKey("usePageLengthFromPrinter")) {
                pageFormat.setUsePrinterPageLength(parsedPageFormat.getBoolean("usePageLengthFromPrinter"));
            }
        }
    }

    /**
     * Parse <code>"template"</code> section from this JSON template.
     * @param json the root JSON of this template.
     * @return result in <code>Pages</code>.
     */
    public Report parseTemplateText(JsonObject json) {
        JsonValue template = json.get("template");
        if (template == null) {
            throw new IllegalArgumentException("JSON Template must contains 'template'.");
        }
        Parser parser = new Parser(getPageFormat());

        if (template.getValueType() == JsonValue.ValueType.ARRAY) {

            parser.setDetail(json.getJsonArray("template"));

        } else if (template.getValueType() == JsonValue.ValueType.OBJECT) {

            if (getPageFormat().getPageLength() == null) {
                throw new IllegalArgumentException("Using object on 'template' require 'pageLength' " +
                        "to be defined in 'pageFormat'.");
            }
            JsonObject templateObject = json.getJsonObject("template");
            if (templateObject.containsKey("firstPage")) {
                parser.setFirstPage(templateObject.getJsonArray("firstPage"));
            }
            if (templateObject.containsKey("header")) {
                parser.setHeader(templateObject.getJsonArray("header"));
            }
            if (templateObject.containsKey("footer")) {
                parser.setFooter(templateObject.getJsonArray("footer"));
            }
            if (templateObject.containsKey("lastPage")) {
                parser.setLastPage(templateObject.getJsonArray("lastPage"));
            }
            if (templateObject.containsKey("detail")) {
                parser.setDetail(templateObject.getJsonArray("detail"));
            }

        } else {
            throw new IllegalArgumentException("Invalid value for 'template'.");
        }

        report = parser.parse();
        return report;
    }

    /**
     * {@inheritDoc}
     */
    public Report parse() {
        if (report == null) {
            try (JsonReader reader = Json.createReader(new StringReader(originalText))) {
                JsonObject json = reader.readObject();
                parsePageFormat(json);
                parseTemplateText(json);
            }
        }
        return report;
    }

    /**
     * Retrieve the text that represent this template before it is parsed.
     *
     * @return JSON string.
     */
    public String getOriginalText() {
        return originalText;
    }

}
