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

package simple.escp.placeholder;

import simple.escp.data.DataSource;
import simple.escp.exception.InvalidPlaceholder;
import java.util.logging.Logger;

/**
 *  This class represent a <code>Placeholder</code> that retrieves its value simply from property or method name.
 */
public class BasicPlaceholder extends Placeholder {

    private static final Logger LOG = Logger.getLogger("simple.escp");
    public static final String SEPARATOR = ":";

    private String name;

    /**
     * Create a new instance of basic placeholder.
     *
     * @param text a string that defines this placeholder.
     */
    public BasicPlaceholder(String text) {
        super(text);
        parseText(getText());
    }

    /**
     * Parse placeholder text.
     *
     * @param text full text that represent this placeholder.
     */
    private void parseText(String text) {
        LOG.fine("Parsing [" + text + "]");
        if (text.contains(SEPARATOR)) {
            String[] parts = text.split(SEPARATOR, 2);
            this.name = parts[0].trim();
            parseText(parts[1].split(SEPARATOR));
        } else {
            this.name = text;
        }
    }

    /**
     * Retrieve the name of this placeholder.
     *
     * @return name of this placeholder.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this placeholder.
     *
     * @param name name of this placeholder.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(DataSource[] dataSources) {
        for (DataSource dataSource: dataSources) {
            if (dataSource.has(name)) {
                LOG.fine("Use the following datasource: [" + dataSource + "]");
                return dataSource.get(name);
            }
        }
        LOG.warning("Can't find datasource that has member [" + name + "]");
        throw new InvalidPlaceholder("Can't find data source's member for [" + name + "]");
    }

}
