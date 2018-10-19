package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
public class AttributeAuthorizationPlainBenchmark extends AbstractAuthorizationBenchmark {

    private AttributiveAuthorizationService authorizationService;

    @Setup(Level.Trial)
    public void initService() throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AbacAuthModel model = creator.createModel(getClass().getResourceAsStream("/OrdersPolicy.yaml"));

        this.authorizationService = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.DIRECT_INSTANCE)
                .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                .build();
    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermitted(Blackhole blackhole) {
        Order order = getOrder();
        OrderAction action = getOrderApproveAction();

        List<AuthAttribute> authAttributes = new ArrayList<>();
        authAttributes.add(new AuthAttribute("order.action", "order." + action.getId()));
        authAttributes.add(new AuthAttribute("order.branchId", order.getBranchId()));
        authAttributes.add(new AuthAttribute("order.amount", "" + order.getAmount()));

        AuthResponse response = authorizationService.authorize(authAttributes);
        blackhole.consume(response);
    }

    @Benchmark
    public void ensureRejectSameClientOrderPermitted(Blackhole blackhole) {
        Subject managerSubject = getManagerSubject();
        OrderAction action = getOrderRejectAction();

        List<AuthAttribute> authAttributes = new ArrayList<>();
        authAttributes.add(new AuthAttribute("order.action", "order." + action.getId()));
        authAttributes.add(new AuthAttribute("subject.role", managerSubject.getRole()));
        authAttributes.add(new AuthAttribute("customer.branchId", getCustomer().getBranchId()));
        authAttributes.add(new AuthAttribute("subject.branchId", managerSubject.getBranchId()));

        AuthResponse response = authorizationService.authorize(authAttributes);
        blackhole.consume(response);
    }

    @Benchmark
    public void ensureApproveByNonManagerDenied(Blackhole blackhole) {
        Subject operatorSubject = getOperatorSubject();
        OrderAction action = getOrderApproveAction();

        List<AuthAttribute> authAttributes = new ArrayList<>();
        authAttributes.add(new AuthAttribute("order.action", "order." + action.getId()));
        authAttributes.add(new AuthAttribute("subject.role", operatorSubject.getRole()));
        authAttributes.add(new AuthAttribute("customer.branchId", getCustomer().getBranchId()));
        authAttributes.add(new AuthAttribute("subject.branchId", operatorSubject.getBranchId()));

        AuthResponse response = authorizationService.authorize(authAttributes);
        blackhole.consume(response);
    }
}