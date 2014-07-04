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

import simple.escp.Line;
import simple.escp.PageFormat;
import simple.escp.Report;
import simple.escp.TextLine;
import javax.json.JsonArray;

/**
 * A helper class for parsing.
 */
public class Parser {

    private Report result;
    private Integer pageLength;
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
        this.pageLength = pageFormat.getPageLength();
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
        if (pageLength == null) {
            throw new IllegalArgumentException("Can't use 'header' if 'pageLength' is not defined.");
        }
        this.header = header;
    }

    /**
     * Set the "footer" section.
     *
     * @param footer a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setFooter(JsonArray footer) {
        if (pageLength == null) {
            throw new IllegalArgumentException("Can't use 'footer' if 'pageLength' is not defined.");
        }
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
     * Convert <code>JsonArray</code> into <code>String[]</code>.
     *
     * @param text is the JSON array to convert.
     * @return result in <code>String[]</code>.
     */
    private String[] jsonToString(JsonArray text) {
        int size = (text == null ? 0 : text.size());
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = text.getString(i);
        }
        return result;
    }

    /**
     * Convert <code>JsonArray</code> into <code>Line[]</code>.
     *
     * @param text is the JSON array to convert.
     * @return result in <code>Line[]</code>.
     */
    private Line[] jsonToLine(JsonArray text) {
        int size = (text == null ? 0 : text.size());
        TextLine[] result = new TextLine[size];
        for (int i = 0; i < size; i++) {
            result[i] = new TextLine(text.getString(i));
        }
        return result;
    }

    /**
     * Before calling this method, don't forget to call setters such as <code>setFirstPage()</code>,
     * <code>setLastPage()</code>, <code>setDetail()</code>, etc.  The parse result from this method
     * can also be obtained later by calling <code>getResult()</code>.
     *
     * @return result of parsing in <code>Pages</code>.
     */
    public Report parse() {
        result = new Report(pageFormat, (TextLine[]) jsonToLine(header), (TextLine[]) jsonToLine(footer));
        if (firstPage != null) {
            result.appendSinglePage(jsonToLine(firstPage), true);
            result.lineBreak();
        }
        if (detail != null) {
            for (Line line: jsonToLine(detail)) {
                result.append(line, false);
            }
        }
        if (lastPage != null) {
            result.lineBreak();
            result.appendSinglePage(jsonToLine(lastPage), true);
        }
        return getResult();
    }

    /**
     * Get the result of previous <code>parse()</code> invocation.  If <code>parse()</code> hasn't been invoked
     * before, this method will invoke it and return the result.
     *
     * @return result of parsing in <code>Pages</code>.
     */
    public Report getResult() {
        if (result == null) {
            parse();
        }
        return result;
    }

}
