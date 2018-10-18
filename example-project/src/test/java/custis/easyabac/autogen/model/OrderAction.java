package custis.easyabac.autogen.model;

import custis.easyabac.api.attr.annotation.AuthorizationAction;
import custis.easyabac.api.attr.annotation.AuthorizationActionId;

import java.util.Arrays;
import java.util.Optional;

@AuthorizationAction(entity = "order")
public enum OrderAction {

    VIEW("view"), CREATE("create"), APPROVE("approve"), REJECT("reject");

    @AuthorizationActionId
    private String id;

    private OrderAction(String id) {
        this.id = id;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public static OrderAction byId(String id) {
        Optional<OrderAction> optional = Arrays.asList(values()).stream().filter(action -> action.id.equals(id)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new IllegalArgumentException(id);
    }
}
