package custis.easyabac.core.model.policy;

import java.util.List;

public class Target {
    private Operation operation;
    private List<TargetCondition> conditions;

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
