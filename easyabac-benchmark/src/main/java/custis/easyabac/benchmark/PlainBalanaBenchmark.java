package custis.easyabac.benchmark;

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

public class PlainBalanaBenchmark {

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
        Set<Attribute> subjectAttrsSet = new HashSet<>();
        Attribute subjectBranchIdAttr = balanaAttribute("urn:attr:subject.branchId",
                DataType.STRING,
                singletonList("1234"), false);
        subjectAttrsSet.add(subjectBranchIdAttr);
        Attribute subjectRoleAttr = balanaAttribute("urn:attr:subject.role",
                DataType.STRING,
                singletonList("OPERATOR"), false);
        subjectAttrsSet.add(subjectRoleAttr);
        Attributes subjectAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"), subjectAttrsSet);

        Set<Attributes> allAttrs = new HashSet<>();
        allAttrs.add(prepareActionAttributes("order.approve"));
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

    @Benchmark
    public void ensureRejectSameClientOrderPermitted(PlainBalanaState state, Blackhole blackhole) throws EasyAbacInitException {
        Set<Attribute> resourceAttrsSet = new HashSet<>();
        Attribute customerBranchIdAttr = balanaAttribute("urn:attr:customer.branchId",
                DataType.STRING,
                singletonList("1234"), false);
        resourceAttrsSet.add(customerBranchIdAttr);
        Attributes resourceAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"), resourceAttrsSet);

        Set<Attribute> subjectAttrsSet = new HashSet<>();
        Attribute subjectBranchIdAttr = balanaAttribute("urn:attr:subject.branchId",
                DataType.STRING,
                singletonList("1234"), false);
        subjectAttrsSet.add(subjectBranchIdAttr);
        Attribute subjectRoleAttr = balanaAttribute("urn:attr:subject.role",
                DataType.STRING,
                singletonList("MANAGER"), false);
        subjectAttrsSet.add(subjectRoleAttr);
        Attributes subjectAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"), subjectAttrsSet);

        Set<Attributes> allAttrs = new HashSet<>();
        allAttrs.add(prepareActionAttributes("order.reject"));
        allAttrs.add(resourceAttrs);
        allAttrs.add(subjectAttrs);

        RequestCtx rejectSameClientOrderRequest = new RequestCtx(allAttrs, null);

        ResponseCtx responseCtx = state.pdp.evaluate(rejectSameClientOrderRequest);
        blackhole.consume(responseCtx);
    }

    @Benchmark
    public void ensureApproveByNonManagerDenied(PlainBalanaState state, Blackhole blackhole) throws EasyAbacInitException {
        Set<Attribute> subjectAttrsSet = new HashSet<>();
        Attribute subjectBranchIdAttr = balanaAttribute("urn:attr:subject.branchId",
                DataType.STRING,
                singletonList("1234"), false);
        subjectAttrsSet.add(subjectBranchIdAttr);
        Attribute subjectRoleAttr = balanaAttribute("urn:attr:subject.role",
                DataType.STRING,
                singletonList("OPERATOR"), false);
        subjectAttrsSet.add(subjectRoleAttr);
        Attributes subjectAttrs = new Attributes(
                URI.create("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"), subjectAttrsSet);

        Set<Attributes> allAttrs = new HashSet<>();
        allAttrs.add(prepareActionAttributes("order.approve"));
        allAttrs.add(subjectAttrs);

        RequestCtx approveByNonManagerRequest = new RequestCtx(allAttrs, null);

        ResponseCtx responseCtx = state.pdp.evaluate(approveByNonManagerRequest);
        blackhole.consume(responseCtx);
    }
}