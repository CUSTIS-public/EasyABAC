package custis.easyabac.core.trace;

import custis.easyabac.core.trace.interceptors.AttributeFinderInterceptor;
import custis.easyabac.core.trace.interceptors.RuleCombiningAlgorithmInterceptor;
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
import org.wso2.balana.xacml3.AdviceExpression;

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
        enhancer.setCallback(new PolicyFinderInterceptor());
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
        enhancer.setCallback(new RuleInterceptor());
        return (Rule) enhancer.create(constructorClasses, constructorParameters);
    }

    private PolicyFinderResult createPolicyFinderResult(PolicyFinderResult policyFinderResult, PolicyFinder policyFinder) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PolicyFinderResult.class);
        enhancer.setCallback(new PolicyFinderResultInterceptor(policyFinder));
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

    private class PolicyFinderInterceptor implements MethodInterceptor {

        /*@Override*/
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (method.getName().equals("findPolicy")) {
                EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();
                handler.onFindPolicyStart();
                Object invokeSuperResult = proxy.invokeSuper(obj, args);
                PolicyFinderResult policyFinderResult = (PolicyFinderResult) invokeSuperResult;
                handler.onFindPolicyEnd(policyFinderResult);
                return createPolicyFinderResult(policyFinderResult, (PolicyFinder) obj);
            } else {
                return proxy.invokeSuper(obj, args);
            }
        }

    }

    private class PolicyFinderResultInterceptor implements MethodInterceptor {

        private final PolicyFinder policyFinder;

        public PolicyFinderResultInterceptor(PolicyFinder policyFinder) {
            this.policyFinder = policyFinder;
        }

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (method.getName().equals("getPolicy")) {
                Object superInvoke = proxy.invokeSuper(obj, args);
                AbstractPolicy policy = (AbstractPolicy) superInvoke;
                return createAbstractPolicy(policy, policyFinder);
            } else {
                return proxy.invokeSuper(obj, args);
            }
        }

    }

    private static class AbstractPolicyInterceptor implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            String methodName = method.getName();
            AbstractPolicy abstractPolicy = (AbstractPolicy) obj;
            Object realResult = null;

            EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();

            if (methodName.equals("evaluate")) {
                handler.onPolicyEvaluateStart(abstractPolicy);
                realResult = proxy.invokeSuper(obj, args);
                handler.onPolicyEvaluateEnd((AbstractResult) realResult);
            } else if (methodName.equals("match")) {
                handler.onPolicyMatchStart(abstractPolicy);
                realResult = proxy.invokeSuper(obj, args);
                handler.onPolicyMatchEnd((MatchResult) realResult);
            } else {
                realResult = proxy.invokeSuper(obj, args);
            }

            return realResult;
        }

    }

    private static class ConditionInterceptor implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            String methodName = method.getName();
            Condition condition = (Condition) obj;
            Object realResult = null;

            EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();

            if (methodName.equals("evaluate")) {
                handler.onConditionEvaluateStart(condition);
                realResult = proxy.invokeSuper(obj, args);
                handler.onConditionEvaluateEnd((EvaluationResult) realResult);
            } else {
                realResult = proxy.invokeSuper(obj, args);
            }



            return realResult;
        }

    }

    private static class RuleInterceptor implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            String methodName = method.getName();
            Rule rule = (Rule) obj;
            Object realResult = null;

            EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();

            if (methodName.equals("evaluate")) {
                handler.onRuleEvaluateStart(rule);
                realResult = proxy.invokeSuper(obj, args);
                handler.onRuleEvaluateEnd((AbstractResult) realResult);
            } else if (methodName.equals("match")) {
                handler.onRuleMatchStart(rule);
                realResult = proxy.invokeSuper(obj, args);
                handler.onRuleMatchEnd((MatchResult) realResult);
            } else {
                realResult = proxy.invokeSuper(obj, args);
            }



            return realResult;
        }

    }

    private class PDPInterceptor implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            boolean evaluationCall = method.getName().equals("evaluate") && args.length == 1 && args[0] instanceof EvaluationCtx;
            if (evaluationCall) {
                EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();
                handler.beforeProcess((EvaluationCtx) args[0]);
                Object result = proxy.invokeSuper(obj, args);
                handler.postProcess((ResponseCtx) result);
                return result;
            } else {
                return proxy.invokeSuper(obj, args);
            }
        }
    }

    private static class PolicyCombiningAlgorithmInterceptor implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (method.getName().equals("combine")) {
                EvaluatingProcessHandler handler = EvaluationProcessIdentifier.get();
                handler.onPolicyCombineStart((PolicyCombiningAlgorithm) obj);
                Object invokeSuperResult = proxy.invokeSuper(obj, args);
                handler.onPolicyCombineEnd((AbstractResult) invokeSuperResult);
                return invokeSuperResult;
            } else {
                return proxy.invokeSuper(obj, args);
            }
        }
    }

}
