package custis.easyabac.pdp;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthAttribute {

    private String id;
    private Object value;

    public AuthAttribute(String id, Object value) {
        this.id = id;
        this.value = value;
    }
}
