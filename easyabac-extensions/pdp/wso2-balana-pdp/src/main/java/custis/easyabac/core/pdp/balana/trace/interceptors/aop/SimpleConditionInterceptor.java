package custis.easyabac.core.pdp.balana.trace.interceptors.aop;

import custis.easyabac.core.pdp.balana.trace.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

class SimpleConditionInterceptor implements MethodInterceptor {

    private final int index;

    public SimpleConditionInterceptor(int index) {
        this.index = index;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onSimpleConditionStart(index);
            realResult = invocation.proceed();
            BalanaTraceHandlerProvider.get().onSimpleCondition((EvaluationResult) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;

    }
}
