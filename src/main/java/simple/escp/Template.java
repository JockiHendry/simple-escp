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

import java.util.Map;

/**
 *  <code>Template</code> represent a template used for printing.  A single <code>Template</code>
 *  can be printed many times with different values.
 */
public interface Template {

    /**
     * Parse the template into a text.  This is usually executed only once.
     *
     * @return a parsed <code>String</code>.
     */
    public String parse();

    /**
     * Fill this template with data from a <code>Map</code>.  This template must be parsed
     * if it hasn't been parsed previously.
     *
     * @param map contains data for this template.
     * @return text that will be printed and may contains ESC/P code.
     */
    public String fill(Map map);

    /**
     * Fill this template with data from an <code>Object</code>.  Every getter in the object
     * will be treated as a value.  For example, <code>getName()</code> will return a value
     * for <code>name</code> placeholder.
     *
     * @param object contains data for this template.
     * @return text that will be printed and may contains ESC/P.
     */
    public String fill(Object object);

}
