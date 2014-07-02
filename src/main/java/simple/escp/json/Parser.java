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
import simple.escp.Template;
import simple.escp.util.EscpUtil;
import javax.json.JsonArray;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * A helper class for parsing.
 */
public class Parser {

    private StringBuffer result;
    private int lineNumber;
    //private int pageLength;
    private PageFormat pageFormat;
    private JsonArray firstPage;
    private JsonArray lastPage;
    private JsonArray detail;
    private Set<String> placeholderNames;

    /**
     * Create a new instance of this class.
     *
     * @param pageFormat a <code>PageFormat</code>.
     */
    public Parser(PageFormat pageFormat) {
        this.pageFormat = pageFormat;
//        if (pageFormat.getPageLength() != null) {
//            this.pageLength = pageFormat.getPageLength();
//        } else {
//            this.pageLength = 0;
//        }
        this.lineNumber = 1;
        this.placeholderNames = new HashSet<>();
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
     * Return placeholder names found during parsed.
     *
     * @return a <code>Set</code> that contains string of placeholder name.
     */
    public Set<String> getPlaceholderNames() {
        return placeholderNames;
    }

    /**
     * Find the name of placeholder, such as <code>${name}</code>, in a string.  This method will also stores
     * found placeholders that can be retrieved later by using <code>getPlaceholderNames()</code>.
     *
     * @param text search placeholder definition in this string.
     * @return <code>Set</code> that contains one or more placeholder's name.  If no placeholder is
     *         declared in the string, this method will return an empty <code>Set</code>.
     */
    public Set<String> findPlaceholderIn(String text) {
        Set<String> results = new HashSet<>();
        Matcher matcher = Template.PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String match = matcher.group(1);
            results.add(match);
        }
        placeholderNames.addAll(results);
        return results;
    }

    /**
     * A helper method to parse <code>JsonArray</code>.
     *
     * @param detail the <code>JsonArray</code> that will be parsed.
     * @param basic if <code>true</code>, this method will work in basic mode and ignore <code>"header"</code> or
     *              <code>"footer"</code> section.
     */
    private void parseHelper(JsonArray detail, boolean basic) {
        for (JsonValue line : detail) {
            if (line instanceof JsonString) {
                String text = ((JsonString) line).getString();
                findPlaceholderIn(text);
                result.append(text);
                result.append(pageFormat.isAutoLineFeed() ? EscpUtil.CR : EscpUtil.CRLF);
            }
            lineNumber++;
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
        if (firstPage != null) {
            parseHelper(firstPage, true);
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
