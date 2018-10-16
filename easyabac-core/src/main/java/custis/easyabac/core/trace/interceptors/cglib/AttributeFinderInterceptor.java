package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.finder.AttributeFinder;

import java.lang.reflect.Method;

class AttributeFinderInterceptor implements MethodInterceptor {

    private final AttributeFinder attributeFinder;

    public AttributeFinderInterceptor(AttributeFinder attributeFinder) {
        this.attributeFinder = attributeFinder;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        if (methodName.equals("findAttribute")) {
            EvaluationResult invokeSuperResult = (EvaluationResult) method.invoke(attributeFinder, args);
            BalanaTraceHandlerProvider.get().onFindAttribute(invokeSuperResult, args[0].toString(), args[1].toString(), args[3].toString());

            return invokeSuperResult;
        } else {
            return method.invoke(attributeFinder, args);
        }
    }
}
