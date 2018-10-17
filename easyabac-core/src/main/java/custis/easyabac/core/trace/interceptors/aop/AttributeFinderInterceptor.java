package custis.easyabac.core.trace.interceptors.aop;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

class AttributeFinderInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        if (methodName.equals("findAttribute")) {
            EvaluationResult invokeSuperResult = (EvaluationResult) invocation.proceed();
            Object[] args = invocation.getArguments();
            BalanaTraceHandlerProvider.get().onFindAttribute(invokeSuperResult, args[0].toString(), args[1].toString(), args[3].toString());

            return invokeSuperResult;
        } else {
            return invocation.proceed();
        }
    }
}
