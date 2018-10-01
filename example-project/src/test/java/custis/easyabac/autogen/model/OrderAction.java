package custis.easyabac.autogen.model;

import custis.easyabac.api.AuthorizationAction;
import custis.easyabac.api.AuthorizationActionId;

@AuthorizationAction()
public enum OrderAction {

    VIEW("view"), CREATE("create"), APPROVE("approve"), REJECT("reject");

    @AuthorizationActionId()
    private String id;

    private OrderAction(String id) {
        this.id = id;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }
}
