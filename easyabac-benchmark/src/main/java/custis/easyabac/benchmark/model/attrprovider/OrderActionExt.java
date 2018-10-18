package custis.easyabac.benchmark.model.attrprovider;

import custis.easyabac.api.attr.imp.AttributiveAuthorizationAction;
import custis.easyabac.core.pdp.AuthAttribute;

import java.util.Arrays;
import java.util.Optional;

public enum OrderActionExt implements AttributiveAuthorizationAction {

    VIEW("view"), CREATE("create"), APPROVE("approve"), REJECT("reject");

    private String id;

    private OrderActionExt(String id) {
        this.id = id;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public static OrderActionExt byId(String id) {
        Optional<OrderActionExt> optional = Arrays.asList(values()).stream().filter(action -> action.id.equals(id)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new IllegalArgumentException(id);
    }

    @Override
    public AuthAttribute getAuthAttribute() {
        return new AuthAttribute("order.action", "order." + id);
    }
}
