package custis.easyabac.generation.test.model;

import custis.easyabac.api.AuthorizationAction;
import custis.easyabac.api.AuthorizationActionId;

@AuthorizationAction()
public enum ReportAction {

    EDIT("edit"), VIEW("view"), REMOVE("remove");

    @AuthorizationActionId()
    private String id;

    private ReportAction(String id) {
        this.id = id;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }
}
