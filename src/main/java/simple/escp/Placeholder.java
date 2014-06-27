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

import simple.escp.exception.InvalidPlaceholder;

/**
 *  <code>Placeholder</code> represent a placeholder in template, such as <code>${name}</code>.
 */
public class Placeholder {

    private String name;

    /**
     * Create a new instance of template's placeholder.
     *
     * @param name a name for this placeholder.
     */
    public Placeholder(String name) {
        this.name = name;
    }

    /**
     * Get the name of this placeholder.  All placeholder will be identified in template
     * by their name.  For example, placeholder name for <code>${name}</code> is <code>name</code>.
     *
     * @return name of this placeholder.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this placeholder.
     *
     * @param name the name for this placeholder.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieve formatted value for this placeholder.
     *
     * @param value the source value that will be used as replacement.
     * @return formatted value that will be used for printing.
     */
    public String forValue(Object value) {
        if (value == null) {
            throw new InvalidPlaceholder("Null value is not supported for " + getName());
        }
        return value.toString();
    }

}
