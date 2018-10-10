package custis.easyabac.core.trace.interceptors;

import custis.easyabac.core.trace.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Rule;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

public class RuleInterceptor implements MethodInterceptor {

    private final Rule rule;

    public RuleInterceptor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onRuleEvaluateStart(rule);
            realResult = invocation.proceed();
            BalanaTraceHandlerProvider.get().onRuleEvaluateEnd((AbstractResult) realResult);
        } else if (methodName.equals("match")) {
            BalanaTraceHandlerProvider.get().onRuleMatchStart(rule);
            realResult = invocation.proceed();
            BalanaTraceHandlerProvider.get().onRuleMatchEnd((MatchResult) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;

    }

}
