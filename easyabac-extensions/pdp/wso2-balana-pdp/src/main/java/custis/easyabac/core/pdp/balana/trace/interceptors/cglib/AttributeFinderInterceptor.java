package custis.easyabac.core.pdp.balana.trace.interceptors.cglib;

import custis.easyabac.core.pdp.balana.trace.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;

class AttributeFinderInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();

        if (methodName.equals("findAttribute")) {
            EvaluationResult invokeSuperResult = (EvaluationResult) proxy.invokeSuper(obj, args);
            BalanaTraceHandlerProvider.get().onFindAttribute(invokeSuperResult, args[0].toString(), args[1].toString(), args[3].toString());

            return invokeSuperResult;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }
}
