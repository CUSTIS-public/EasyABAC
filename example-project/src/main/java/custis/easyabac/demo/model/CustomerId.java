package custis.easyabac.demo.model;

import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.UUID;

@NoArgsConstructor
@Embeddable
public class CustomerId extends GenericId {

    public CustomerId(String value) {
        super(value);
    }

    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID().toString());
    }

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }
}
