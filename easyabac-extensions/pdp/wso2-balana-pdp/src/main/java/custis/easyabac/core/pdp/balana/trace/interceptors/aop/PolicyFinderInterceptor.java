package custis.easyabac.core.pdp.balana.trace.interceptors.aop;

import custis.easyabac.core.pdp.balana.trace.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderResult;

import java.lang.reflect.Method;

class PolicyFinderInterceptor implements MethodInterceptor {

    private final PolicyFinder policyFinder;

    public PolicyFinderInterceptor() {
        this.policyFinder = null;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        if (methodName.equals("findPolicy")) {
            BalanaTraceHandlerProvider.get().onFindPolicyStart((EvaluationCtx) invocation.getArguments()[0]);
            Object invokeSuperResult = invocation.proceed();
            PolicyFinderResult policyFinderResult = (PolicyFinderResult) invokeSuperResult;
            BalanaTraceHandlerProvider.get().onFindPolicyEnd(policyFinderResult);
            return policyFinderResult;
        } else {
            return invocation.proceed();
        }
    }

}
