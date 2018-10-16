package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

class PolicyCombiningAlgorithmInterceptor implements MethodInterceptor {

    private final CombiningAlgorithm combiningAlg;

    public PolicyCombiningAlgorithmInterceptor(CombiningAlgorithm combiningAlg) {
        this.combiningAlg = combiningAlg;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("combine")) {
            BalanaTraceHandlerProvider.get().onPolicyCombineStart((PolicyCombiningAlgorithm) combiningAlg);
            realResult = method.invoke(combiningAlg, args);
            BalanaTraceHandlerProvider.get().onPolicyCombineEnd((AbstractResult) realResult);
        } else {
            realResult = method.invoke(combiningAlg, args);
        }

        return realResult;

    }

}
