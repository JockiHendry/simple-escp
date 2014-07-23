package simple.escp.data;

import simple.escp.exception.InvalidPlaceholder;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.StringReader;

public class CustomJsonDataSource implements DataSource {

    private String jsonString;
    private JsonObject json;

    public CustomJsonDataSource(String jsonString) {
        this.jsonString = jsonString;
        try (JsonReader reader = Json.createReader(new StringReader(jsonString))) {
            json = reader.readObject();
        }
    }

    public CustomJsonDataSource(JsonObject jsonObject) {
        this.json = jsonObject;
    }

    @Override
    public boolean has(String s) {
        return json.containsKey(s);
    }

    @Override
    public Object get(String member) throws InvalidPlaceholder {
        JsonValue value = json.get(member);
        if (value.getValueType() == JsonValue.ValueType.ARRAY) {
            return value;
        } else if (value.getValueType() == JsonValue.ValueType.NUMBER) {
            return ((JsonNumber) value).bigDecimalValue();
        } else if (value.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) value).getString();
        } else {
            return value.toString();
        }
    }

    @Override
    public Object getSource() {
        return jsonString;
    }

    @Override
    public String[] getMembers() {
        return json.keySet().toArray(new String[0]);
    }
}
