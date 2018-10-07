package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Rule;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

public class RuleInterceptor extends TraceMethodInterceptor {

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
            handler.onRuleEvaluateStart(rule);
            realResult = invocation.proceed();
            handler.onRuleEvaluateEnd((AbstractResult) realResult);
        } else if (methodName.equals("match")) {
            handler.onRuleMatchStart(rule);
            realResult = invocation.proceed();
            handler.onRuleMatchEnd((MatchResult) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;

    }

}
