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
    private EscpUtil.CHARACTER_PITCH characterPitch;
    private Integer pageLength;
    private Integer pageWidth;
    private Integer leftMargin;
    private Integer rightMargin;

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
     * Set character pitch (cpi).
     *
     * @param value possible value is <code>"5"</code>, <code>"6"</code>, <code>"10"</code>, <code>"12"</code>,
     *              <code>"17"</code>, and <code>"20"</code>.
     */
    public void setCharacterPitch(String value) {
        if ("5".equals(value) || "5 cpi".equals(value)) {
            this.characterPitch = EscpUtil.CHARACTER_PITCH.CPI_5;
        } else if ("6".equals(value) || "6 cpi".equals(value)) {
            this.characterPitch = EscpUtil.CHARACTER_PITCH.CPI_6;
        } else if ("10".equals(value) || "10 cpi".equals(value)) {
            this.characterPitch = EscpUtil.CHARACTER_PITCH.CPI_10;
        } else if ("12".equals(value) || "12 cpi".equals(value)) {
            this.characterPitch = EscpUtil.CHARACTER_PITCH.CPI_12;
        } else if ("17".equals(value) || "17 cpi".equals(value)) {
            this.characterPitch = EscpUtil.CHARACTER_PITCH.CPI_17;
        } else if ("20".equals(value) || "20 cpi".equals(value)) {
            this.characterPitch = EscpUtil.CHARACTER_PITCH.CPI_20;
        } else {
            throw new IllegalArgumentException("Invalid value for character pitch: " + value);
        }
    }

    /**
     * Get specified character pitch (in cpi).
     *
     * @return character pitch, by default, it is <code>CHARACTER_PITCH.CPI_10</code>.
     */
    public EscpUtil.CHARACTER_PITCH getCharacterPitch() {
        return (this.characterPitch != null) ? this.characterPitch : EscpUtil.CHARACTER_PITCH.CPI_10;
    }

    /**
     * Set page length in number of lines.
     *
     * @param pageLength number of lines that count as a page.
     */
    public void setPageLength(Integer pageLength) {
        this.pageLength = pageLength;
    }

    /**
     * Get specified page length in number of lines.
     *
     * @return number of lines for a page.
     */
    public Integer getPageLength() {
        return pageLength;
    }

    /**
     * Set page width in number of characters.  You must make sure that this number is within printable area
     * width.
     *
     * @param pageWidth number of characters measured from the left-most printable column.
     */
    public void setPageWidth(Integer pageWidth) {
        this.pageWidth = pageWidth;
    }

    /**
     * Get specified page width in number of characters.
     *
     * @return number of characters for a line.
     */
    public Integer getPageWidth() {
        return pageWidth;
    }

    /**
     * Set left margin.
     *
     * @param leftMargin number of characters measured from the left-most printable column.
     */
    public void setLeftMargin(Integer leftMargin) {
        this.leftMargin = leftMargin;
    }

    /**
     * Get specified left margin.
     *
     * @return left margin in number of characters measured from the left-most printable column.
     */
    public Integer getLeftMargin() {
        return leftMargin;
    }

    /**
     *  Set right margin.  You <strong>must</strong> set page width if you want to set right margin because
     *  right margin will be calculated based on page width.
     *
     * @param rightMargin number of characters before reaching page width.
     */
    public void setRightMargin(Integer rightMargin) {
        this.rightMargin = rightMargin;
    }

    /**
     * Get specified right margin.
     *
     * @return right margin in number of characters before reaching page width.
     */
    public Integer getRightMargin() {
        return rightMargin;
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

        // set character pitch
        if (characterPitch != null) {
            result.append(EscpUtil.escMasterSelect(characterPitch));
        }

        // set page length
        if (pageLength != null) {
            result.append(EscpUtil.escPageLength(pageLength));
        }

        // set page width
        if ((pageWidth != null) && (rightMargin == null)) {
            // Use right margin because ESC/P doesn't have page width setting.
            result.append(EscpUtil.escRightMargin(pageWidth));
        }

        // set left margin
        if (leftMargin != null) {
            result.append(EscpUtil.escLeftMargin(leftMargin));
        }

        // set right margin
        if (rightMargin != null) {
            if (pageWidth == null) {
                throw new UnsupportedOperationException("Can't set right margin if page width is not specified.");
            }
            result.append(EscpUtil.escRightMargin(pageWidth - rightMargin));
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
