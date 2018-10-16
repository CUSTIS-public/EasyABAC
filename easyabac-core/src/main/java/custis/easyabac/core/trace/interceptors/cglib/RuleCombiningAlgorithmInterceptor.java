package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

class RuleCombiningAlgorithmInterceptor implements MethodInterceptor {

    private final RuleCombiningAlgorithm combiningAlg;

    public RuleCombiningAlgorithmInterceptor(RuleCombiningAlgorithm combiningAlg) {
        this.combiningAlg = combiningAlg;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("combine")) {
            BalanaTraceHandlerProvider.get().onRuleCombineStart(combiningAlg);
            realResult = method.invoke(combiningAlg, args);
            BalanaTraceHandlerProvider.get().onRuleCombineEnd((AbstractResult) realResult);
        } else {
            realResult = method.invoke(combiningAlg, args);
        }

        return realResult;

    }
}
