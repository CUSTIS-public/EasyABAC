package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

public class AttributeFinderInterceptor extends TraceMethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        if (methodName.equals("findAttribute")) {
            EvaluationResult invokeSuperResult = (EvaluationResult) invocation.proceed();
            handler.onFindAttribute(invokeSuperResult);

            return invokeSuperResult;
        } else {
            return invocation.proceed();
        }
    }
}
