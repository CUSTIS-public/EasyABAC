package custis.easyabac.core.trace;

import custis.easyabac.core.trace.interceptors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.wso2.balana.*;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.finder.*;
import org.wso2.balana.xacml3.AdviceExpression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PolicyElementsFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyElementsFactory.class);

    public static PDP newPDP(Set<PolicyFinderModule> policyFinderModules, List<AttributeFinderModule> attributeFinderModules, boolean useProxy) {
        if (useProxy) {
            PDPConfig pdpConfig = newPDPConfig(policyFinderModules, attributeFinderModules);

            ProxyFactory result = new ProxyFactory();
            result.setTarget(new PDP(pdpConfig));
            result.addAdvice(new PDPInterceptor(pdpConfig));

            return (PDP) result.getProxy();
        } else {
            PDPConfig pdpConfig = Balana.getInstance().getPdpConfig();
            pdpConfig.getAttributeFinder().setModules(attributeFinderModules);
            pdpConfig.getPolicyFinder().setModules(policyFinderModules);
            return new PDP(pdpConfig);
        }
    }

    /*@Override*/
    private static PDPConfig newPDPConfig(Set<PolicyFinderModule> policyFinderModules, List<AttributeFinderModule> attributeFinderModules) {
        AttributeFinder attributeFinder = newAttributeFinder();
        attributeFinder.setModules(attributeFinderModules);

        PolicyFinder policyFinder = newPolicyFinder();
        policyFinder.setModules(policyFinderModules);
        return new PDPConfig(attributeFinder, policyFinder, null);
    }

    private static PolicyFinder newPolicyFinder() {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(new PolicyFinder());
        result.addAdvice(new PolicyFinderInterceptor());

        return (PolicyFinder) result.getProxy();
    }

    private static AttributeFinder newAttributeFinder() {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(new AttributeFinder());
        result.addAdvice(new AttributeFinderInterceptor());

        return (AttributeFinder) result.getProxy();
    }

    private static PolicyCombiningAlgorithm createPolicyCombiningAlgorithm(CombiningAlgorithm combiningAlg) {
        if (combiningAlg == null) {
            return (PolicyCombiningAlgorithm) combiningAlg;
        }
        ProxyFactory result = new ProxyFactory();
        result.setTarget(combiningAlg);
        result.addAdvice(new PolicyCombiningAlgorithmInterceptor(combiningAlg));

        return (PolicyCombiningAlgorithm) result.getProxy();
    }


    private static Rule createRule(Rule rule) {
        ProxyFactory result = new ProxyFactory();

        Set<AbstractObligation> obligations = new HashSet<>();
        Set<AdviceExpression> advices = new HashSet<>();

/* not needed now
        try {
            Field field = ReflectionUtils.findField(Rule.class, "obligationExpressions");
            field.setAccessible(true);
            obligations = (Set<AbstractObligation>) field.get(sourceRule);
        } catch (Exception e) {
            LOGGER.error("Ошибка при попытке получить значение поля obligationExpressions");
        }
        try {
            Field field = ReflectionUtils.findField(Rule.class, "adviceExpressions");
            field.setAccessible(true);
            advices = (Set<AdviceExpression>) field.get(sourceRule);
        } catch (Exception e) {
            LOGGER.error("Ошибка при попытке получить значение поля adviceExpressions");
        }
*/


        Rule target = new Rule(rule.getId(), rule.getEffect(), rule.getDescription(),
                rule.getTarget(), createCondition(rule.getCondition()), obligations, advices, XACMLConstants.XACML_VERSION_3_0);

        result.setTarget(target);
        result.addAdvice(new RuleInterceptor(rule));

        return (Rule) result.getProxy();
    }

    private static RuleCombiningAlgorithm createRuleCombiningAlgorithm(CombiningAlgorithm combiningAlg) {
        if (combiningAlg == null) {
            return (RuleCombiningAlgorithm) combiningAlg;
        }
        ProxyFactory result = new ProxyFactory();
        result.setTarget(combiningAlg);
        result.addAdvice(new RuleCombiningAlgorithmInterceptor(combiningAlg));

        return (RuleCombiningAlgorithm) result.getProxy();
    }

    private static Condition createCondition(Condition condition) {
        if (condition == null) {
            return condition;
        }
        ProxyFactory result = new ProxyFactory();
        result.setTarget(condition);
        result.addAdvice(new ConditionInterceptor(condition));

        return (Condition) result.getProxy();
    }

    public static PolicyFinderResult createPolicyFinderResult(PolicyFinderResult policyFinderResult, PolicyFinder policyFinder) {
        ProxyFactory result = new ProxyFactory();
        result.setTarget(policyFinderResult);
        result.addAdvice(new PolicyFinderResultInterceptor(policyFinder));
        return (PolicyFinderResult) result.getProxy();
    }

    public static AbstractPolicy createAbstractPolicy(AbstractPolicy policy, final PolicyFinder policyFinder) {
        ProxyFactory result = new ProxyFactory();
        AbstractPolicy abstractPolicy = null;
        if (policy instanceof Policy) {
            Policy cast = (Policy) policy;

            List<PolicyTreeElement> rules = policy.getChildren();
            List<Rule> proxiedRules = new ArrayList<>(rules.size());
            for (PolicyTreeElement policyTreeElement : rules) {
                proxiedRules.add(createRule((Rule) policyTreeElement));
            }

            abstractPolicy = new Policy(
                    cast.getId(), cast.getVersion(), createRuleCombiningAlgorithm(cast.getCombiningAlg()),
                    cast.getDescription(), cast.getTarget(), cast.getDefaultVersion(), proxiedRules,
                    cast.getObligationExpressions(), cast.getVariableDefinitions());
        } else if (policy instanceof PolicySet) {
            PolicySet cast = (PolicySet) policy;

            List<PolicyTreeElement> policies = policy.getChildren();
            List<AbstractPolicy> proxiedPolicies = new ArrayList<>(policies.size());
            for (PolicyTreeElement policyTreeElement : policies) {
                proxiedPolicies.add(createAbstractPolicy((AbstractPolicy) policyTreeElement, policyFinder));
            }

            abstractPolicy = new PolicySet(
                    cast.getId(), cast.getVersion(), createPolicyCombiningAlgorithm(cast.getCombiningAlg()),
                    cast.getDescription(), cast.getTarget(), proxiedPolicies, cast.getDefaultVersion(),
                    cast.getObligationExpressions());
        } else if (policy instanceof PolicyReference) {
            PolicyReference cast = (PolicyReference) policy;
            abstractPolicy = new PolicyReference(cast.getId(), cast.getReferenceType(), cast.getConstraints(),
                    policyFinder, cast.getMetaData());
        } else {
            LOGGER.error("Unknown AbstractPolicy implementation");
            return policy;
        }
        result.setTarget(abstractPolicy);
        result.addAdvice(new AbstractPolicyInterceptor(policy));

        return (AbstractPolicy) result.getProxy();
    }

}
