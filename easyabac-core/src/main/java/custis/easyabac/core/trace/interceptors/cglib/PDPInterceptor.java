package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;

import java.lang.reflect.Method;

class PDPInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object realResult = null;


        boolean evaluationCall = method.getName().equals("evaluate") && args.length == 1 && args[0] instanceof RequestCtx;
        if (evaluationCall) {
            BalanaTraceHandlerProvider.get().beforeProcess((RequestCtx) args[0]);
            realResult = proxy.invokeSuper(obj, args);
            BalanaTraceHandlerProvider.get().postProcess((ResponseCtx) realResult);
        } else {
            realResult = proxy.invokeSuper(obj, args);
        }

        return realResult;
    }

}