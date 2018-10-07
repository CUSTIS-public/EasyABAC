package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

public class ConditionInterceptor extends TraceMethodInterceptor {

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
            handler.onConditionEvaluateStart(condition);
            realResult = invocation.proceed();
            handler.onConditionEvaluateEnd((EvaluationResult) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;

    }
}
