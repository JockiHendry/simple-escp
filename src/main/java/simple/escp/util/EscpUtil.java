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

/**
 *  This is a helper class to generate ESC/P string from number that represent ASCII code.
 */
public class EscpUtil {

    public static final char ESC = (char) 27;
    public static final int COMMAND_INITIALIZE = 64;

    /**
     * Create an ESC/P code.
     *
     * @param command an ASCII number that represent ESC/P command.
     * @param value an ASCII number value as parameters for specified command.
     * @return ESC/P in form of a string.
     */
    public static String esc(int command, int... value) {
        StringBuffer result = new StringBuffer();
        result.append(ESC);
        result.append((char) command);
        if (value != null) {
            for (int i: value) {
                result.append((char) i);
            }
        }
        return result.toString();
    }

    /**
     * Create an ESC/P code.
     *
     * @param command an ASCII number that represent ESC/P command.
     * @return ESC/P in form of a string.
     */
    public static String esc(int command) {
        return esc(command, null);
    }

    /**
     * Generate ESC @ command.  Use this command to initialized printer.
     *
     * @return string of ESC @ command.
     */
    public static String escInitalize() {
        return esc(COMMAND_INITIALIZE);
    }

}
