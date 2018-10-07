package custis.easyabac.core.trace;

import custis.easyabac.core.trace.interceptors.*;
import org.springframework.aop.framework.ProxyFactory;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.Rule;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.finder.*;

import java.util.List;
import java.util.Set;

public class PolicyElementsFactory {

    public static PDPConfig newPDPConfig(Set<PolicyFinderModule> policyFinderModules, List<AttributeFinderModule> attributeFinderModules) {
        AttributeFinder attributeFinder = newAttributeFinder();
        attributeFinder.setModules(attributeFinderModules);

        PolicyFinder policyFinder = newPolicyFinder();
        policyFinder.setModules(policyFinderModules);
        return new PDPConfig(attributeFinder, policyFinder, null);
    }

    /*@Override*/
    public static PDP newPDP(PDPConfig pdpConfig) {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(PDP.class);
        result.addAdvice(new PDPInterceptor(pdpConfig));

        return (PDP) result.getProxy();
    }

    public static PolicyFinder newPolicyFinder() {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(PolicyFinder.class);
        result.addAdvice(new PolicyFinderInterceptor());

        return (PolicyFinder) result.getProxy();
    }

    public static AttributeFinder newAttributeFinder() {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(AttributeFinder.class);
        result.addAdvice(new AttributeFinderInterceptor());

        return (AttributeFinder) result.getProxy();
    }

    public static CombiningAlgorithm createPolicyCombiningAlgorithm(CombiningAlgorithm combiningAlg) {
        if (combiningAlg == null) {
            return combiningAlg;
        }
        ProxyFactory result = new ProxyFactory();
        result.setTarget(RuleCombiningAlgorithm.class);
        result.addAdvice(new PolicyCombiningAlgorithmInterceptor(combiningAlg));

        return (CombiningAlgorithm) result.getProxy();
    }


    public static Rule createRule(Rule sourceRule) {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(Rule.class);
        result.addAdvice(new RuleInterceptor(sourceRule));
        /*
        Enhancer enhancer = new Enhancer();
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

        };*/

        return (Rule) result.getProxy();
    }

    public  static CombiningAlgorithm createRuleCombiningAlgorithm(CombiningAlgorithm combiningAlg) {
        if (combiningAlg == null) {
            return combiningAlg;
        }
        ProxyFactory result = new ProxyFactory();
        result.setTarget(RuleCombiningAlgorithm.class);
        result.addAdvice(new RuleCombiningAlgorithmInterceptor(combiningAlg));

        return (RuleCombiningAlgorithm) result.getProxy();
    }

    private Condition createCondition(Condition condition) {
        if (condition == null) {
            return condition;
        }
        ProxyFactory result = new ProxyFactory();
        result.setTarget(Condition.class);
        result.addAdvice(new ConditionInterceptor(condition));


       /* boolean isVersionOne = condition.getFunction() != null;

        enhancer.setCallback(new ConditionInterceptor());
        if (isVersionOne) {
            return (Condition) enhancer.create(new Class[] {Function.class, List.class}, new Object[] { condition.getFunction(), condition.getChildren()});
        } else {
            Expression expression = null;
            try {
                Field field = ReflectionUtils.findField(Condition.class, "expression");
                field.setAccessible(true);
                expression = (Expression) field.get(condition);
            } catch (Exception e) {
                LOGGER.error("Ошибка при попытке получить значение поля expression");
                return condition;
            }
            return (Condition) enhancer.create(new Class[] {Expression.class}, new Object[] { expression});
        }
*/
        return (Condition) result.getProxy();
    }

    public static PolicyFinderResult createPolicyFinderResult(PolicyFinderResult policyFinderResult, PolicyFinder policyFinder) {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(Condition.class);
        result.addAdvice(new PolicyFinderResultInterceptor(policyFinderResult, policyFinder));

        /*Object proxyObject = null;
        if (policyFinderResult.getPolicy() != null) {
            proxyObject = enhancer.create(new Class[]{AbstractPolicy.class}, new Object[]{policyFinderResult.getPolicy()});
        } else {
            if (policyFinderResult.getStatus() != null) {
                proxyObject = enhancer.create(new Class[]{Status.class}, new Object[]{policyFinderResult.getStatus()});
            } else {
                proxyObject = enhancer.create();
            }
        }
        PolicyFinderResult proxiedResult = (PolicyFinderResult) proxyObject;
        */

        return (PolicyFinderResult) result.getProxy();
    }

    public static AbstractPolicy createAbstractPolicy(AbstractPolicy policy, final PolicyFinder policyFinder) {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(AbstractPolicy.class);
        result.addAdvice(new AbstractPolicyInterceptor(policy));


        /*Class[] constructorClasses = null;
        Object[] constructorParameters = null;
        if (policy instanceof Policy) {
            enhancer.setSuperclass(Policy.class);
            constructorClasses = new Class[] {
                    URI.class, String.class, RuleCombiningAlgorithm.class, String.class,
                    AbstractTarget.class, String.class, List.class,
                    Set.class, Set.class
            };

            List<PolicyTreeElement> rules = policy.getChildren();
            List<Rule> proxiedPolicies = new ArrayList<Rule>(rules.size());
            for (PolicyTreeElement policyTreeElement : rules) {
                proxiedPolicies.add(createRule((Rule) policyTreeElement));
            }

            constructorParameters = new Object[] {
                    policy.getId(), policy.getVersion(), createRuleCombiningAlgorithm(policy.getCombiningAlg()), policy.getDescription(),
                    policy.getTarget(), policy.getDefaultVersion(), proxiedPolicies,
                    policy.getObligationExpressions(), ((Policy) policy).getVariableDefinitions()

            };

        } else  if (policy instanceof PolicySet) {
            enhancer.setSuperclass(PolicySet.class);
            constructorClasses = new Class[] {
                    URI.class, String.class, PolicyCombiningAlgorithm.class, String.class,
                    AbstractTarget.class, List.class, String.class,
                    Set.class
            };

            List<PolicyTreeElement> policies = policy.getChildren();
            List<AbstractPolicy> proxiedPolicies = new ArrayList<AbstractPolicy>(policies.size());
            for (PolicyTreeElement policyTreeElement : policies) {
                proxiedPolicies.add(createAbstractPolicy((AbstractPolicy) policyTreeElement, policyFinder));
            }

            constructorParameters = new Object[] {
                    policy.getId(), policy.getVersion(), createPolicyCombiningAlgorithm(policy.getCombiningAlg()), policy.getDescription(),
                    policy.getTarget(), proxiedPolicies, policy.getDefaultVersion(),
                    policy.getObligationExpressions()
            };
        } else if (policy instanceof PolicyReference) {
            enhancer.setSuperclass(PolicyReference.class);

            constructorClasses = new Class[] {
                    URI.class, int.class, VersionConstraints.class, PolicyFinder.class, PolicyMetaData.class
            };

            constructorParameters = new Object[] {
                    policy.getId(), ((PolicyReference) policy).getReferenceType(), ((PolicyReference) policy).getConstraints(),
                    policyFinder, policy.getMetaData()
            };
        } else {
            LOGGER.error("Неизвестная реализация AbstractPolicy");
            return policy;
        }

        enhancer.setCallback(new AbstractPolicyInterceptor());

        return (AbstractPolicy) enhancer.create(constructorClasses, constructorParameters);*/
        return (AbstractPolicy) result.getProxy();
    }

}
