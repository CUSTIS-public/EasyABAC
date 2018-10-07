package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.ResponseCtx;

import java.lang.reflect.Method;

public class PDPInterceptor extends TraceMethodInterceptor {

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


        boolean evaluationCall = method.getName().equals("evaluate") && args.length == 1 && args[0] instanceof EvaluationCtx;
        if (evaluationCall) {
            handler.beforeProcess((EvaluationCtx) args[0]);
            realResult = invocation.proceed();
            handler.postProcess((ResponseCtx) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;

    }

}