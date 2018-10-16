package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

class ConditionInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();
        Condition condition = (Condition) o;
        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onConditionEvaluateStart(condition);
            realResult = proxy.invokeSuper(condition, args);
            BalanaTraceHandlerProvider.get().onConditionEvaluateEnd((EvaluationResult) realResult);
        } else {
            realResult = proxy.invokeSuper(condition, args);
        }

        return realResult;

    }
}
