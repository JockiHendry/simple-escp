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

package simple.escp.swing;

import simple.escp.util.EscpUtil;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  This panel will display string as GUI preview.
 */
public class OutputPane extends JPanel {

    private static final Font FONT;
    private static final int DEFAULT_FONT_SIZE = 20;
    private static final String[] CP437_TO_UNICODE;
    private static final int CP437_ASCII_START = 128;

    static {
        Font defaultFont;
        try {
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, Thread.currentThread().getContextClassLoader().
                getResourceAsStream("DejaVuSansMono.ttf")).deriveFont(Font.PLAIN, DEFAULT_FONT_SIZE);
        } catch (FontFormatException | IOException e) {
            defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, DEFAULT_FONT_SIZE);
        }
        FONT = defaultFont;

        CP437_TO_UNICODE = new String[] {
            "\u00C7", "\u00FC", "\u00E9", "\u00E2", "\u00E4", "\u00E0", "\u00E5", "\u00E7",
            "\u00EA", "\u00EB", "\u00E8", "\u00EF", "\u00EE", "\u00EC", "\u00C4", "\u00C5",
            "\u00C9", "\u00E6", "\u00C6", "\u00F4", "\u00F6", "\u00F2", "\u00FB", "\u00F9",
            "\u00FF", "\u00D6", "\u00DC", "\u00A2", "\u00A3", "\u00A5", "\u20A7", "\u0192",
            "\u00E1", "\u00ED", "\u00F3", "\u00FA", "\u00F1", "\u00D1", "\u00AA", "\u00BA",
            "\u00BF", "\u2310", "\u00AC", "\u00BD", "\u00BC", "\u00A1", "\u00AB", "\u00BB",
            "\u2591", "\u2592", "\u2593", "\u2502", "\u2524", "\u2561", "\u2562", "\u2556",
            "\u2555", "\u2563", "\u2551", "\u2557", "\u255D", "\u255C", "\u255B", "\u2510",
            "\u2514", "\u2534", "\u252C", "\u251C", "\u2500", "\u253C", "\u255E", "\u255F",
            "\u255A", "\u2554", "\u2569", "\u2566", "\u2560", "\u2550", "\u256C", "\u2567",
            "\u2568", "\u2564", "\u2565", "\u2569", "\u2558", "\u2552", "\u2553", "\u256B",
            "\u256A", "\u2518", "\u250C", "\u258B", "\u2584", "\u258C", "\u2590", "\u2580",
            "\u03B1", "\u00DF", "\u0393", "\u03C0", "\u03A3", "\u03C3", "\u00B5", "\u03C4",
            "\u03A6", "\u039B", "\u03A9", "\u03B4", "\u221E", "\u03C6", "\u03B5", "\u2229",
            "\u2261", "\u00B1", "\u2265", "\u2264", "\u2320", "\u2321", "\u00F7", "\u2248",
            "\u00B0", "\u2219", "\u00B7", "\u221A", "\u207F", "\u00B2", "\u25A0", "\u00A0",
        };

    }

    private static final BasicStroke DASH_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 10.0f, new float[] {1.0f}, 0.0f);
    private static final float MARGIN_LEFT = 10.0f;
    private static final float MARGIN_RIGHT = 50.0f;
    private static final int CHAR_WIDTH = 12;
    private static final int CHAR_HEIGHT = 25;
    private static final int CIRCLE_SIZE = 15;
    private static final int CIRCLE_SPACING = 25;
    private static final int CIRCLE_LINE_MARGIN = 5;
    private static final float X_START = 10.0f, Y_START = 10.0f;

    private String[] lines;
    private int pageLength;
    private int pageWidth;
    private int pages;

    private List<Shape> backgrounds;
    private Dimension prefferedSize;

    /**
     * Construct a new instance of <code>OutputPane</code>.
     *
     * @param text the string that should be displayed.  It may contains ESC/P commands.
     * @param pageLength number of lines per page.
     * @param pageWidth number of characters per line.
     */
    public OutputPane(String text, int pageLength, int pageWidth) {
        this.pageLength = pageLength;
        this.pageWidth = pageWidth;

        // Replace CP347 characters with Unicode
        StringBuilder result = new StringBuilder();
        for (int textIndex = 0; textIndex < text.length(); textIndex++) {
            int i = CP437_ASCII_START;
            boolean found = false;
            for (String ch : CP437_TO_UNICODE) {
                if (text.charAt(textIndex) == (char)i) {
                    result.append(ch);
                    found = true;
                    break;
                }
                i++;
            }
            if (!found) {
                result.append(text.charAt(textIndex));
            }
        }

        this.lines = result.toString().split("(" + EscpUtil.CRLF + ")");
        this.pages = this.lines.length / pageLength;
        for (int i = 0; i < lines.length; i++) {
            // Ignore ESC Sequences that didn't have effect in preview
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_INITIALIZE), "");
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_ONE_PER_EIGHT_LINE_SPACING), "");
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_ONE_PER_SIX_INCH_LINE_SPACING), "");
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_MASTER_SELECT) + "(.|\\r|\\n)", "");
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_SELECT_TYPEFACE) + "(.|\\r|\\n)", "");
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_LEFT_MARGIN) + "(.|\\r|\\n)", "");
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_RIGHT_MARGIN) + "(.|\\r|\\n)", "");
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_BOTTOM_MARGIN) + "(.|\\r|\\n)", "");
            lines[i] = lines[i].replaceAll(EscpUtil.esc(EscpUtil.COMMAND_PAGE_LENGTH) + "(.|\\r|\\n)", "");
            lines[i] = lines[i].replaceAll(EscpUtil.CRFF, "");
        }

        // Create background shapes
        backgrounds = new ArrayList<>();
        float x = X_START, y = Y_START;
        float xWidth = MARGIN_LEFT + MARGIN_RIGHT + (pageWidth * CHAR_WIDTH);
        for (int page = 0; page <= pages; page++) {
            for (int line = 0; line < pageLength; line++) {
                Shape circleStart = new Ellipse2D.Float(x, y, CIRCLE_SIZE, CIRCLE_SIZE);
                Shape circleEnd = new Ellipse2D.Float(x + xWidth, y, CIRCLE_SIZE, CIRCLE_SIZE);
                backgrounds.add(circleStart);
                backgrounds.add(circleEnd);
                y += CIRCLE_SPACING;
            }
            Shape lineBreak = new Line2D.Float(X_START, y, X_START + xWidth + CIRCLE_SIZE,
                y);
            backgrounds.add(lineBreak);
        }
        Shape horLineLeft1 = new Line2D.Float(X_START + CIRCLE_SIZE + CIRCLE_LINE_MARGIN, Y_START,
            X_START + CIRCLE_SIZE + CIRCLE_LINE_MARGIN, y);
        Shape horLineLeft2 = new Line2D.Float(X_START - CIRCLE_LINE_MARGIN, Y_START,
            X_START - CIRCLE_LINE_MARGIN, y);
        Shape horLineRight1 = new Line2D.Float(X_START + xWidth - CIRCLE_LINE_MARGIN, Y_START,
            X_START + xWidth - CIRCLE_LINE_MARGIN, y);
        Shape horLineRight2 = new Line2D.Float(X_START + xWidth + CIRCLE_SIZE + CIRCLE_LINE_MARGIN , Y_START,
            X_START + xWidth + CIRCLE_SIZE + CIRCLE_LINE_MARGIN, y);
        backgrounds.add(horLineLeft1);
        backgrounds.add(horLineLeft2);
        backgrounds.add(horLineRight1);
        backgrounds.add(horLineRight2);

        prefferedSize = new Dimension((int)(X_START + xWidth + CIRCLE_SIZE + CIRCLE_SPACING),
                                      (int)(y + CIRCLE_SIZE + CIRCLE_SPACING));
    }

    /**
     * Draw background.
     *
     * @param g2 a graphic context for drawing.
     */
    private void paintBackground(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.GRAY);
        g2.setStroke(DASH_STROKE);
        for (Shape shape : backgrounds) {
            g2.draw(shape);
        }
    }

    /**
     * Draw text.
     *
     * @param g2 a graphic context for drawing.
     */
    private void paintText(Graphics2D g2) {
        g2.setFont(FONT);
        g2.setColor(Color.BLACK);

        float x = X_START + CIRCLE_SIZE + MARGIN_LEFT;
        float y = Y_START + CIRCLE_SIZE;
        for (String line: lines) {
            g2.drawString(line, x, y);
            y += CHAR_HEIGHT;
        }

    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        paintBackground(g2);
        paintText(g2);
    }

    @Override
    public Dimension getPreferredSize() {
        return prefferedSize;
    }

}
