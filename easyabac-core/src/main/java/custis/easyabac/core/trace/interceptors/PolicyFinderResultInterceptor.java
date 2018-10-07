package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderResult;

import java.lang.reflect.Method;

import static custis.easyabac.core.trace.PolicyElementsFactory.createAbstractPolicy;

public class PolicyFinderResultInterceptor extends TraceMethodInterceptor {

    private final PolicyFinderResult policyFinderResult;
    private final PolicyFinder policyFinder;

    public PolicyFinderResultInterceptor(PolicyFinderResult policyFinderResult, PolicyFinder policyFinder) {
        this.policyFinderResult = policyFinderResult;
        this.policyFinder = policyFinder;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        if (methodName.equals("getPolicy")) {
            Object superInvoke = invocation.proceed();
            AbstractPolicy policy = (AbstractPolicy) superInvoke;
            return createAbstractPolicy(policy, policyFinder);
        } else {
            return invocation.proceed();
        }
    }

}
