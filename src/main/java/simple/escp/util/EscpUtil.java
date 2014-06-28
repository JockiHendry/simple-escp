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

    public static final int ESC = 27;
    public static final int COMMAND_INITIALIZE = 64;
    public static final int COMMAND_ONE_PER_SIX_INCH_LINE_SPACING = 50;
    public static final int COMMAND_ONE_PER_EIGHT_LINE_SPACING = 48;
    public static final int COMMAND_MASTER_SELECT = 33;
    public static final int MASTER_SELECT_CPI_5 = 32;
    public static final int MASTER_SELECT_CPI_6 = 33;
    public static final int MASTER_SELECT_CPI_10 = 0;
    public static final int MASTER_SELECT_CPI_12 = 1;
    public static final int MASTER_SELECT_CPI_17 = 4;
    public static final int MASTER_SELECT_CPI_20 = 5;
    public static final int COMMAND_PAGE_LENGTH = 67;

    /**
     * Create an ESC/P code.
     *
     * @param command an ASCII number that represent ESC/P command.
     * @param value an ASCII number value as parameters for specified command.
     * @return ESC/P in form of a string.
     */
    public static String esc(int command, int... value) {
        StringBuffer result = new StringBuffer();
        result.append((char) ESC);
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

    /**
     * Generate ESC 2 to select 1/6-inch line spacing.
     *
     * @return string of ESC 2 command.
     */
    public static String escOnePerSixInchLineSpacing() {
        return esc(COMMAND_ONE_PER_SIX_INCH_LINE_SPACING);
    }

    /**
     * Generate ESC 0 to select 1/8-inch line spacing.
     *
     * @return string of ESC 0 command.
     */
    public static String escOnePerEightInchLineSpacing() {
        return esc(COMMAND_ONE_PER_EIGHT_LINE_SPACING);
    }

    /**
     * Generate ESC ! for master select.
     *
     * @param value parameter for ESC !
     * @return string of ESC ! command.
     */
    public static String escMasterSelect(int value) {
        return esc(COMMAND_MASTER_SELECT, value);
    }

    /**
     * Generate ESC C for setting page length.
     *
     * @param value parameter of ESC C (number of lines).
     * @return string of ESC C command.
     */
    public static String escPageLength(int value) {
        return esc(COMMAND_PAGE_LENGTH, value);
    }
}
