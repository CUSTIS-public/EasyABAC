package custis.easyabac.core.trace;

import custis.easyabac.core.trace.model.TraceResult;
import custis.easyabac.model.AbacAuthModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTrace implements Trace {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultTrace.class);
    public static final Trace INSTANCE = new DefaultTrace();

    @Override
    public void handleTrace(AbacAuthModel abacAuthModel, TraceResult traceResult) {

    }
}
