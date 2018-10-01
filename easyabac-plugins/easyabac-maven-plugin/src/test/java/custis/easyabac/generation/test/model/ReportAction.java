package custis.easyabac.generation.test.model;

import custis.easyabac.api.AuthorizationAction;
import custis.easyabac.api.AuthorizationActionId;

@AuthorizationAction()
public enum ReportAction {

    EDIT("edit", "edit"), VIEW("view", "view"), REMOVE("remove", "remove");

    @AuthorizationActionId()
    private String id;

    private String title;

    private ReportAction(String id, String title) {
        this.id = id;
        this.title = title;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }
}
