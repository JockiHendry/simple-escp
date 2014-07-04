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

package simple.escp;

import simple.escp.exception.InvalidPlaceholder;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 *  <code>Placeholder</code> represent a placeholder in template, such as <code>${name}</code>.
 */
public class Placeholder {

    private String text;
    private Map mapSource;
    private Object objectSource;
    private PropertyDescriptor[] propertyDescriptors;
    private MethodDescriptor[] methodDescriptors;

    /**
     * Create a new instance of template's placeholder.
     *
     * @param text a string that defines this placeholder.
     * @param mapSource a source for this placeholder in form of <code>Map</code>.
     * @param objectSource a source for this placeholder in form of an object.
     */
    public Placeholder(String text, Map mapSource, Object objectSource) {
        this.text = text;
        this.mapSource = mapSource;
        this.objectSource = objectSource;
        if (objectSource != null) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(objectSource.getClass());
                propertyDescriptors = beanInfo.getPropertyDescriptors();
                methodDescriptors = beanInfo.getMethodDescriptors();
            } catch (IntrospectionException e) {
                throw new RuntimeException("Can't read information from object.", e);
            }
        }
    }

    /**
     * Get the text of this placeholder.  All placeholder will be identified in template
     * by their text.  For example, placeholder text for <code>${name}</code> is <code>name</code>.
     *
     * @return text of this placeholder.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of this placeholder.
     *
     * @param text the text for this placeholder.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get the <code>Map</code> source for this placeholder.
     *
     * @return a <code>Map</code> or <code>null</code> if source is not defined.
     */
    public Map getMapSource() {
        return mapSource;
    }

    /**
     * Set the <code>Map</code> source for this placeholder.
     *
     * @param mapSource a new <code>Map</code> source.
     */
    public void setMapSource(Map mapSource) {
        this.mapSource = mapSource;
    }

    /**
     * Get the <code>Object</code> source for this placeholder.
     *
     * @return an <code>object</code> or <code>null</code> if source is not defined.
     */
    public Object getObjectSource() {
        return objectSource;
    }

    /**
     * Set the object source for this placeholder.
     *
     * @param objectSource a new object source.
     */
    public void setObjectSource(Object objectSource) {
        this.objectSource = objectSource;
    }

    /**
     * Retrieve value for this placeholder in form of object.  In most cases, you will use
     * {@link #value()} that will return a <code>String</code> instead of object.
     *
     * @return an object that represent the value for this placeholder.
     */
    public Object valueAsObject() {
        Object v = null;
        // Try to get value from Map first.
        if (mapSource != null) {
            v = mapSource.get(text);
        }
        if (v == null && objectSource != null) {
            if (text.startsWith("@")) {
                // This is a method call
                text = text.substring(1);
                for (MethodDescriptor methodDescriptor : methodDescriptors) {
                    if (methodDescriptor.getName().equals(text)) {
                        try {
                            v = methodDescriptor.getMethod().invoke(objectSource);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new InvalidPlaceholder("Can't invoke method for placeholder [" +
                                    text + "]");
                        }
                    }
                }
            } else {
                // Try to get value from object's property.
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyDescriptor.getName().equals(text)) {
                        try {
                            v = propertyDescriptor.getReadMethod().invoke(objectSource);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new InvalidPlaceholder("Can't get property for placehoder [" +
                                    text + "]");
                        }
                        break;
                    }
                }
            }
        }

        if (v == null) {
            throw new InvalidPlaceholder("Can't find supplied value for placeholder [" + text + "].");
        }

        return v;
    }
    /**
     * Retrieve formatted value for this placeholder.
     *
     * @return formatted value that will be used for printing.
     */
    public String value() {
        return valueAsObject().toString();
    }

}
