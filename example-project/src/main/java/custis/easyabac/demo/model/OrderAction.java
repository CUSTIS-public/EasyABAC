package custis.easyabac.demo.model;

import custis.easyabac.api.attr.annotation.AuthorizationAction;
import custis.easyabac.api.attr.annotation.AuthorizationActionId;

import java.util.Arrays;
import java.util.Optional;

@AuthorizationAction(entity = "order")
public enum OrderAction {

    VIEW("view", "View order details"),
    CREATE("create", "Create new order"),
    APPROVE("approve", "Approve existing order"),
    REJECT("reject", "Reject existing order");

    @AuthorizationActionId
    private String id;

    private String title;

    OrderAction(String id, String title) {
        this.id = id;
        this.title = title;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public static OrderAction byId(String id) {
        Optional<OrderAction> optional = Arrays.asList(values()).stream().filter(action -> action.id.equals(id)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new IllegalArgumentException(id);
    }
}