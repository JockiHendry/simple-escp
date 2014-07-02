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

import org.junit.Test;
import simple.escp.PageFormat;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest {

    @Test
    public void findPlaceholderNameResult() {
        PageFormat pageFormat = new PageFormat();
        Parser parser = new Parser(pageFormat);
        Set<String> results = parser.findPlaceholderIn("Your id is ${id}");
        assertEquals(1, results.size());
        assertTrue(results.contains("id"));

        results = parser.findPlaceholderIn("Your id is ${id} and your name is ${name}");
        assertEquals(2, results.size());
        assertTrue(results.contains("id"));
        assertTrue(results.contains("name"));
    }

    @Test
    public void findPlaceholderName() {
        PageFormat pageFormat = new PageFormat();
        Parser parser = new Parser(pageFormat);
        parser.findPlaceholderIn("Your id is ${id} and your name is ${name}");
        assertEquals(2, parser.getPlaceholderNames().size());
        assertTrue(parser.getPlaceholderNames().contains("id"));
        assertTrue(parser.getPlaceholderNames().contains("name"));
    }

}
