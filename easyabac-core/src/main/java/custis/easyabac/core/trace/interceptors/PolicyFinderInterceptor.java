package custis.easyabac.core.trace.interceptors;

import custis.easyabac.core.trace.ProxyImpl;
import org.wso2.balana.finder.PolicyFinder;

public class PolicyFinderInterceptor implements MethodInterceptor {

    private ProxyImpl proxy;

    public PolicyFinderInterceptor(ProxyImpl proxy) {
        this.proxy = proxy;
    }

    /*@Override*/
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("findPolicy")) {
            EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();
            handler.onFindPolicyStart();
            Object invokeSuperResult = proxy.invokeSuper(obj, args);
            PolicyFinderResult policyFinderResult = (PolicyFinderResult) invokeSuperResult;
            handler.onFindPolicyEnd(policyFinderResult);
            return proxy.createPolicyFinderResult(policyFinderResult, (PolicyFinder) obj);
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

}
