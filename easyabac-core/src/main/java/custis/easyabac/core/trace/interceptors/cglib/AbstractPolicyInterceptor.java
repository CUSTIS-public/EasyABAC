package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

class AbstractPolicyInterceptor implements MethodInterceptor {

    private final AbstractPolicy policy;

    public AbstractPolicyInterceptor(AbstractPolicy policy) {
        this.policy = policy;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();
        AbstractPolicy abstractPolicy = (AbstractPolicy) obj;
        Object realResult = null;

        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onPolicyEvaluateStart(abstractPolicy);
            realResult = proxy.invokeSuper(obj, args);
            BalanaTraceHandlerProvider.get().onPolicyEvaluateEnd((AbstractResult) realResult);
        } else if (methodName.equals("match")) {
            BalanaTraceHandlerProvider.get().onPolicyMatchStart(abstractPolicy);
            realResult = proxy.invokeSuper(obj, args);
            BalanaTraceHandlerProvider.get().onPolicyMatchEnd((MatchResult) realResult);
        } else {
            realResult = proxy.invokeSuper(obj, args);
        }

        return realResult;
    }

}
