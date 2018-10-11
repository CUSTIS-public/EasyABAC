package custis.easyabac.core.trace;

import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.trace.model.TraceResult;

public class DefaultTrace implements Trace {

    public static final Trace INSTANCE = new DefaultTrace();

    @Override
    public void handleTrace(AbacAuthModel abacAuthModel, TraceResult traceResult) {
        System.out.println(traceResult);
    }
}
