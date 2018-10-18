package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
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

import static java.util.Collections.singletonList;

@State(Scope.Benchmark)
public class AttributeAuthorizationProxyBenchmark extends AbstractAuthorizationBenchmark {

    private AttributiveAuthorizationService managerAuthService;
    private AttributiveAuthorizationService operatorAuthService;

    @Setup(Level.Trial)
    public void initService() throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AbacAuthModel model = creator.createModel(getClass().getResourceAsStream("/OrdersPolicy.yaml"));

        this.managerAuthService = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .datasources(singletonList(getCustomerBranchIdDatasource()))
                .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                .build();

        this.operatorAuthService = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .datasources(singletonList(getCustomerBranchIdDatasource()))
                .subjectAttributesProvider(getSubjectAttributesProvider(getOperatorSubject(), model))
                .build();
    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermitted(Blackhole blackhole) {
        List<AuthAttribute> authAttributes = prepareAttributes(getOrderApproveAction());

        AuthResponse response = managerAuthService.authorize(authAttributes);
        blackhole.consume(response);
    }

    @Benchmark
    public void ensureRejectSameClientOrderPermitted(Blackhole blackhole) {
        AuthResponse response = managerAuthService.authorize(prepareAttributes(getOrderRejectAction()));
        blackhole.consume(response);
    }

    @Benchmark
    public void ensureApproveByNonManagerDenied(Blackhole blackhole) {
        AuthResponse response = operatorAuthService.authorize(prepareAttributes(getOrderApproveAction()));
        blackhole.consume(response);
    }

    private List<AuthAttribute> prepareAttributes(OrderAction action) {
        Order order = getOrder();
        List<AuthAttribute> authAttributes = new ArrayList<>();
        authAttributes.add(new AuthAttribute("order.action", "order." + action.getId()));
        authAttributes.add(new AuthAttribute("order.branchId", order.getBranchId()));
        authAttributes.add(new AuthAttribute("order.amount", Integer.toString(order.getAmount())));
        return authAttributes;
    }
}