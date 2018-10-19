package custis.easyabac.benchmark;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.permissionchecker.OrderPermissionChecker;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.openjdk.jmh.annotations.*;

import java.util.Collections;

@State(Scope.Benchmark)
public class DynamicAuthorizationBenchmark extends AbstractAuthorizationBenchmark {

    private OrderPermissionChecker managerChecker;
    private OrderPermissionChecker operatorChecker;

    @Setup(Level.Trial)
    public void init() throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AbacAuthModel model = creator.createModel(getClass().getResourceAsStream("/OrdersPolicy.yaml"));

        AuthService managerAuthService = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.DIRECT_INSTANCE)
                .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                .datasources(Collections.singletonList(getCustomerBranchIdDatasource()))
                .build();
        EasyABACPermissionCheckerFactory mgrFactory = new EasyABACPermissionCheckerFactory(managerAuthService);
        this.managerChecker = mgrFactory.getPermissionChecker(OrderPermissionChecker.class);

        AuthService operatorAuthService = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.DIRECT_INSTANCE)
                .subjectAttributesProvider(getSubjectAttributesProvider(getOperatorSubject(), model))
                .datasources(Collections.singletonList(getCustomerBranchIdDatasource()))
                .build();
        EasyABACPermissionCheckerFactory oprFactory = new EasyABACPermissionCheckerFactory(operatorAuthService);
        this.operatorChecker = oprFactory.getPermissionChecker(OrderPermissionChecker.class);
    }

    @Benchmark
    public boolean ensureApproveSameBranchOrderPermitted() {
        Order order = getOrder();
        boolean hasException;
        try {
            managerChecker.ensurePermittedApprove(order);
            hasException = false;
        } catch (NotExpectedResultException npe) {
            hasException = true;
        }

        return hasException;
    }

    @Benchmark
    public boolean ensureRejectSameClientOrderPermitted() {
        Order order = getOrder();
        boolean hasException;
        try {
            managerChecker.ensurePermittedReject(order);
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
            this.operatorChecker.ensurePermittedApprove(getOrder());
            hasException = false;
        } catch (NotExpectedResultException nere) {
            hasException = true;
        }
        return hasException;
    }

    public static void main(String[] args) throws EasyAbacInitException {
        DynamicAuthorizationBenchmark benchmark = new DynamicAuthorizationBenchmark();
        benchmark.init();
        System.out.printf("ensureApproveSameBranchOrderPermitted has exception: %b\n", benchmark.ensureApproveSameBranchOrderPermitted());
        System.out.printf("ensureRejectSameClientOrderPermitted has exception: %b\n", benchmark.ensureRejectSameClientOrderPermitted());
        System.out.printf("ensureApproveByNonManagerDenied has exception: %b\n", benchmark.ensureApproveByNonManagerDenied());
    }
}