package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.init.BalanaPdpHandlerFactory;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

@State(Scope.Benchmark)
public class AttributeAuthorizationProxyBenchmark extends AbstractAuthorizationBenchmark {

    private AttributiveAuthorizationService managerAuthService;
    private AttributiveAuthorizationService operatorAuthService;

    @Setup(Level.Trial)
    public void initService() throws EasyAbacInitException {
        AbacAuthModel model = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML,
                getClass().getResourceAsStream("/OrdersPolicy.yaml"));

        this.managerAuthService = new EasyAbac.Builder(model)
                .pdpHandlerFactory(BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .datasources(singletonList(getCustomerBranchIdDatasource()))
                .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                .build();

        this.operatorAuthService = new EasyAbac.Builder(model)
                .pdpHandlerFactory(BalanaPdpHandlerFactory.PROXY_INSTANCE)
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