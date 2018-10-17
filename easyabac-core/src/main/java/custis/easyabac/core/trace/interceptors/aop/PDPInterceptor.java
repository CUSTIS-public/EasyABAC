package custis.easyabac.core.trace.interceptors.aop;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;

import java.lang.reflect.Method;

class PDPInterceptor implements MethodInterceptor {

    private final PDPConfig pdpConfig;

    public PDPInterceptor(PDPConfig pdpConfig) {
        this.pdpConfig = pdpConfig;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        Object realResult = null;

        Object[] args = invocation.getArguments();


        boolean evaluationCall = method.getName().equals("evaluate") && args.length == 1 && args[0] instanceof RequestCtx;
        if (evaluationCall) {
            BalanaTraceHandlerProvider.get().beforeProcess((RequestCtx) args[0]);
            realResult = invocation.proceed();
            BalanaTraceHandlerProvider.get().postProcess((ResponseCtx) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;

    }

}