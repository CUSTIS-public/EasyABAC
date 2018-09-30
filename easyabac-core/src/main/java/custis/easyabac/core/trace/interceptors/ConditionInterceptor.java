package custis.easyabac.core.trace.interceptors;

import org.wso2.balana.cond.Condition;

public class ConditionInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();
        Condition condition = (Condition) obj;
        Object realResult = null;

        EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();

        if (methodName.equals("evaluate")) {
            handler.onConditionEvaluateStart(condition);
            realResult = proxy.invokeSuper(obj, args);
            handler.onConditionEvaluateEnd((EvaluationResult) realResult);
        } else {
            realResult = proxy.invokeSuper(obj, args);
        }



        return realResult;
    }

}
