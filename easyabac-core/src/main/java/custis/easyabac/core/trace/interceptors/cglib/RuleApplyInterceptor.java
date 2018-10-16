package custis.easyabac.core.trace.interceptors.cglib;

import custis.easyabac.core.trace.balana.BalanaTraceHandlerProvider;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.cond.Expression;

import java.lang.reflect.Method;

class RuleApplyInterceptor implements MethodInterceptor {

    private final Expression expression;

    public RuleApplyInterceptor(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String methodName = method.getName();

        Object realResult = null;


        if (methodName.equals("evaluate")) {
            BalanaTraceHandlerProvider.get().onRuleExpressionStart(expression);
            realResult = method.invoke(expression, args);
            BalanaTraceHandlerProvider.get().onRuleExpressionEnd((EvaluationResult) realResult);
        } else {
            realResult = method.invoke(expression, args);
        }

        return realResult;

    }
}
