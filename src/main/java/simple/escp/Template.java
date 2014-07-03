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
public abstract class Template {

    protected PageFormat pageFormat = new PageFormat();
    protected Report report;

    /**
     * Retrieve current <code>PageFormat</code> associated with this template.
     *
     * @return an instance of <code>PageFormat</code> for this template.
     */
    public PageFormat getPageFormat() {
        return pageFormat;
    }

    /**
     * Parse the template into a text.  This is usually executed only once and generates result as
     * <code>Pages</code>.
     *
     * @return result of parsing in an instance of <code>Report</code>.
     */
    public abstract Report parse();

    /**
     * Fill this template with data from <code>Map</code> and/or an object.  This template must be parsed if
     * it hasn't been parsed previously.
     *
     * @param map contains data for this template in form of <code>Map</code>.  This argument has a
     *            higher priority than <code>object</code>.  If no data from <code>Map</code> is required,
     *            set this as <code>null</code>.
     * @param object contains data for this template in form of <code>Object</code>.  If no data from object
     *               is required, set this as <code>null</code>.
     * @return text that will be printed and may contains ESC/P code.
     */
    public String fill(Map map, Object object) {
        if (report == null) {
            parse();
        }
        return report.fill(map, object);
    }

}
