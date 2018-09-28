package custis.easyabac.demo.model;

import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.UUID;

@NoArgsConstructor
@Embeddable
public class OrderId extends GenericId {

    public OrderId(String value) {
        super(value);
    }

    public static OrderId newId() {
        return new OrderId(UUID.randomUUID().toString());
    }

    public static OrderId of(String value) {
        return new OrderId(value);
    }
}
