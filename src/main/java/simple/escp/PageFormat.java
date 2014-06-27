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

import simple.escp.util.EscpUtil;

/**
 *  <code>PageFormat</code> represent page format section of a template.  It will be used
 *  to generate initialization ESC/P commands such as selecting line spacing and font size
 *  before printing data.
 *
 */
public class PageFormat {

    private LINE_SPACING lineSpacing;

    /**
     * Set vertical line spacing.
     *
     * @param value a String value that can be one of <code>"1/8"</code>, <code>"ONE_PER_EIGHT_INCH"</code>,
     *              <code>"1/6"</code>, or <code>"ONE_PER_SIX_INCH"</code>.
     */
    public void setLineSpacing(String value) {
        if ("1/8".equals(value) || "ONE_PER_EIGHT_INCH".equals(value)) {
            this.lineSpacing = LINE_SPACING.ONE_PER_EIGHT_INCH;
        } else if ("1/6".equals(value) || "ONE_PER_SIX_INCH".equals(value)) {
            this.lineSpacing = LINE_SPACING.ONE_PER_SIX_INCH;
        } else {
            throw new IllegalArgumentException("Invalid value for line spacing: " + value);
        }
    }

    /**
     * Get specified vertical line spacing.
     * @return vertical line spacing, by default, it is <code>LINE_SPACING.ONE_PER_SIX_INCH</code>.
     */
    public LINE_SPACING getLineSpacing() {
        if (lineSpacing == null) {
            return LINE_SPACING.ONE_PER_SIX_INCH;
        } else {
            return lineSpacing;
        }
    }

    /**
     * Build a string that represent ESC/P commands for this page format.
     *
     * @return a string that contains ESC/P commands.
     */
    public String build() {
        StringBuffer result = new StringBuffer();
        result.append(EscpUtil.escInitalize());

        // set line spacing
        if (lineSpacing != null) {
            if (lineSpacing == LINE_SPACING.ONE_PER_EIGHT_INCH) {
                result.append(EscpUtil.escOnePerEightInchLineSpacing());
            } else if (lineSpacing == LINE_SPACING.ONE_PER_SIX_INCH) {
                result.append(EscpUtil.escOnePerSixInchLineSpacing());
            }
        }

        return result.toString();
    }

    /**
     * This enum represents available fixed vertical line spacings.
     */
    public enum LINE_SPACING {
        ONE_PER_EIGHT_INCH, ONE_PER_SIX_INCH
    }

}
