package custis.easyabac.core.trace;

import custis.easyabac.core.trace.interceptors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.balana.*;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.cond.Condition;
import org.wso2.balana.cond.Expression;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProxyImpl implements BalanaInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyImpl.class);

    public PDPConfig newPDPConfig(Set<PolicyFinderModule> policyFinderModules, List<AttributeFinderModule> attributeFinderModules) {
        AttributeFinder attributeFinder = newAttributeFinder();
        attributeFinder.setModules(attributeFinderModules);

        PolicyFinder policyFinder = newPolicyFinder();
        policyFinder.setModules(policyFinderModules);
        return new PDPConfig(attributeFinder, policyFinder, null);
    }

    /*@Override*/
    public PDP newPDP(PDPConfig pdpConfig) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PDP.class);
        enhancer.setCallback(new PDPInterceptor());
        PDP pdp = (PDP) enhancer.create(new Class[]{PDPConfig.class}, new Object[]{pdpConfig});
        return pdp;
    }

    private PolicyFinder newPolicyFinder() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PolicyFinder.class);
        enhancer.setCallback(new PolicyFinderInterceptor(this));
        PolicyFinder policyFinder = (PolicyFinder) enhancer.create();
        return policyFinder;
    }

    private AttributeFinder newAttributeFinder() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AttributeFinder.class);
        enhancer.setCallback(new AttributeFinderInterceptor());
        AttributeFinder attributeFinder = (AttributeFinder) enhancer.create();
        return attributeFinder;
    }

    private static CombiningAlgorithm createPolicyCombiningAlgorithm(CombiningAlgorithm combiningAlg) {
        if (combiningAlg == null) {
            return combiningAlg;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(combiningAlg.getClass());
        enhancer.setCallback(new PolicyCombiningAlgorithmInterceptor());
        return (PolicyCombiningAlgorithm) enhancer.create();
    }

    private CombiningAlgorithm createRuleCombiningAlgorithm(CombiningAlgorithm combiningAlg) {
        if (combiningAlg == null) {
            return combiningAlg;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(combiningAlg.getClass());
        enhancer.setCallback(new RuleCombiningAlgorithmInterceptor());
        return (RuleCombiningAlgorithm) enhancer.create();
    }

    private AbstractPolicy createAbstractPolicy(AbstractPolicy policy, final PolicyFinder policyFinder) {
        Enhancer enhancer = new Enhancer();

        Class[] constructorClasses = null;
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

        return (AbstractPolicy) enhancer.create(constructorClasses, constructorParameters);
    }

    private Condition createCondition(Condition condition) {
        if (condition == null) {
            return condition;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Condition.class);

        boolean isVersionOne = condition.getFunction() != null;

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

    }

    private PolicyFinderResult createPolicyFinderResult(PolicyFinderResult policyFinderResult, PolicyFinder policyFinder) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PolicyFinderResult.class);
        enhancer.setCallback(new PolicyFinderResultInterceptor(this, policyFinder));
        Object proxyObject = null;
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
        return proxiedResult;
    }

}
