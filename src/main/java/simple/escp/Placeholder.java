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

import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;

/**
 *  <code>Placeholder</code> represent a placeholder in template, such as <code>${name}</code>.
 *
 *  <p>Placeholder can also have an optional formatting, such as <code>${salary:currency}</code>.  The following
 *  format options are available on number value: <code>number</code>, <code>integer</code> and
 *  <code>currency</code>.  The following options are available on date value: <code>date_full</code>,
 *  <code>date_long</code>, <code>date_medium</code>, and <code>date_short</code>.
 *
 *  <p>All value types support a number as options that will limit the result of this placeholder if number
 *  of resulting characters more than this number.  For example: <code>${salary:currency:10}</code> will always take
 *  10 characters.  If the actual value is greater than the width, it will be truncated.  If the actual value is
 *  less than the width, spaces will be appended to it.
 *
 *  <p>If placeholders has more than one part separated by semicolon (<code>:</code>), the first part should always
 *  be name of the placeholder.
 */
public class Placeholder {

    private String text;
    private String name;
    private Format format;
    private int width = 0;

    /**
     * Create a new instance of template's placeholder.
     *
     * @param text a string that defines this placeholder.
     */
    public Placeholder(String text) {
        this.text = text;
        parseText(text);
    }

    /**
     * Parse formatter such as <code>number</code>, <code>date_full</code>, etc.
     *
     * @param text part of text for this placeholder.
     */
    private void parseFormatter(String text) {
        if ("number".equals(text)) {
            format = NumberFormat.getNumberInstance();
        } else if ("integer".equals(text)) {
            format = NumberFormat.getIntegerInstance();
        } else if ("currency".equals(text)) {
            format = NumberFormat.getCurrencyInstance();
        } else if ("date_full".equals(text)) {
            format = DateFormat.getDateInstance(DateFormat.FULL);
        } else if ("date_long".equals(text)) {
            format = DateFormat.getDateInstance(DateFormat.LONG);
        } else if ("date_medium".equals(text)) {
            format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        } else if ("date_short".equals(text)) {
            format = DateFormat.getDateInstance(DateFormat.SHORT);
        }
    }

    /**
     * Parse width for a placeholder.  By default, <code>width</code> is 0 and no restriction will be applied.
     *
     * @param text part of text for this placeholder.
     */
    private void parseWidth(String text) {
        try {
            width = Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return;
        }
    }

    /**
     * Parse placeholder text.
     *
     * @param text full text that represent this placeholder.
     */
    private void parseText(String text) {
        if (text.contains(":")) {
            String[] parts = text.split(":", 2);
            this.name = parts[0];
            for (String part: parts[1].split(":")) {
                parseFormatter(part);
                parseWidth(part);
            }
        } else {
            this.name = text;
        }
    }

    /**
     * Get the text of this placeholder.  All placeholder will be identified in template
     * by their text.  For example, placeholder text for <code>${name}</code> is <code>name</code>.
     *
     * @return text of this placeholder.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of this placeholder.
     *
     * @param text the text for this placeholder.
     */
    public void setText(String text) {
        this.text = text;
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
     * Retrieve the maximal width allowed for the value of this placeholder.
     *
     * @return maximal number of characters for this placeholder.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the maximal number of characters for this placeholder.
     *
     * @param width new maximal number of characters (width) for this placeholder, or <code>0</code> if it is
     *              unlimited.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get a <code>Format</code> for this placeholder.
     *
     * @return an instance of <code>Format</code> for this placeholder.  If no <code>format</code> is defined,
     *         this method will return <code>null</code>.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Set a new <code>Format</code> for this placeholder.
     *
     * @param format new format for this placeholder.
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * Get a formatted version, including width limit, of a value.
     *
     * @param value the value passed to this placehoder.
     * @return the formatted value.  If <code>value</code> is formatted, it will be returned as <code>String</code>.
     *         If no formatting is specified for this placeholder, the <code>value</code> will be returned as is.
     */
    public Object getFormatted(Object value) {
        Object result = value;
        if (getFormat() != null) {
            result = getFormat().format(result);
        }
        if (getWidth() > 0) {
            if (result.toString().length() < getWidth()) {
                StringBuilder tmp = new StringBuilder(result.toString());
                int numOfSpaces = getWidth() - result.toString().length() + 1;
                for (int i = 1; i < numOfSpaces; i++) {
                    tmp.append(' ');
                }
                result = tmp.toString();
            } else if (result.toString().length() > getWidth()) {
                result = result.toString().substring(0, getWidth());
            }
        }
        return result;
    }

}
