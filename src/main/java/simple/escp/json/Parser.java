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

import simple.escp.PageFormat;
import simple.escp.util.EscpUtil;
import javax.json.JsonArray;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class for parsing.
 */
public class Parser {

    public static final Pattern FUNCTION_PATTERN = Pattern.compile("%\\{([a-zA-Z0-9_]+)\\}");

    private StringBuffer result;
    private int lineNumber;
    private int pageNumber;
    private int pageLength;
    private int pageBreak;
    private PageFormat pageFormat;
    private JsonArray firstPage;
    private JsonArray lastPage;
    private JsonArray header;
    private JsonArray footer;
    private JsonArray detail;

    /**
     * Create a new instance of this class.
     *
     * @param pageFormat a <code>PageFormat</code>.
     */
    public Parser(PageFormat pageFormat) {
        this.pageFormat = pageFormat;
        if (pageFormat.getPageLength() != null) {
            this.pageLength = pageFormat.getPageLength();
            this.pageBreak = this.pageLength;
        } else {
            this.pageLength = 0;
            this.pageBreak = 0;
        }
        this.lineNumber = 1;
        this.pageNumber = 1;
    }

    /**
     * Set the "firstPage" section.
     *
     * @param firstPage a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setFirstPage(JsonArray firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * Set the "header" section.
     *
     * @param header a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setHeader(JsonArray header) {
        if (pageLength == 0) {
            throw new IllegalArgumentException("Can't use 'header' if 'pageLength' is 0.");
        }
        if (header.size() >= pageLength) {
            throw new IllegalArgumentException("Number of lines of 'header' (" + header.size() +
                    ") can't be greater than 'pageLength' (" + pageLength + ")");
        }
        this.header = header;
    }

    /**
     * Set the "footer" section.
     *
     * @param footer a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setFooter(JsonArray footer) {
        if (pageLength == 0) {
            throw new IllegalArgumentException("Can't use 'footer' if 'pageLength' is 0.");
        }
        if (footer.size() >= pageLength) {
            throw new IllegalArgumentException("Number of lines of 'footer' (" + footer.size() +
                    ") can't be greater than 'pageLength' (" + pageLength + ")");
        }
        this.pageBreak = pageLength - footer.size();
        this.footer = footer;
    }

    /**
     * Set the "lastPage" section.
     *
     * @param lastPage a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setLastPage(JsonArray lastPage) {
        this.lastPage = lastPage;
    }

    /**
     * Set the "detail" section.  If this method is called more than once, it will append the
     * <code>JsonArray</code> to previous detail.
     *
     * @param detail a <code>JsonArray</code>.
     */
    public void setDetail(JsonArray detail) {
        if (this.detail == null) {
            this.detail = detail;
        } else {
            this.detail.addAll(detail);
        }
    }

    /**
     * A helper method that will parse built-in function in format of <code>%{...}</code> such as
     * <code>%{PAGE_NO}</code> and replace it with actual value.
     *
     * @param text the string that will be parsed.
     * @return the result in which functions are replaced by values.
     */
    private String parseFunction(String text) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = FUNCTION_PATTERN.matcher(text);
        while (matcher.find()) {
            String function = matcher.group(1);

            // PAGE_NO
            if ("PAGE_NO".equals(function)) {
                matcher.appendReplacement(result, String.valueOf(pageNumber));
            }

            // LINE_NO
            if ("LINE_NO".equals(function)) {
                matcher.appendReplacement(result, String.valueOf(lineNumber));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * A helper method to parse <code>JsonArray</code>.
     *
     * @param detail the <code>JsonArray</code> that will be parsed.
     * @param basic if <code>true</code>, this method will work in basic mode and ignore <code>"header"</code> or
     *              <code>"footer"</code> section.
     */
    private void parseHelper(JsonArray detail, boolean basic) {
        boolean pageFooterDisplayed = false;

        for (JsonValue line : detail) {

            // print header if necessary
            if ((header != null) && (lineNumber == 1) && !basic) {
                parseHelper(header, true);
            }

            // parse line
            if (line instanceof JsonString) {
                String text = parseFunction(((JsonString) line).getString());
                result.append(text);
                result.append(pageFormat.isAutoLineFeed() ? EscpUtil.CR : EscpUtil.CRLF);
                if ((pageLength > 0) && (lineNumber == pageBreak)) {
                    if ((footer != null) && !basic) {
                        parseHelper(footer, true);
                        pageFooterDisplayed = true;
                    } else {
                        result.append(EscpUtil.CRFF);
                    }
                }
            }

            // increase line number
            lineNumber++;
            if (lineNumber > pageLength) {
                lineNumber = 1;
                pageNumber++;
                pageFooterDisplayed = false;
            }
        }

        // check if footer need to be displayed
        if ((lineNumber != 1) && (footer != null) && !pageFooterDisplayed &&  !basic) {
            while (lineNumber++ < pageLength) {
                result.append(EscpUtil.CRLF);
            }
            parseHelper(footer, true);
            result.append(EscpUtil.CRFF);
        }
    }

    /**
     * Before calling this method, don't forget to call setters such as <code>setFirstPage()</code>,
     * <code>setLastPage()</code>, <code>setDetail()</code>, etc.  The parse result from this method
     * can also be obtained later by calling <code>getResult()</code>.
     *
     * @return result of parsing in <code>String</code>.
     */
    public String parse() {
        result = new StringBuffer();
        lineNumber = 1;
        if (firstPage != null) {
            parseHelper(firstPage, true);
            if ((lineNumber != 1) && (header != null)) {
                parseHelper(header, true);
            }
        }
        if (detail != null) {
            parseHelper(detail, false);
        }
        if (lastPage != null) {
            parseHelper(lastPage, true);
        }
        return getResult();
    }

    /**
     * Get the result of previous <code>parse()</code> invocation.  If <code>parse()</code> hasn't been invoked
     * before, this method will invoke it and return the result.
     *
     * @return result of parsing in <code>String</code>.
     */
    public String getResult() {
        if (result == null) {
            parse();
        }
        return result.toString();
    }

}
