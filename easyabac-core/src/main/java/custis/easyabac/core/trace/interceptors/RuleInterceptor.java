package custis.easyabac.core.trace.interceptors;

import org.wso2.balana.MatchResult;
import org.wso2.balana.Rule;

public class RuleInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();
        Rule rule = (Rule) obj;
        Object realResult = null;

        EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();

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
