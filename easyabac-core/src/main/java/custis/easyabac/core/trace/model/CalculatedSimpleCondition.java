package custis.easyabac.core.trace.model;

import custis.easyabac.model.Condition;

/**
 * Calculated Simple Rule like attribute = value
 */
public class CalculatedSimpleCondition {

    private final int index;
    private Condition condition;
    private CalculatedResult result;


    public CalculatedSimpleCondition(int index) {
        this.index = index;
    }


    public void setResult(CalculatedResult result) {
        this.result = result;
    }



    public CalculatedResult getResult() {
        return result;
    }

    public int getIndex() {
        return index;
    }

    public Condition getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "CalculatedRule{" +
                "index='" + index + '\'' +
                ", result=" + result +
                '}';
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
