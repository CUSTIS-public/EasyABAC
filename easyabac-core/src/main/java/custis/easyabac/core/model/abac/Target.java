package custis.easyabac.core.model.abac;

import java.util.List;


public class Target {
    private String id;
    private Operation operation;
    private List<TargetCondition> conditions;
    private List<String> accessToActions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAccessToActions() {
        return accessToActions;
    }

    public void setAccessToActions(List<String> accessToActions) {
        this.accessToActions = accessToActions;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public List<TargetCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<TargetCondition> conditions) {
        this.conditions = conditions;
    }
}
