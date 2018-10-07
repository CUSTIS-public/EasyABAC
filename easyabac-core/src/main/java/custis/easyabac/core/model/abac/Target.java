package custis.easyabac.core.model.abac;

import java.util.List;


public class Target {
    private Operation operation;
    private List<TargetCondition> conditions;
    private List<String> accessToActions;

    public Target(Operation operation, List<TargetCondition> conditions, List<String> accessToActions) {
        this.operation = operation;
        this.conditions = conditions;
        this.accessToActions = accessToActions;
    }


    public Operation getOperation() {
        return operation;
    }

    public List<TargetCondition> getConditions() {
        return conditions;
    }

    public List<String> getAccessToActions() {
        return accessToActions;
    }
}
