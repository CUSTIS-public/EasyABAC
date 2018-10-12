package custis.easyabac.core.trace.interceptors;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

public class AbstractPolicyInterceptor implements MethodInterceptor {

    private final AbstractPolicy policy;

    public AbstractPolicyInterceptor(AbstractPolicy policy) {
        this.policy = policy;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        Object realResult = null;

        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onPolicyEvaluateStart(policy);
            realResult = invocation.proceed();
            BalanaTraceHandlerProvider.get().onPolicyEvaluateEnd((AbstractResult) realResult);
        } else if (methodName.equals("match")) {
            BalanaTraceHandlerProvider.get().onPolicyMatchStart(policy);
            realResult = invocation.proceed();
            BalanaTraceHandlerProvider.get().onPolicyMatchEnd((MatchResult) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;
    }

}
