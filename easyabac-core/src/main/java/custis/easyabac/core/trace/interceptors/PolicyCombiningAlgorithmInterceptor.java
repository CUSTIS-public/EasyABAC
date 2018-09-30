package custis.easyabac.core.trace.interceptors;

import org.wso2.balana.combine.PolicyCombiningAlgorithm;

public class PolicyCombiningAlgorithmInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("combine")) {
            EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();
            handler.onPolicyCombineStart((PolicyCombiningAlgorithm) obj);
            Object invokeSuperResult = proxy.invokeSuper(obj, args);
            handler.onPolicyCombineEnd((AbstractResult) invokeSuperResult);
            return invokeSuperResult;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }
}
