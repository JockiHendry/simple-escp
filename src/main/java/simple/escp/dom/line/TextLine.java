package simple.escp.dom.line;

import simple.escp.dom.Line;

/**
 * Implementation of <code>Line</code> for a line that consists of text.
 */
public class TextLine extends Line {

    private String text;

    /**
     * Create a new instance of <code>TextLine</code> from a string.
     *
     * @param text the string that represent text for this line.
     */
    public TextLine(String text) {
        this.text = text;
    }

    /**
     * Create a new instance as a copy from another <code>TextLine</code>.
     *
     * @param another the instance to copy.
     */
    public TextLine(TextLine another) {
        this(another.getText());
        setLineNumber(another.getLineNumber());
        setGlobalLineNumber(another.getGlobalLineNumber());
    }

    /**
     * Get string value for this line.
     *
     * @return string value for this line.
     */
    public String getText() {
        return text;
    }

    /**
     * Set a new string value for this line.
     *
     * @param text the new string value for this line.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDynamic() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return text;
    }
}
