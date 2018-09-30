package custis.easyabac.core.trace.interceptors;

import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;

public class AbstractPolicyInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();
        AbstractPolicy abstractPolicy = (AbstractPolicy) obj;
        Object realResult = null;

        EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();

        if (methodName.equals("evaluate")) {
            handler.onPolicyEvaluateStart(abstractPolicy);
            realResult = proxy.invokeSuper(obj, args);
            handler.onPolicyEvaluateEnd((AbstractResult) realResult);
        } else if (methodName.equals("match")) {
            handler.onPolicyMatchStart(abstractPolicy);
            realResult = proxy.invokeSuper(obj, args);
            handler.onPolicyMatchEnd((MatchResult) realResult);
        } else {
            realResult = proxy.invokeSuper(obj, args);
        }

        return realResult;
    }

}
