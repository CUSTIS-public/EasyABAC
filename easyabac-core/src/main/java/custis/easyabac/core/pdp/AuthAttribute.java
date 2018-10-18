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


}
