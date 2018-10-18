package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;
import custis.easyabac.core.init.InputStreamPolicyFinderModule;
import custis.easyabac.core.pdp.balana.trace.interceptors.cglib.CGLibPolicyElementsFactory;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.attribute.DataType;
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

@State(Scope.Benchmark)
public class PlainBalanaBenchmark extends AbstractAuthorizationBenchmark {

    private PDP pdp;

    @Setup(Level.Trial)
    public void initPDP() {
        Set<PolicyFinderModule> policyModules = new HashSet<>();
        policyModules.add(new InputStreamPolicyFinderModule(
                PlainBalanaBenchmark.class.getResourceAsStream("/OrdersPolicy.xacml"), false));
        this.pdp = CGLibPolicyElementsFactory.newPDP(policyModules, Collections.emptyList(), false);
    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermitted(Blackhole blackhole) throws EasyAbacInitException {
        Order order = getOrder();
        OrderAction action = getOrderApproveAction();
        Subject subject = getManagerSubject();

        Set<Attribute> resourceAttrsSet = new HashSet<>();
        Attribute orderBranchIdAttr = balanaAttribute("urn:attr:order.branchId",
                DataType.STRING,
                singletonList(order.getBranchId()), false);
        resourceAttrsSet.add(orderBranchIdAttr);
        Attribute orderAmountAttr = balanaAttribute("urn:attr:order.amount",
                DataType.INT,
                singletonList(Integer.toString(order.getAmount())), false);
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
                singletonList(subject.getBranchId()), false);
        subjectAttrsSet.add(subjectBranchIdAttr);
        Attribute subjectMaxAmountAttr = balanaAttribute("urn:attr:subject.maxOrderAmount",
                DataType.INT,
                singletonList(Integer.toString(subject.getMaxOrderAmount())), false);
        subjectAttrsSet.add(subjectMaxAmountAttr);
        Attributes subjectAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"), subjectAttrsSet);

        Set<Attributes> allAttrs = new HashSet<>();
        allAttrs.add(resourceAttrs);
        allAttrs.add(prepareActionAttributes("order." + action.getId()));
        allAttrs.add(subjectAttrs);

        RequestCtx approveSameBranchOrderRequest = new RequestCtx(allAttrs, null);

        ResponseCtx responseCtx = pdp.evaluate(approveSameBranchOrderRequest);
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

    @Benchmark
    public void ensureRejectSameClientOrderPermitted(Blackhole blackhole) throws EasyAbacInitException {
        Set<Attribute> resourceAttrsSet = new HashSet<>();
        Attribute customerBranchIdAttr = balanaAttribute("urn:attr:customer.branchId",
                DataType.STRING,
                singletonList(getCustomer().getBranchId()), false);
        resourceAttrsSet.add(customerBranchIdAttr);
        Attributes resourceAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"), resourceAttrsSet);

        Subject manager = getManagerSubject();
        Set<Attribute> subjectAttrsSet = new HashSet<>();
        Attribute subjectBranchIdAttr = balanaAttribute("urn:attr:subject.branchId",
                DataType.STRING,
                singletonList(manager.getBranchId()), false);
        subjectAttrsSet.add(subjectBranchIdAttr);
        Attribute subjectRoleAttr = balanaAttribute("urn:attr:subject.role",
                DataType.STRING,
                singletonList(manager.getRole()), false);
        subjectAttrsSet.add(subjectRoleAttr);
        Attributes subjectAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"), subjectAttrsSet);

        Set<Attributes> allAttrs = new HashSet<>();
        allAttrs.add(prepareActionAttributes("order." + getOrderRejectAction()));
        allAttrs.add(resourceAttrs);
        allAttrs.add(subjectAttrs);

        RequestCtx rejectSameClientOrderRequest = new RequestCtx(allAttrs, null);

        ResponseCtx responseCtx = pdp.evaluate(rejectSameClientOrderRequest);
        blackhole.consume(responseCtx);
    }

    @Benchmark
    public void ensureApproveByNonManagerDenied(Blackhole blackhole) throws EasyAbacInitException {
        Subject operator = getOperatorSubject();
        Set<Attribute> subjectAttrsSet = new HashSet<>();
        Attribute subjectBranchIdAttr = balanaAttribute("urn:attr:subject.branchId",
                DataType.STRING,
                singletonList(operator.getBranchId()), false);
        subjectAttrsSet.add(subjectBranchIdAttr);
        Attribute subjectRoleAttr = balanaAttribute("urn:attr:subject.role",
                DataType.STRING,
                singletonList(operator.getRole()), false);
        subjectAttrsSet.add(subjectRoleAttr);
        Attributes subjectAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"), subjectAttrsSet);

        Set<Attributes> allAttrs = new HashSet<>();
        allAttrs.add(prepareActionAttributes("order." + getOrderApproveAction()));
        allAttrs.add(subjectAttrs);

        RequestCtx approveByNonManagerRequest = new RequestCtx(allAttrs, null);

        ResponseCtx responseCtx = pdp.evaluate(approveByNonManagerRequest);
        blackhole.consume(responseCtx);
    }
}