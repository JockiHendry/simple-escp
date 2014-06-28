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

import simple.escp.Placeholder;
import simple.escp.Template;
import simple.escp.exception.InvalidPlaceholder;
import simple.escp.util.EscpUtil;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.StringReader;
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
 *          "placeholder": [
 *              {"name": "id"},
 *              "nickname"
 *          ],
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
 *              "characterPitch": "17"
 *          },
 *          "placeholder": [
 *              "id",
 *              "nickname"
 *          ],
 *          "template": [
 *              "Your id is ${id}.",
 *              "Mr. ${nickname}."
 *          ]
 *      }
 *  </pre>
 */
public class JsonTemplate extends Template {

    private static Logger logger = Logger.getLogger("simple.escp.json.JsonTemplate");

    private String originalText;
    private String parsedText;

    /**
     * Create a new template from a string.
     *
     * @param json JSON formatted string
     */
    public JsonTemplate(String json) {
        this.originalText = json;
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
                JsonValue pageLength = parsedPageFormat.get("pageLength");
                if (pageLength.getValueType() == JsonValue.ValueType.NUMBER) {
                    pageFormat.setPageLength(((JsonNumber) pageLength).intValue());
                } else if (pageLength.getValueType() == JsonValue.ValueType.STRING) {
                    pageFormat.setPageLength(Integer.valueOf(((JsonString) pageLength).getString()));
                }
            }

        }
    }

    /**
     * Parse <code>"placeholder"</code> section from this JSON template.
     *
     * @param json the root JSON of this template.
     */
    private void parsePlaceholder(JsonObject json) {
        JsonArray placeholdersDefinitions = json.getJsonArray("placeholder");
        if (placeholdersDefinitions != null) {
            for (JsonValue placeholderDefinition : placeholdersDefinitions) {
                if (placeholderDefinition instanceof JsonObject) {

                    JsonObject placeholderObject = (JsonObject) placeholderDefinition;

                    // Process name
                    if (placeholderObject.getJsonString("name") == null) {
                        throw new IllegalArgumentException("Object inside placeholder must has 'name'");
                    }
                    String name = placeholderObject.getString("name");
                    Placeholder placeholder = new Placeholder(name);
                    placeholders.put(name, placeholder);

                } else if (placeholderDefinition instanceof JsonString) {

                    String name = ((JsonString) placeholderDefinition).getString();
                    placeholders.put(name, new Placeholder(name));

                }
            }
        }
    }

    /**
     * Parse <code>"template"</code> section from this JSON template.
     * @param json the root JSON of this template.
     * @return result in <code>String</code>.
     */
    public String parseTemplateText(JsonObject json) {
        StringBuffer tmp = new StringBuffer();
        JsonArray templateLines = json.getJsonArray("template");
        if (templateLines == null) {
            throw new IllegalArgumentException("JSON Template must contains 'template'.");
        }
        for (JsonValue line : templateLines) {
            if (line instanceof JsonString) {

                // Check for undefined placeholder name
                for (String placeHolderName : findPlaceholderIn(((JsonString) line).getString())) {
                    if (!hasPlaceholder(placeHolderName)) {
                        throw new InvalidPlaceholder("[" + placeHolderName + "] is not defined.");
                    }
                }

                tmp.append(((JsonString) line).getString());
                tmp.append('\n');

            }
        }
        return tmp.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String parse() {
        if (parsedText == null) {
            try (JsonReader reader = Json.createReader(new StringReader(originalText))) {

                StringBuffer tmp = new StringBuffer();
                JsonObject json = reader.readObject();

                // Parse page format
                parsePageFormat(json);
                tmp.append(pageFormat.build());

                // Parse placeholders
                parsePlaceholder(json);

                // Parse the template text
                tmp.append(parseTemplateText(json));

                // Add ESC @ at the end
                tmp.append(EscpUtil.escInitalize());

                this.parsedText = tmp.toString();

            }
        }
        return this.parsedText;
    }

    /**
     * Retrieve the text that represent this template before it is parsed.
     *
     * @return JSON string.
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * Retrieve the text that represent this template after it is parsed.
     *
     * @return string parsed from JSON or <code>null</code> if this template hasn't been parsed.
     */
    public String getParsedText() {
        return parsedText;
    }

}
