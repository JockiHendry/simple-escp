package simple.escp.placeholder;

/**
 * This class represent a <code>Placeholder</code> that contains scripts that will be executed by using
 * JSR 223 Scripting for the Java Platform API.
 */
public class ScriptPlaceholder extends Placeholder {

    private String script;

    /**
     * Create a new instance of script placeholder.
     *
     * @param text a string that defines this placeholder.
     */
    public ScriptPlaceholder(String text) {
        super(text);
        parseText(getText());
    }

    /**
     * Parse placeholder text.
     *
     * @param text full text that represent this placeholder.
     */
    private void parseText(String text) {
        if (text.contains(":")) {
            String[] parts = text.split(":", 2);
            this.script = parts[0].trim();
            for (String part: parts[1].split(":")) {
                part = part.trim();
                parseFormatter(part);
                parseWidth(part);
            }
        } else {
            this.script = text;
        }
    }

    /**
     * Retrieve the script of this placeholder.
     *
     * @return a script that will be executed for this placeholder.
     */
    public String getScript() {
        return script;
    }

    /**
     * Set the script of this placeholder.
     *
     * @param script script that will be executed for this placeholder.
     */
    public void setScript(String script) {
        this.script = script;
    }


}
