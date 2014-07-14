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

package simple.escp.printer;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import simple.escp.SimpleEscp;
import simple.escp.Template;
import simple.escp.category.RequirePrinterCategory;
import simple.escp.json.JsonTemplate;
import java.util.HashMap;
import java.util.Map;

@Category(RequirePrinterCategory.class)
public class SimpleEscpTest {

    @Test
    public void printString() {
        SimpleEscp simpleEscp = new SimpleEscp("EPSON LX-310 ESC/P");
        simpleEscp.print("printString(): Executing SimpleEscpTest.printString()\n" +
            "printString(): And this this a second line.\n");
    }

    @Test
    public void printStringFromDefaultPrinter() {
        SimpleEscp simpleEscp = new SimpleEscp();
        simpleEscp.print("printStringFromDefaultPrinter(): Executing SimpleEscpTest.printStringFromDefaultPrinter()\n" +
            "printStringFromDefaultPrinter(): And this this a second line.\n");
    }

    @Test
    public void printTemplateBasedOnMap() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"placeholder\": [" +
                "\"id\"," +
                "\"nickname\"" +
            "]," +
            "\"template\": [" +
                "\"From  : printTemplateBasedOnMap()\"," +
                "\"ID    : ${id}\"," +
                "\"Name  : Mr. ${nickname}.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        Map<String, String> data = new HashMap<>();
        data.put("id", "007");
        data.put("nickname", "The Solid Snake");
        simpleEscp.print(template, data, null);
    }

    @Test
    public void printLineSpacingOneEight() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"lineSpacing\": \"1/8\"" +
            "}," +
            "\"template\": [" +
                "\"LineSpace 1/8: First Line\"," +
                "\"LineSpace 1/8: Second Line\"," +
                "\"LineSpace 1/8: Third Line\"," +
                "\"LineSpace 1/8: Fourth Line\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print5Cpi() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"5\"" +
            "}," +
            "\"template\": [" +
                "\"5 cpi: This is an example text in 5 cpi.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print10Cpi() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"10\"" +
            "}," +
            "\"template\": [" +
                "\"10 cpi: This is an example text in 10 cpi.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print12Cpi() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"12\"" +
            "}," +
            "\"template\": [" +
                "\"12 cpi: This is an example text in 12 cpi.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print17Cpi() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"17\"" +
            "}," +
            "\"template\": [" +
                "\"17 cpi: This is an example text in 17 cpi.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void print20Cpi() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"characterPitch\": \"20\"" +
            "}," +
            "\"template\": [" +
                "\"20 cpi: This is an example text in 20 cpi.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printPageWidth() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"10\"" +
            "}," +
            "\"template\": [" +
                "\"Page Width 10: 1234567890.\"," +
                "\"Page Width 10: 1234567890.\"," +
                "\"Page Width 10: 1234567890.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printLeftAndRightMargin() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"70\"," +
                "\"leftMargin\": \"50\"," +
                "\"rightMargin\": \"10\"" +
            "}," +
            "\"template\": [" +
                "\"Margin 70,50,10: 1234567890.\"," +
                "\"Margin 70,50,10: 1234567890.\"," +
                "\"Margin 70,50,10: 1234567890.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printInRomanTypeface() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"typeface\": \"roman\"" +
            "}," +
            "\"template\": [" +
                "\"printInRomanTypeface: This should be in roman typeface.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printInSansSerifTypeface() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"pageFormat\": {" +
                "\"typeface\": \"sans-serif\"" +
            "}," +
            "\"template\": [" +
                "\"printInRomanTypeface: This should be in sans-serif typeface.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printBold() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"template\": [" +
                "\"bold: Normal %{BOLD}bold%{BOLD} normal.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printItalic() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"template\": [" +
                "\"italic: Normal %{ITALIC}italic%{ITALIC} normal.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }

    @Test
    public void printDoubleStrike() {
        SimpleEscp simpleEscp = new SimpleEscp();
        String json = "{" +
            "\"template\": [" +
                "\"strike: Normal %{DOUBLE}double strike%{DOUBLE} normal.\"" +
            "]" +
        "}";
        Template template = new JsonTemplate(json);
        template.getPageFormat().setAutoFormFeed(false);
        simpleEscp.print(template, null, null);
    }
}
