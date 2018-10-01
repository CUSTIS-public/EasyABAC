package custis.easyabac.generation.test.model;

import custis.easyabac.api.AuthorizationAction;
import custis.easyabac.api.AuthorizationActionId;

@AuthorizationAction()
public enum OrderAction {

    WRITE;

    /**
     * Authorization attribute "Действие"
     */
    @AuthorizationActionId
    private String action;
}
