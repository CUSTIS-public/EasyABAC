package custis.easyabac.core.trace;

import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.trace.model.TraceResult;

public interface Trace {

    void handleTrace(AbacAuthModel abacAuthModel, TraceResult traceResult);

}
