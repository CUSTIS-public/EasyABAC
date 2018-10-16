package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.Expression;

import java.lang.reflect.Method;

class SimpleConditionInterceptor implements MethodInterceptor {

    private final Expression expression;
    private final int index;

    public SimpleConditionInterceptor(Expression expression, int index) {
        this.expression = expression;
        this.index = index;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onSimpleConditionStart(index);
            realResult = method.invoke(expression, args);
            BalanaTraceHandlerProvider.get().onSimpleCondition((EvaluationResult) realResult);
        } else {
            realResult = method.invoke(expression, args);
        }

        return realResult;

    }
}
