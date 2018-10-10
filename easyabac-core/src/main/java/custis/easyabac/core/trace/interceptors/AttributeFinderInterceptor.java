package custis.easyabac.core.trace.interceptors;

import custis.easyabac.core.trace.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

public class AttributeFinderInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        if (methodName.equals("findAttribute")) {
            EvaluationResult invokeSuperResult = (EvaluationResult) invocation.proceed();
            BalanaTraceHandlerProvider.get().onFindAttribute(invokeSuperResult);

            return invokeSuperResult;
        } else {
            return invocation.proceed();
        }
    }
}
