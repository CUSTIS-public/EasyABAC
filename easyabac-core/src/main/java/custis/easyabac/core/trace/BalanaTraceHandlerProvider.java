package custis.easyabac.core.trace;

/**
 * Holder of Trace process
 */
public class BalanaTraceHandlerProvider {

    private static ThreadLocal<BalanaTraceHandler> THREAD_LOCAL = new ThreadLocal<>();

    public static BalanaTraceHandler instantiate(Trace trace) {
        BalanaTraceHandler result = new BalanaTraceHandler(trace);
        THREAD_LOCAL.set(result);
        return result;
    }

    public static BalanaTraceHandler get() {
        if (THREAD_LOCAL.get() != null) {
            return THREAD_LOCAL.get();
        }
        throw new IllegalStateException("Thread local not instantiated");
    }

}
