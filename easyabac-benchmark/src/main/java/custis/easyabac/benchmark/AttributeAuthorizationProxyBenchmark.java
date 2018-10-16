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
import java.util.List;

public class AttributeAuthorizationProxyBenchmark extends AbstractAuthorizationBenchmark {

    @State(Scope.Benchmark)
    public static class AttributeAuthorizationState {
        private AttributiveAuthorizationService authorizationService;

        @Setup(Level.Trial)
        public void initService() throws EasyAbacInitException {
            AbacAuthModel model = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML,
                    getClass().getResourceAsStream("/OrdersPolicy.yaml"));
            this.authorizationService = new EasyAbac.Builder(model)
                    .pdpHandlerFactory(BalanaPdpHandlerFactory.PROXY_INSTANCE)
                    .subjectAttributesProvider(getSubjectAttributesProvider(model))
                    .build();
        }

    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermitted(AttributeAuthorizationState state, Blackhole blackhole) {
        Order order = getOrder();
        OrderAction action = getOrderAction();
        Subject subject = getSubject();

        List<AuthAttribute> authAttributes = new ArrayList<>();
        authAttributes.add(new AuthAttribute("order.action", "order." + action.getId()));
        authAttributes.add(new AuthAttribute("order.branchId", order.getBranchId()));
        authAttributes.add(new AuthAttribute("order.amount", "" + order.getAmount()));

        AuthResponse response = state.authorizationService.authorize(authAttributes);
        blackhole.consume(response);
    }

}