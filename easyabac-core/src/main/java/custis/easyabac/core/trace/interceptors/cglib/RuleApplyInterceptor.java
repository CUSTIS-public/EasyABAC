package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.Expression;

import java.lang.reflect.Method;

class RuleApplyInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onRuleExpressionStart((Expression) obj);
            realResult = proxy.invokeSuper(obj, args);
            BalanaTraceHandlerProvider.get().onRuleExpressionEnd((EvaluationResult) realResult);
        } else {
            realResult = proxy.invokeSuper(obj, args);
        }

        return realResult;

    }
}
