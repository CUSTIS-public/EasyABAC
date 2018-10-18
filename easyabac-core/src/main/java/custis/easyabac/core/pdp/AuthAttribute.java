package custis.easyabac.core.pdp;

import java.util.Arrays;
import java.util.List;


public class AuthAttribute {

    private String id;
    private List<String> values;

    public AuthAttribute(String id, List<String> values) {
        this.id = id;
        this.values = values;
    }

    public AuthAttribute(String id, String value) {
        this.id = id;
        this.values = Arrays.asList(value);
    }

    public String getId() {
        return id;
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthAttribute that = (AuthAttribute) o;

        if (!id.equals(that.id)) return false;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }



}
