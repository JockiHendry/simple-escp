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

}
