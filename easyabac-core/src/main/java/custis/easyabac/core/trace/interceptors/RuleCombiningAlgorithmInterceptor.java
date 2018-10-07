package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInvocation;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

public class RuleCombiningAlgorithmInterceptor extends TraceMethodInterceptor {

    private final CombiningAlgorithm combiningAlg;

    public RuleCombiningAlgorithmInterceptor(CombiningAlgorithm combiningAlg) {
        this.combiningAlg = combiningAlg;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("combine")) {
            handler.onRuleCombineStart((RuleCombiningAlgorithm) combiningAlg);
            realResult = invocation.proceed();
            handler.onRuleCombineEnd((AbstractResult) realResult);
        } else {
            realResult = invocation.proceed();
        }

        return realResult;

    }
}
