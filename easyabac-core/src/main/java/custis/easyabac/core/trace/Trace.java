package custis.easyabac.core.trace;

import custis.easyabac.core.trace.model.TraceResult;

public interface Trace {

    void handleTrace(TraceResult traceResult);

}
