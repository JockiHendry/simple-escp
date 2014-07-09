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

    static {
        Font defaultFont;
        try {
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, Thread.currentThread().getContextClassLoader().
                getResourceAsStream("DejaVuSansMono.ttf")).deriveFont(Font.PLAIN, DEFAULT_FONT_SIZE);
        } catch (FontFormatException | IOException e) {
            defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, DEFAULT_FONT_SIZE);
        }
        FONT = defaultFont;
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

        this.lines = text.split("(" + EscpUtil.CRLF  + ")");
        this.pages = this.lines.length / pageLength;

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

            // Ignore ESC Sequences that didn't have effect in preview
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_INITIALIZE), "");
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_ONE_PER_EIGHT_LINE_SPACING), "");
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_ONE_PER_SIX_INCH_LINE_SPACING), "");
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_MASTER_SELECT) + "(.|\\r|\\n)", "");
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_SELECTTYPEFACE) + "(.|\\r|\\n)", "");
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_LEFT_MARGIN) + "(.|\\r|\\n)", "");
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_RIGHT_MARGIN) + "(.|\\r|\\n)", "");
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_BOTTOM_MARGIN) + "(.|\\r|\\n)", "");
            line = line.replaceAll(EscpUtil.esc(EscpUtil.COMMAND_PAGE_LENGTH) + "(.|\\r|\\n)", "");
            line = line.replaceAll(EscpUtil.CRFF, "");

            // Replace box drawing characters
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_HORIZONTAL), "\u2500");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_VERTICAL), "\u2502");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_DOWN_RIGHT), "\u250c");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_DOWN_LEFT), "\u2510");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_DOWN_HORIZONTAL), "\u252c");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_VERTICAL_RIGHT), "\u251c");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_VERTICAL_HORIZONTAL), "\u253c");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_VERTICAL_LEFT), "\u2524");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_UP_RIGHT), "\u2514");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_UP_HORIZONTAL), "\u2534");
            line = line.replaceAll(String.valueOf(EscpUtil.CP347_LIGHT_UP_LEFT), "\u2518");

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
