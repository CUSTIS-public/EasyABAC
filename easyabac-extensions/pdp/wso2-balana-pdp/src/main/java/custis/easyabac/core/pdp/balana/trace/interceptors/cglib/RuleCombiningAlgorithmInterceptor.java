package custis.easyabac.core.pdp.balana.trace.interceptors.cglib;

import custis.easyabac.core.pdp.balana.trace.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

class RuleCombiningAlgorithmInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("combine")) {
            BalanaTraceHandlerProvider.get().onRuleCombineStart((RuleCombiningAlgorithm) obj);
            realResult = proxy.invokeSuper(obj, args);
            BalanaTraceHandlerProvider.get().onRuleCombineEnd((AbstractResult) realResult);
        } else {
            realResult = proxy.invokeSuper(obj, args);
        }

        return realResult;

    }
}
