package custis.easyabac.core.trace.interceptors;

import custis.easyabac.core.trace.ProxyImpl;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.finder.PolicyFinder;

public class PolicyFinderResultInterceptor implements MethodInterceptor {

    private ProxyImpl proxy;
    private final PolicyFinder policyFinder;

    public PolicyFinderResultInterceptor(ProxyImpl proxy, PolicyFinder policyFinder) {
        this.proxy = proxy;
        this.policyFinder = policyFinder;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("getPolicy")) {
            Object superInvoke = proxy.invokeSuper(obj, args);
            AbstractPolicy policy = (AbstractPolicy) superInvoke;
            return proxy.createAbstractPolicy(policy, policyFinder);
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

}
