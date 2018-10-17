package custis.easyabac.benchmark;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.permissionchecker.OrderPermissionChecker;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.init.BalanaPdpHandlerFactory;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class DynamicAuthorizationBenchmark extends AbstractAuthorizationBenchmark {

    private OrderPermissionChecker checker;

    @Setup
    public void init() throws EasyAbacInitException {
        AbacAuthModel model = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML,
                getClass().getResourceAsStream("/OrdersPolicy.yaml"));
        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(model)
                .pdpHandlerFactory(BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .subjectAttributesProvider(getSubjectAttributesProvider(model))
                .build();
        EasyABACPermissionCheckerFactory factory = new EasyABACPermissionCheckerFactory(authorizationService);
        this.checker = factory.getPermissionChecker(OrderPermissionChecker.class);
    }


    @Benchmark
    public boolean ensureApproveSameBranchOrderPermitted() {
        Order order = getOrder();
        boolean hasException;
        try {
            checker.ensurePermittedApprove(order);
            hasException = false;
        } catch (NotPermittedException npe) {
            hasException = true;
        }

        return hasException;
    }

    public static void main(String[] args) throws EasyAbacInitException {
        DynamicAuthorizationBenchmark benchmark = new DynamicAuthorizationBenchmark();
        benchmark.init();
        System.out.printf("Has exception: %b\n", benchmark.ensureApproveSameBranchOrderPermitted());
    }
}