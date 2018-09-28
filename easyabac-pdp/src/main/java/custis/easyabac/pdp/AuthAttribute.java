package custis.easyabac.pdp;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
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
}
