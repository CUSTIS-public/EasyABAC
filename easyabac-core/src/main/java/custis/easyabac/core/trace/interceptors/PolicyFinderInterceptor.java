package custis.easyabac.core.trace.interceptors;

import custis.easyabac.core.trace.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderResult;

import java.lang.reflect.Method;

import static custis.easyabac.core.trace.PolicyElementsFactory.createPolicyFinderResult;

public class PolicyFinderInterceptor implements MethodInterceptor {

    private final PolicyFinder policyFinder;

    public PolicyFinderInterceptor() {
        this.policyFinder = null;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        if (methodName.equals("findPolicy")) {
            BalanaTraceHandlerProvider.get().onFindPolicyStart();
            Object invokeSuperResult = invocation.proceed();
            PolicyFinderResult policyFinderResult = (PolicyFinderResult) invokeSuperResult;
            BalanaTraceHandlerProvider.get().onFindPolicyEnd(policyFinderResult);
            return createPolicyFinderResult(policyFinderResult, policyFinder);
        } else {
            return invocation.proceed();
        }
    }

}
