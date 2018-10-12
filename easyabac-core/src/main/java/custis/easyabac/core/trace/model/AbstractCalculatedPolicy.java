package custis.easyabac.core.trace.model;

import java.net.URI;

public abstract class AbstractCalculatedPolicy {

    protected final URI id;
    protected CalculatedResult result;
    protected CalculatedMatch match;
    protected CalculatedResult combinationResult;

    public AbstractCalculatedPolicy(URI id) {
        this.id = id;
    }

    public URI getId() {
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

    public CalculatedResult getResult() {
        return result;
    }

    public CalculatedMatch getMatch() {
        return match;
    }

    public CalculatedResult getCombinationResult() {
        return combinationResult;
    }
}
