package custis.easyabac.core.trace.interceptors;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

public class ConditionInterceptor implements MethodInterceptor {

    private final Condition condition;

    public ConditionInterceptor(Condition condition) {
        this.condition = condition;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onConditionEvaluateStart(condition);
            realResult = invocation.proceed();
            BalanaTraceHandlerProvider.get().onConditionEvaluateEnd((EvaluationResult) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;

    }
}
