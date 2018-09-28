package custis.easyabac.demo.model;

import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.UUID;

@NoArgsConstructor
@Embeddable
public class BranchId extends GenericId {

    public BranchId(String value) {
        super(value);
    }

    public static BranchId newId() {
        return new BranchId(UUID.randomUUID().toString());
    }

    public static BranchId of(String value) {
        return new BranchId(value);
    }
}
