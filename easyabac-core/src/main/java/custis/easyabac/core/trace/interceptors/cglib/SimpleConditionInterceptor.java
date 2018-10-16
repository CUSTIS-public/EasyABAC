package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

class SimpleConditionInterceptor implements MethodInterceptor {

    private final int index;

    public SimpleConditionInterceptor(int index) {
        this.index = index;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onSimpleConditionStart(index);
            realResult = methodProxy.invokeSuper(o, args);
            BalanaTraceHandlerProvider.get().onSimpleCondition((EvaluationResult) realResult);
        } else {
            realResult = methodProxy.invokeSuper(o, args);
        }

        return realResult;

    }
}
