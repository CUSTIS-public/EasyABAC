package custis.easyabac.core.trace.interceptors;

import custis.easyabac.core.trace.TraceHandler;
import org.aopalliance.intercept.MethodInterceptor;

public abstract class TraceMethodInterceptor implements MethodInterceptor {
    protected TraceHandler handler;
}
