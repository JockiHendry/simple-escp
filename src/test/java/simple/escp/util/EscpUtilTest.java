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

package simple.escp.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class EscpUtilTest {

    @Test
    public void esc() {
        String result = EscpUtil.esc(55);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 55, result.charAt(1));
    }

    @Test
    public void escValue() {
        String result = EscpUtil.esc(73, 0);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 73, result.charAt(1));
        assertEquals((char)  0, result.charAt(2));

        result = EscpUtil.esc(40, 71, 1, 0, 20);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 40, result.charAt(1));
        assertEquals((char) 71, result.charAt(2));
        assertEquals((char)  1, result.charAt(3));
        assertEquals((char)  0, result.charAt(4));
        assertEquals((char) 20, result.charAt(5));
    }

    @Test
    public void escInitialize() {
        String result = EscpUtil.escInitalize();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 64, result.charAt(1));
    }

    @Test
    public void escOnePerSixInchLineSpacing() {
        String result = EscpUtil.escOnePerSixInchLineSpacing();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 50, result.charAt(1));
    }

    @Test
    public void escOnePerEightInchLineSpacing() {
        String result = EscpUtil.escOnePerEightInchLineSpacing();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 48, result.charAt(1));
    }

    @Test
    public void escPageLength() {
        String result = EscpUtil.escPageLength(10);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 67, result.charAt(1));
        assertEquals((char) 10, result.charAt(2));
    }

}
