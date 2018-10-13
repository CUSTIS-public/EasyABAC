package custis.easyabac.core.trace.model;

import java.util.List;

/**
 * Calculated Attribute during authorization
 */
public class CalculatedAttribute {

    private final String id;
    private final List<String> values;

    public CalculatedAttribute(String id, List<String> values) {
        this.id = id;
        this.values = values;
    }

    public static CalculatedAttribute of(String id, List<String> values) {
        return new CalculatedAttribute(id, values);
    }

    public String getId() {
        return id;
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "CalculatedAttribute{" +
                "id='" + id + '\'' +
                ", values=" + values +
                '}';
    }

    public void addValues(List<String> values) {
        this.values.addAll(values);
    }
}
