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

import simple.escp.data.DataSource;
import simple.escp.data.DataSources;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.standard.PrinterName;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods to access simple-escp services.
 * You should create a new <code>SimpleEscp</code> for every instance of <code>PrintService</code>.
 *
 * <p>For example, to print string from default printer, use the following code:
 *
 * <pre>
 *     SimpleEscp simpleEscp = new SimpleEscp();
 *     simpleEscp.print("This is the first line\nThis is the second line\n\f");
 * </pre>
 *
 * <p>To print based on JSON template, use the following code:
 *
 * <pre>
 *     Template template = new JsonTemplate(...);
 *     Map data = new HashMap();
 *     data.put("name", "Solid Snake");
 *     SimpleEscp simpleEscp = new SimpleEscp();
 *     simpleEscp.print(template, data);
 * </pre>
 */
public class SimpleEscp {

    private static Logger logger = Logger.getLogger("simple.escp.SimpleEscp");

    private PrintService printService;

    /**
     * Create a new instance of <code>SimpleEscp</code> that will use default printer.
     */
    public SimpleEscp() {
        useDefaultPrinter();
    }

    /**
     * Create a new instance of <code>SimpleEscp</code> that will use specified printer's name.
     *
     * @param printerName the name of printer that will be used for printing.
     */
    public SimpleEscp(String printerName) {
        HashAttributeSet attributeSet = new HashAttributeSet();
        attributeSet.add(new PrinterName(printerName, null));
        usePrinter(attributeSet);
    }

    /**
     * Use the printer that matches the specified <code>AttributeSet</code>.
     *
     * @param attributeSet use printer that matches this value.
     * @throws java.lang.IllegalArgumentException if printer is not found.
     *
     */
    public void usePrinter(AttributeSet attributeSet) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, attributeSet);
        if (services.length == 0) {
            throw new IllegalArgumentException("Printer not found.");
        } else if (services.length > 1) {
            logger.log(Level.WARNING, "Found more than one printer. Only the first printer will be used.");
        }
        printService = services[0];
    }

    /**
     * Use printer that was marked as default printer in operating system's control panel.
     */
    public void useDefaultPrinter() {
        printService = PrintServiceLookup.lookupDefaultPrintService();
    }

    /**
     * Print a string to current printer.  This is a low level service provides by simple-escp for
     * direct string printing.
     *
     * @param text the string to print.  This string may contains ESC/P code.
     * @return a <code>DocPrintJob</code> that is associated with this operation.
     */
    public DocPrintJob print(String text)  {
        InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII));
        Doc doc = new SimpleDoc(in, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        DocPrintJob job = printService.createPrintJob();
        try {
            job.print(doc, null);
        } catch (PrintException e) {
            logger.log(Level.SEVERE, "Error during printing.", e);
            throw new RuntimeException("Error during printing", e);
        }
        return job;
    }

    /**
     * Fill a template based on value and print it to current printer.
     *
     * @param template an instance of <code>Template</code>.
     * @param mapSource contains values that will replace placeholders in template.  This value has the highest
     *                  priority.
     * @param objectSource contains value that will replace placeholders in template.
     * @return a <code>DocPrintJob</code> that is associated with this operation.
     */
    public DocPrintJob print(Template template, Map mapSource, Object objectSource) {
        FillJob fillJob = new FillJob(template.parse(), DataSources.from(mapSource, objectSource));
        return print(fillJob.fill());
    }

    /**
     * Fill a template based on value and print it to current printer.
     *
     * @param template an instance of <code>Template</code>.
     * @param mapSource contains values that will replace placeholders in template.
     * @return a <code>DocPrintJob</code> that is associated with this operation.
     */
    public DocPrintJob print(Template template, Map mapSource) {
        FillJob fillJob = new FillJob(template.parse(), DataSources.from(mapSource));
        return print(fillJob.fill());
    }

    /**
     * Fill a template based on value and print it to current printer.
     *
     * @param template an instance of <code>Template</code>.
     * @param dataSource the data source to fill <code>template</code>
     * @return a <code>DocPrintJob</code> that is associated with this operation.
     */
    public DocPrintJob print(Template template, DataSource dataSource) {
        FillJob fillJob = new FillJob(template.parse(), dataSource);
        return print(fillJob.fill());
    }

    /**
     * Fill a template based on value and print it to current printer.
     *
     * @param template an instance of <code>Template</code>.
     * @param dataSources one or more <code>DataSource</code> to fill <code>template</code>.
     * @return a <code>DocPrintJob</code> that is associated with this operation.
     */
    public DocPrintJob print(Template template, DataSource[] dataSources) {
        FillJob fillJob = new FillJob(template.parse(), dataSources);
        return print(fillJob.fill());
    }

    /**
     * Get printer or print service associated with this instance.
     *
     * @return an instance of <code>PrintService</code>.
     */
    public PrintService getPrintService() {
        return printService;
    }

}
