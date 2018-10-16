package custis.easyabac.core.trace.interceptors.cglib;

import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.balana.*;
import org.wso2.balana.combine.CombiningAlgorithm;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.RuleCombiningAlgorithm;
import org.wso2.balana.cond.*;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.xacml3.AdviceExpression;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CGLibPolicyElementsFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CGLibPolicyElementsFactory.class);

    public static PDP newPDP(Set<PolicyFinderModule> policyFinderModules, List<AttributeFinderModule> attributeFinderModules, boolean useProxy) {
        if (useProxy) {
            PDPConfig pdpConfig = newPDPConfig(policyFinderModules, attributeFinderModules);

            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(PDP.class);
            enhancer.setCallback(new PDPInterceptor());
            PDP pdp = (PDP) enhancer.create(new Class[]{PDPConfig.class}, new Object[]{pdpConfig});
            return pdp;
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
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PolicyFinder.class);
        enhancer.setCallback(new PolicyFinderInterceptor());
        PolicyFinder policyFinder = (PolicyFinder) enhancer.create();
        return policyFinder;
    }

    private static AttributeFinder newAttributeFinder() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AttributeFinder.class);
        enhancer.setCallback(new AttributeFinderInterceptor());
        AttributeFinder attributeFinder = (AttributeFinder) enhancer.create();
        return attributeFinder;
    }

    private static PolicyCombiningAlgorithm createPolicyCombiningAlgorithm(CombiningAlgorithm combiningAlg) {
        if (combiningAlg == null) {
            return (PolicyCombiningAlgorithm) combiningAlg;
        }

        PolicyCombiningAlgorithmInterceptor handler = new PolicyCombiningAlgorithmInterceptor();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(combiningAlg.getClass());
        enhancer.setCallback(handler);
        return (PolicyCombiningAlgorithm) enhancer.create();
    }


    private static Rule createRule(Rule rule) {
        Set<AbstractObligation> obligations = new HashSet<>();
        Set<AdviceExpression> advices = new HashSet<>();

        Object[] constructorParameters = new Object[] {
                rule.getId(), rule.getEffect(), rule.getDescription(), rule.getTarget(),
                createCondition(rule.getCondition()), obligations, advices, XACMLConstants.XACML_VERSION_3_0

        };

        RuleInterceptor handler = new RuleInterceptor();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Rule.class);
        Class[] constructorClasses = new Class[] {
                URI.class, int.class, String.class, AbstractTarget.class,
                Condition.class, Set.class, Set.class, int.class
        };



        enhancer.setCallback(handler);
        return (Rule) enhancer.create(constructorClasses, constructorParameters);
    }

    private static RuleCombiningAlgorithm createRuleCombiningAlgorithm(CombiningAlgorithm combiningAlg) {
        if (combiningAlg == null) {
            return (RuleCombiningAlgorithm) combiningAlg;
        }
        RuleCombiningAlgorithmInterceptor handler = new RuleCombiningAlgorithmInterceptor();
        return (RuleCombiningAlgorithm) Enhancer.create(combiningAlg.getClass(), handler);
    }

    private static Condition createCondition(Condition condition) {
        if (condition == null) {
            return condition;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Condition.class);
        Expression expression = createRuleApplyCondition((Expression) condition.getChildren().get(0));
        enhancer.setCallback(new ConditionInterceptor());
        return (Condition) enhancer.create(new Class[] {Expression.class}, new Object[] { expression});
    }

    private static Expression createRuleApplyCondition(Expression expression) {
        if (expression == null || !(expression instanceof Apply)) {
            return expression;
        }
        List<Expression> objects = new ArrayList<>();
        for (int i = 0; i < ((Apply) expression).getChildren().size(); i++) {
            Expression exp = (Expression) ((Apply) expression).getChildren().get(i);
            objects.add(createSimpleRule(exp, i));
        }

        RuleApplyInterceptor handler = new RuleApplyInterceptor();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Apply.class);
        enhancer.setInterfaces(new Class[] {Evaluatable.class});
        enhancer.setCallback(handler);
        return  (Apply) enhancer.create(new Class[]{Function.class, List.class}, new Object[]{((Apply) expression).getFunction(), objects});
    }

    private static Expression createSimpleRule(Expression expression, int index) {
        if (expression == null) {
            return expression;
        }
        SimpleConditionInterceptor handler = new SimpleConditionInterceptor(index);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(expression.getClass());
        enhancer.setInterfaces(new Class[] {Evaluatable.class});
        enhancer.setCallback(handler);
        return (Expression) enhancer.create(new Class[]{Function.class, List.class}, new Object[]{((Apply) expression).getFunction(), ((Apply) expression).getChildren()});
    }

    public static AbstractPolicy createAbstractPolicy(AbstractPolicy policy, final PolicyFinder policyFinder) {
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

}
