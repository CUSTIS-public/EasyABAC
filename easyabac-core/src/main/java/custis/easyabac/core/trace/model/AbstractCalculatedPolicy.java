package custis.easyabac.core.trace.model;

public abstract class AbstractCalculatedPolicy {

    private final String id;
    private CalculatedResult result;
    private CalculatedMatch match;
    private CalculatedResult combinationResult;

    public AbstractCalculatedPolicy(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMatch(CalculatedMatch match) {
        this.match = match;
    }

    public void setResult(CalculatedResult result) {
        this.result = result;
    }

    public void setCombinationResult(CalculatedResult combinationResult) {
        this.combinationResult = combinationResult;
    }
}
