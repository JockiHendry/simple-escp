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
import simple.escp.category.RequirePrinterCategory;

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
    
}
