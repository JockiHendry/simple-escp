package simple.escp.placeholder;

import simple.escp.data.DataSource;
import simple.escp.exception.InvalidPlaceholder;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represent a <code>Placeholder</code> that contains scripts that will be executed by using
 * JSR 223 Scripting for the Java Platform API.
 */
public class ScriptPlaceholder extends Placeholder {

    private static Logger logger = Logger.getLogger("simple.escp.placeholder.ScriptPlaceholder");

    private String script;
    private ScriptEngine scriptEngine;

    /**
     * Create a new instance of script placeholder.
     *
     * @param text a string that defines this placeholder.
     * @param scriptEngine a script engine to execute script in this placeholder.
     */
    public ScriptPlaceholder(String text, ScriptEngine scriptEngine) {
        super(text);
        this.scriptEngine = scriptEngine;
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
            parseText(parts[1].split(":"));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(DataSource[] dataSources) {
        try {
            return scriptEngine.eval(script);
        } catch (ScriptException e) {
            throw new InvalidPlaceholder("Can't execute script [" + script + "]", e);
        }
    }

}
