package custis.easyabac.core.trace;

import org.wso2.balana.AbstractObligation;
import org.wso2.balana.AbstractTarget;
import org.wso2.balana.Rule;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.xacml3.AdviceExpression;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Set;

public class PolicyElementsFactory {

    private Rule createRule(Rule sourceRule) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Rule.class);
        Class[] constructorClasses = new Class[] {
                URI.class, int.class, String.class, AbstractTarget.class,
                Condition.class, Set.class, Set.class, int.class
        };

        Set<AbstractObligation> obligations = null;
        try {
            Field field = ReflectionUtils.findField(Rule.class, "obligationExpressions");
            field.setAccessible(true);
            obligations = (Set<AbstractObligation>) field.get(sourceRule);
        } catch (Exception e) {
            LOGGER.error("Ошибка при попытке получить значение поля obligationExpressions");
        }

        Set<AdviceExpression> advices = null;
        try {
            Field field = ReflectionUtils.findField(Rule.class, "adviceExpressions");
            field.setAccessible(true);
            advices = (Set<AdviceExpression>) field.get(sourceRule);
        } catch (Exception e) {
            LOGGER.error("Ошибка при попытке получить значение поля adviceExpressions");
        }

        Integer xacmlVersion = XACMLConstants.XACML_VERSION_3_0;
        try {
            Field field = ReflectionUtils.findField(Rule.class, "xacmlVersion");
            field.setAccessible(true);
            xacmlVersion = (Integer) field.get(sourceRule);
        } catch (Exception e) {
            LOGGER.error("Ошибка при попытке получить значение поля xacmlVersion");
        }

        Object[] constructorParameters = new Object[] {
                sourceRule.getId(), sourceRule.getEffect(), sourceRule.getDescription(), sourceRule.getTarget(),
                createCondition(sourceRule.getCondition()), obligations, advices, xacmlVersion

        };
        enhancer.setCallback(new ProxyImpl.RuleInterceptor());
        return (Rule) enhancer.create(constructorClasses, constructorParameters);
    }
}
