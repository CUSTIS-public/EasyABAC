package custis.easyabac.benchmark;

import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.permissionchecker.OrderPermissionChecker;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.init.BalanaPdpHandlerFactory;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class DynamicAuthorizationBenchmark extends AbstractAuthorizationBenchmark {

    @State(Scope.Benchmark)
    public static class AttributeAuthorizationState {
        private AttributiveAuthorizationService authorizationService;
        private EasyABACPermissionCheckerFactory factory;
        private OrderPermissionChecker checker;

        @Setup(Level.Trial)
        public void initService() throws EasyAbacInitException {
            AbacAuthModel model = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML,
                    getClass().getResourceAsStream("/OrdersPolicy.yaml"));
            this.authorizationService = new EasyAbac.Builder(model)
                    .pdpHandlerFactory(BalanaPdpHandlerFactory.DIRECT_INSTANCE)
                    .subjectAttributesProvider(getSubjectAttributesProvider(model))
                    .build();
            this.factory = new EasyABACPermissionCheckerFactory(authorizationService);
            this.checker = factory.getPermissionChecker(OrderPermissionChecker.class);
        }

    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermitted(AttributeAuthorizationState state, Blackhole blackhole) {
        Order order = getOrder();
        OrderAction action = getOrderAction();

        state.checker.ensurePermittedApprove(order);
        blackhole.consume(0);
    }

}