package custis.easyabac.core.trace;

import custis.easyabac.core.trace.model.TraceResult;
import custis.easyabac.model.AbacAuthModel;

public interface Trace {

    void handleTrace(AbacAuthModel abacAuthModel, TraceResult traceResult);

}
