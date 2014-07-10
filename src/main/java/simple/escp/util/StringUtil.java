package simple.escp.util;

/**
 * <code>StringUtil</code> is an utility class that provides methods for String manipulation.
 */
public class StringUtil {

    /**
     * This enumeration represent text alignments.
     */
    public enum ALIGNMENT { LEFT, RIGHT, CENTER };

    /**
     * Create left-aligned text with a maximum <code>width</code> characters.
     *
     * @param text the text that will be aligned.
     * @param width maximum number of characters.  Text exceeds this limit will be truncated.
     * @return aligned text.
     */
    public static String alignLeft(String text, int width) {
        if (text.length() < width) {
            StringBuilder tmp = new StringBuilder(text);
            int numOfSpaces = width - text.length() + 1;
            for (int i = 1; i < numOfSpaces; i++) {
                tmp.append(' ');
            }
            return tmp.toString();
        } else if (text.length() > width) {
            return text.substring(0, width);
        }
        return text;
    }

    /**
     * Create center-aligned text with a maximum <code>width</code> characters.
     *
     * @param text the text that will be aligned.
     * @param width maximum number of characters.  Text exceeds this limit will be truncated.
     * @return aligned text.
     */
    public static String alignCenter(String text, int width) {
        if (text.length() < width) {
            StringBuilder tmp = new StringBuilder();
            int numOfSpaces = (width - text.length()) / 2;
            for (int i = 0; i < numOfSpaces; i++) {
                tmp.append(' ');
            }
            tmp.append(text);
            while (tmp.length() < width) {
                tmp.append(' ');
            }
            return tmp.toString();
        } else if (text.length() > width) {
            return text.substring(0, width);
        }
        return text;
    }

    /**
     * Create right-aligned text with a maximum <code>width</code> characters.
     *
     * @param text the text that will be aligned.
     * @param width maximum number of characters.  Text exceeds this limit will be truncated.
     * @return aligned text.
     */
    public static String alignRight(String text, int width) {
        if (text.length() < width) {
            StringBuilder tmp = new StringBuilder();
            int numOfSpaces = width - text.length() + 1;
            for (int i = 1; i < numOfSpaces; i++) {
                tmp.append(' ');
            }
            tmp.append(text);
            return tmp.toString();
        } else if (text.length() > width) {
            return text.substring(0, width);
        }
        return text;
    }

    /**
     * Create aligned text with a maximum <code>width</code> characters.
     * @param text the text that will be aligned.
     * @param width maximum number of characters.  Text exceeds this limit will be truncated.
     * @param alignment the <code>ALIGNMENT</code> type.
     * @return aligned text.
     */
    public static String align(String text, int width, ALIGNMENT alignment) {
        if (alignment == ALIGNMENT.LEFT) {
            return alignLeft(text, width);
        } else if (alignment == ALIGNMENT.CENTER) {
            return alignCenter(text, width);
        } else if (alignment == ALIGNMENT.RIGHT) {
            return alignRight(text, width);
        }
        throw new IllegalArgumentException("Invalid alignment: " + alignment);
    }
}
