package custis.easyabac.benchmark;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.init.BalanaPdpHandlerFactory;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import org.openjdk.jmh.annotations.*;

import static java.util.Collections.singletonList;

@State(Scope.Thread)
public class PermissionCheckerBenchmark extends AbstractAuthorizationBenchmark {

    private PermitAwarePermissionChecker<Order, OrderAction> managerOrderPermissionChecker;
    private PermitAwarePermissionChecker<Order, OrderAction> operatorOrderPermissionChecker;

    @Setup(Level.Trial)
    public void setup() throws EasyAbacInitException {
        AbacAuthModel model = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML,
                getClass().getResourceAsStream("/OrdersPolicy.yaml"));

        this.managerOrderPermissionChecker = new EasyABACPermissionChecker<>(
                new EasyAbacBuilder(model, pdpHandlerFactory)
                        .pdpHandlerFactory(BalanaPdpHandlerFactory.DIRECT_INSTANCE)
                        .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                        .datasources(singletonList(getCustomerBranchIdDatasource()))
                        .build());

        this.operatorOrderPermissionChecker = new EasyABACPermissionChecker<>(
                new EasyAbacBuilder(model, pdpHandlerFactory)
                        .pdpHandlerFactory(BalanaPdpHandlerFactory.DIRECT_INSTANCE)
                        .datasources(singletonList(getCustomerBranchIdDatasource()))
                        .subjectAttributesProvider(getSubjectAttributesProvider(getOperatorSubject(), model))
                        .build());
    }

    @Benchmark
    public boolean ensureApproveSameBranchOrderPermitted() {
        boolean hasException;
        try {
            this.managerOrderPermissionChecker.ensurePermitted(getOrder(), getOrderApproveAction());
            hasException = false;
        } catch (NotExpectedResultException npe) {
            hasException = true;
        }
        return hasException;
    }

    @Benchmark
    public boolean ensureRejectSameClientOrderPermitted() {
        boolean hasException;
        try {
            this.managerOrderPermissionChecker.ensurePermitted(getOrder(), getOrderRejectAction());
            hasException = false;
        } catch (NotExpectedResultException npe) {
            hasException = true;
        }
        return hasException;
    }

    @Benchmark
    public boolean ensureApproveByNonManagerDenied() {
        boolean hasException;
        try {
            this.operatorOrderPermissionChecker.ensurePermitted(getOrder(), getOrderApproveAction());
            hasException = false;
        } catch (NotExpectedResultException npe) {
            hasException = true;
        }
        return hasException;
    }

    public static void main(String[] args) throws EasyAbacInitException {
        PermissionCheckerBenchmark benchmark = new PermissionCheckerBenchmark();
        benchmark.setup();
        System.out.printf("Has exception: %b\n", benchmark.ensureApproveByNonManagerDenied());
    }
}