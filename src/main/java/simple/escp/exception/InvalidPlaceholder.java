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

package simple.escp.exception;

/**
 *  This exception will be thrown if there is something wrong with template's placeholder.
 */
public class InvalidPlaceholder extends RuntimeException {

    /**
     * Create a new instance with custom error message.
     *
     * @param message a description for the cause of invalid placeholder.
     */
    public InvalidPlaceholder(String message) {
        super(message);
    }

    /**
     * Create a new instance with custom error message.
     *
     * @param message a description for the cause of invalid placeholder.
     * @param cause the cause of this <code>Exception</code>.
     */
    public InvalidPlaceholder(String message, Throwable cause) {
        super(message, cause);
    }

}
