package custis.easyabac.core.trace.interceptors;

public class PDPInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        boolean evaluationCall = method.getName().equals("evaluate") && args.length == 1 && args[0] instanceof EvaluationCtx;
        if (evaluationCall) {
            EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();
            handler.beforeProcess((EvaluationCtx) args[0]);
            Object result = proxy.invokeSuper(obj, args);
            handler.postProcess((ResponseCtx) result);
            return result;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }
}
