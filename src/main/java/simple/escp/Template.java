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

package simple.escp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  <code>Template</code> represent a template used for printing.  A single <code>Template</code>
 *  can be printed many times with different values.
 */
public abstract class Template {

    private final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9]+)\\}");

    protected Map<String, Placeholder> placeholders = new HashMap<>();
    protected PageFormat pageFormat = new PageFormat();

    /**
     * Get declared placeholders in this template.  This method should be called after parsing template
     * or otherwise it will always return an empty <code>Map</code>.
     *
     * @return declared placeholders or an empty <code>Map</code> if none are found.
     */
    public Map<String, Placeholder> getPlaceholders() {
        return this.placeholders;
    }

    /**
     * Find the name of placeholder, such as <code>${name}</code>, in a string.
     *
     * @param text search placeholder definition in this string.
     * @return <code>List</code> that contains one or more placeholder's name.  If no placeholder is
     *         declared in the string, this method will return an empty <code>List</code>.
     */
    public List<String> findPlaceholderIn(String text) {
        List<String> results = new ArrayList<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String match = matcher.group(1);
            results.add(match);
        }
        return results;
    }

    /**
     * Find if a placeholder name is declared in this template.
     *
     * @param name placehoder name to search for.
     * @return <code>true</code> if placeholder name is declared or <code>false</code> if otherwise.
     */
    public boolean hasPlaceholder(String name) {
        return this.placeholders.containsKey(name);
    }

    /**
     * Parse the template into a text.  This is usually executed only once.
     *
     * @return a parsed <code>String</code>.
     */
    public abstract String parse();

    /**
     * Fill this template with data from a <code>Map</code>.  This template must be parsed
     * if it hasn't been parsed previously.
     *
     * @param map contains data for this template.
     * @return text that will be printed and may contains ESC/P code.
     */
    public String fill(Map map) {
        String parsedText = parse();
        StringBuffer result = new StringBuffer();

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(parsedText);
        while (matcher.find()) {
            Placeholder placeholder = getPlaceholders().get(matcher.group(1));
            Object value = map.get(placeholder.getName());
            matcher.appendReplacement(result, placeholder.forValue(value));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Fill this template with data from an <code>Object</code>.  Every getter in the object
     * will be treated as a value.  For example, <code>getName()</code> will return a value
     * for <code>name</code> placeholder.
     *
     * @param object contains data for this template.
     * @return text that will be printed and may contains ESC/P.
     */
    public String fill(Object object) {
        return null;
    }


}
