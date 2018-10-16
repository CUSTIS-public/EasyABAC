package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.PolicyFinderResult;

import java.lang.reflect.Method;

class PolicyFinderInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();

        if (methodName.equals("findPolicy")) {
            BalanaTraceHandlerProvider.get().onFindPolicyStart((EvaluationCtx) args[0]);
            Object invokeSuperResult = proxy.invokeSuper(obj, args);
            PolicyFinderResult policyFinderResult = (PolicyFinderResult) invokeSuperResult;
            BalanaTraceHandlerProvider.get().onFindPolicyEnd(policyFinderResult);
            return policyFinderResult;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

}
