package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.EvaluationResult;

import java.lang.reflect.Method;
import java.net.URI;

public class AttributeFinderInterceptor implements MethodInterceptor {

    /*@Override*/
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("findAttribute")) {
            EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();
            EvaluationResult invokeSuperResult = (EvaluationResult) proxy.invokeSuper(obj, args);
            handler.onFindAttribute((URI) args[0], (URI) args[1], (URI) args[3], invokeSuperResult);

            return invokeSuperResult;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return null;
    }
}
