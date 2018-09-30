package custis.easyabac.core.trace.interceptors;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.ctx.AbstractResult;

import java.lang.reflect.Method;

public class RuleCombiningAlgorithmInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("combine")) {
            EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();
            handler.onRuleCombineStart((RuleCombiningAlgorithm) obj);
            Object invokeSuperResult = proxy.invokeSuper(obj, args);
            handler.onRuleCombineEnd((AbstractResult) invokeSuperResult);
            return invokeSuperResult;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }
}
