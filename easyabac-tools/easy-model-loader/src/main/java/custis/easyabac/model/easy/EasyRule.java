package custis.easyabac.model.easy;

import custis.easyabac.model.Effect;
import custis.easyabac.model.Operation;

import java.util.Collections;
import java.util.List;

public class EasyRule {
    private String title;
    private Effect effect = Effect.PERMIT;
    private Operation operation = Operation.AND;
    private List<String> conditions = Collections.emptyList();

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }


    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
