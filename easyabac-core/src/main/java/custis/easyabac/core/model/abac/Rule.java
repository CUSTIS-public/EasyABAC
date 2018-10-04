package custis.easyabac.core.model.abac;

import java.util.List;

public class Rule {
    private final String id;
    private final String title;
    private final Effect effect = Effect.PERMIT;
    private final Operation operation;
    private final List<Condition> conditions;

    public Rule(String id, String title, Operation operation, List<Condition> conditions) {
        this.id = id;
        this.title = title;
        this.operation = operation;
        this.conditions = conditions;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Effect getEffect() {
        return effect;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<Condition> getConditions() {
        return conditions;
    }
}
