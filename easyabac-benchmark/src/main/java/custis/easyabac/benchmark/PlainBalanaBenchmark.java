package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.init.InputStreamPolicyFinderModule;
import custis.easyabac.core.model.abac.attribute.DataType;
import custis.easyabac.core.trace.PolicyElementsFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.wso2.balana.PDP;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.xacml3.Attributes;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static custis.easyabac.core.init.BalanaAttributesFactory.balanaAttribute;
import static java.util.Collections.singletonList;

public class PlainBalanaBenchmark extends AbstractAuthorizationBenchmark {

    @State(Scope.Benchmark)
    public static class PlainBalanaState {
        private PDP pdp;

        @Setup(Level.Trial)
        public void initPDP() {
            Set<PolicyFinderModule> policyModules = new HashSet<>();
            policyModules.add(new InputStreamPolicyFinderModule(
                    PlainBalanaBenchmark.class.getResourceAsStream("/OrdersPolicy.xacml"), false));
            this.pdp = PolicyElementsFactory.newPDP(policyModules, Collections.emptyList(), false);
        }
    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermitted(PlainBalanaState state, Blackhole blackhole) throws EasyAbacInitException {
        Order order = getOrder();
        OrderAction action = getOrderAction();
        Subject subject = getSubject();

        Set<Attribute> resourceAttrsSet = new HashSet<>();
        Attribute orderBranchIdAttr = balanaAttribute("urn:attr:order.branchId",
                DataType.STRING,
                singletonList(order.getBranchId()), false);
        resourceAttrsSet.add(orderBranchIdAttr);
        Attribute orderAmountAttr = balanaAttribute("urn:attr:order.amount",
                DataType.INT,
                singletonList("" + order.getAmount()), false);
        resourceAttrsSet.add(orderAmountAttr);
        Attributes resourceAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"), resourceAttrsSet);

        Set<Attribute> subjectAttrsSet = new HashSet<>();
        Attribute subjectRoleAttr = balanaAttribute("urn:attr:subject.role",
                DataType.STRING,
                singletonList(subject.getRole()), false);
        subjectAttrsSet.add(subjectRoleAttr);
        Attribute subjectBranchIdAttr = balanaAttribute("urn:attr:subject.branchId",
                DataType.STRING,
                singletonList("" + subject.getBranchId()), false);
        subjectAttrsSet.add(subjectBranchIdAttr);
        Attribute subjectAmpountIdAttr = balanaAttribute("urn:attr:subject.maxOrderAmount",
                DataType.INT,
                singletonList("" + subject.getBranchId()), false);
        subjectAttrsSet.add(subjectAmpountIdAttr);
        Attributes subjectAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"), subjectAttrsSet);

        Set<Attributes> allAttrs = new HashSet<>();
        allAttrs.add(resourceAttrs);
        allAttrs.add(prepareActionAttributes("order." + action.getId()));
        allAttrs.add(subjectAttrs);

        RequestCtx approveSameBranchOrderRequest = new RequestCtx(allAttrs, null);

        ResponseCtx responseCtx = state.pdp.evaluate(approveSameBranchOrderRequest);
        blackhole.consume(responseCtx);
    }

    private Attributes prepareActionAttributes(String action) throws EasyAbacInitException {
        Set<Attribute> actionAttrsSet = new HashSet<>();
        Attribute actionAttr = balanaAttribute("urn:attr:order.action",
                DataType.STRING,
                singletonList(action), false);
        actionAttrsSet.add(actionAttr);
        return new Attributes(
                URI.create("urn:oasis:names:tc:xacml:3.0:attribute-category:action"), actionAttrsSet);
    }

}