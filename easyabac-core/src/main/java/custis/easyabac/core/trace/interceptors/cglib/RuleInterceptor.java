package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandler;
import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Rule;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

class RuleInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();
        Rule rule = (Rule) obj;
        Object realResult = null;

        BalanaTraceHandler handler = BalanaTraceHandlerProvider.get();

        if (methodName.equals("evaluate")) {
            handler.onRuleEvaluateStart(rule);
            realResult = proxy.invokeSuper(obj, args);
            handler.onRuleEvaluateEnd((AbstractResult) realResult);
        } else if (methodName.equals("match")) {
            handler.onRuleMatchStart(rule);
            realResult = proxy.invokeSuper(obj, args);
            handler.onRuleMatchEnd((MatchResult) realResult);
        } else {
            realResult = proxy.invokeSuper(obj, args);
        }



        return realResult;
    }

}
