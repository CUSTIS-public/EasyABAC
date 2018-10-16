package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.PDP;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;

import java.lang.reflect.Method;

class PDPInterceptor implements MethodInterceptor {

    private final PDP pdp;

    public PDPInterceptor(PDP pdp) {
        this.pdp = pdp;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object realResult = null;


        boolean evaluationCall = method.getName().equals("evaluate") && args.length == 1 && args[0] instanceof RequestCtx;
        if (evaluationCall) {
            BalanaTraceHandlerProvider.get().beforeProcess((RequestCtx) args[0]);
            realResult = method.invoke(pdp, args);
            BalanaTraceHandlerProvider.get().postProcess((ResponseCtx) realResult);
        } else {
            realResult = method.invoke(pdp, args);
        }

        return realResult;
    }

}