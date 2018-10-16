package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Rule;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

class RuleInterceptor implements MethodInterceptor {

    private final Rule rule;

    public RuleInterceptor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onRuleEvaluateStart(rule);
            realResult = method.invoke(rule, args);
            BalanaTraceHandlerProvider.get().onRuleEvaluateEnd((AbstractResult) realResult);
        } else if (methodName.equals("match")) {
            BalanaTraceHandlerProvider.get().onRuleMatchStart(rule);
            realResult = method.invoke(rule, args);
            BalanaTraceHandlerProvider.get().onRuleMatchEnd((MatchResult) realResult);
        } else {
            realResult = method.invoke(rule, args);
        }

        return realResult;

    }

}
