package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

public class AbstractPolicyInterceptor extends TraceMethodInterceptor {

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
            handler.onPolicyEvaluateStart(policy);
            realResult = invocation.proceed();
            handler.onPolicyEvaluateEnd((AbstractResult) realResult);
        } else if (methodName.equals("match")) {
            handler.onPolicyMatchStart(policy);
            realResult = invocation.proceed();
            handler.onPolicyMatchEnd((MatchResult) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;
    }

}
