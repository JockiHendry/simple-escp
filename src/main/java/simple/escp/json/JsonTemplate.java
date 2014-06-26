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

import simple.escp.Template;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.Map;
import java.util.logging.Level;
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
 */
public class JsonTemplate implements Template {

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
     * {@inheritDoc}
     */
    public String parse() {
        if (parsedText == null) {
            JsonReader reader = Json.createReader(new StringReader(originalText));
            JsonObject json = reader.readObject();

            // Parse the template text
            JsonArray templateLines = json.getJsonArray("template");
            if (templateLines == null) {
                logger.log(Level.SEVERE, "JSON Template must contains 'template'.");
                throw new IllegalArgumentException("JSON Template must contains 'template'.");
            }
            StringBuffer tmp = new StringBuffer();
            for (JsonValue line: templateLines) {
                if (line instanceof JsonString) {
                    tmp.append(((JsonString) line).getString());
                    tmp.append('\n');
                }
            }
            this.parsedText = tmp.toString();
        }
        return this.parsedText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String fill(Map map) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String fill(Object object) {
        return null;
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
