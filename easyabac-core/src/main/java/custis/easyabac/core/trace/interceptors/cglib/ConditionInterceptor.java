package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

class ConditionInterceptor implements MethodInterceptor {

    private final Condition condition;

    public ConditionInterceptor(Condition condition) {
        this.condition = condition;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onConditionEvaluateStart(condition);
            method.invoke(condition, args);
            BalanaTraceHandlerProvider.get().onConditionEvaluateEnd((EvaluationResult) realResult);
        } else {
            method.invoke(condition, args);
        }

        return realResult;

    }
}
