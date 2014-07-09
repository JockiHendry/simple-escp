package simple.escp.dom.line;

import simple.escp.dom.Line;

/**
 * This class represents an empty line that doesn't have any value associated with it.  It usually means that
 * the line should be discarded or not to be printed.
 */
public class EmptyLine implements Line {

    @Override
    public boolean isDynamic() {
        return false;
    }

}
