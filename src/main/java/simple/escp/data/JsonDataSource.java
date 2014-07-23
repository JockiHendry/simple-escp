package simple.escp.data;

import simple.escp.exception.InvalidPlaceholder;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.logging.Logger;

/**
 * This data source will read data source from JSON.  The JSON can be in form of string or <code>JsonObject</code>.
 */
public class JsonDataSource implements DataSource {

    private static final Logger LOG = Logger.getLogger("simple.escp");
    private JsonObject source;

    /**
     * Create a new <code>JsonDataSource</code> from string that consists of valid JSON format.
     *
     * @param jsonString a valid JSON string that will be parsed and used as source value for this data source.
     */
    public JsonDataSource(String jsonString) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonString))) {
            source = reader.readObject();
        }
    }

    /**
     * Create a new <code>JsonDataSource</code> from a <code>JsonObject</code>.
     *
     * @param jsonObject a <code>JsonObject</code> that will be used as source value for this data source.
     */
    public JsonDataSource(JsonObject jsonObject) {
        source = jsonObject;
    }

    @Override
    public boolean has(String member) {
        return source.containsKey(member);
    }

    @Override
    public Object get(String member) throws InvalidPlaceholder {
        JsonValue value = source.get(member);
        if (value.getValueType() == JsonValue.ValueType.ARRAY) {
            return value;  // as List
        } else if (value.getValueType() == JsonValue.ValueType.NUMBER) {
            return ((JsonNumber) value).bigDecimalValue();
        } else if (value.getValueType() == JsonValue.ValueType.TRUE) {
            return Boolean.TRUE;
        } else if (value.getValueType() == JsonValue.ValueType.FALSE) {
            return Boolean.FALSE;
        } else if (value.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) value).getString();
        } else {
            return value;
        }
    }

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public String[] getMembers() {
        return source.keySet().toArray(new String[0]);
    }
}
