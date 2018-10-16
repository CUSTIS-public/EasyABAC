package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderResult;

import java.lang.reflect.Method;

class PolicyFinderInterceptor implements MethodInterceptor {

    private final PolicyFinder policyFinder;

    public PolicyFinderInterceptor(PolicyFinder policyFinder) {
        this.policyFinder = policyFinder;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        if (methodName.equals("findPolicy")) {
            BalanaTraceHandlerProvider.get().onFindPolicyStart((EvaluationCtx) args[0]);
            Object invokeSuperResult = method.invoke(policyFinder, args);
            PolicyFinderResult policyFinderResult = (PolicyFinderResult) invokeSuperResult;
            BalanaTraceHandlerProvider.get().onFindPolicyEnd(policyFinderResult);
            return policyFinderResult;
        } else {
            return method.invoke(policyFinder, args);
        }
    }

}
