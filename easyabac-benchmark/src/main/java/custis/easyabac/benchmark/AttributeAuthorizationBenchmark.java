package custis.easyabac.benchmark;

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

public class AttributeAuthorizationBenchmark {

    @State(Scope.Benchmark)
    public static class AttributeAuthorizationState {
        private AttributiveAuthorizationService authorizationService;
        private List<AuthAttribute> approveSameBranchOrderRequest;
        private List<AuthAttribute> rejectSameClientOrderRequest;
        private List<AuthAttribute> approveByNonManagerRequest;

        @Setup(Level.Trial)
        public void initService() throws EasyAbacInitException {
            AbacAuthModel model = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML,
                    getClass().getResourceAsStream("/OrdersPolicy.yaml"));
            this.authorizationService = new EasyAbac.Builder(model)
                    .pdpHandlerFactory(BalanaPdpHandlerFactory.PROXY_INSTANCE)
                    .build();
        }

        @Setup(Level.Trial)
        public void prepareApproveByNonManagerRequest() {
            List<AuthAttribute> authAttributes = new ArrayList<>();
            authAttributes.add(new AuthAttribute("order.action", "order.approve"));
            authAttributes.add(new AuthAttribute("subject.role", "OPERATOR"));
            authAttributes.add(new AuthAttribute("order.branchId", "1234"));
            authAttributes.add(new AuthAttribute("subject.branchId", "1234"));

            this.approveByNonManagerRequest = authAttributes;
        }

        @Setup(Level.Trial)
        public void prepareRejectSameClientOrderRequest() {
            List<AuthAttribute> authAttributes = new ArrayList<>();
            authAttributes.add(new AuthAttribute("order.action", "order.reject"));
            authAttributes.add(new AuthAttribute("subject.role", "MANAGER"));
            authAttributes.add(new AuthAttribute("order.branchId", "1234"));
            authAttributes.add(new AuthAttribute("subject.branchId", "1234"));

            this.rejectSameClientOrderRequest = authAttributes;
        }

        @Setup(Level.Trial)
        public void prepareApproveSameBranchOrderRequest() {
            List<AuthAttribute> authAttributes = new ArrayList<>();
            authAttributes.add(new AuthAttribute("order.action", "order.approve"));
            authAttributes.add(new AuthAttribute("subject.role", "MANAGER"));
            authAttributes.add(new AuthAttribute("subject.branchId", "1234"));
            authAttributes.add(new AuthAttribute("subject.maxOrderAmount", "20000"));
            authAttributes.add(new AuthAttribute("order.branchId", "1234"));
            authAttributes.add(new AuthAttribute("order.amount", "1000"));

            this.approveSameBranchOrderRequest = authAttributes;
        }
    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermitted(AttributeAuthorizationState state, Blackhole blackhole) {
        AuthResponse response = state.authorizationService.authorize(state.approveSameBranchOrderRequest);
        blackhole.consume(response);
    }

    @Benchmark
    public void ensureRejectSameClientOrderPermitted(AttributeAuthorizationState state, Blackhole blackhole) {
        AuthResponse response = state.authorizationService.authorize(state.rejectSameClientOrderRequest);
        blackhole.consume(response);
    }

    @Benchmark
    public void ensureApproveByNonManagerDenied(AttributeAuthorizationState state, Blackhole blackhole) {
        AuthResponse response = state.authorizationService.authorize(state.approveByNonManagerRequest);
        blackhole.consume(response);
    }

    public static void main(String[] args) throws EasyAbacInitException {
        AttributeAuthorizationState state = new AttributeAuthorizationState();
        state.initService();
        state.prepareApproveByNonManagerRequest();

        AuthResponse response = state.authorizationService.authorize(state.approveByNonManagerRequest);
        System.out.printf("Response = %s\n", response.getDecision());
    }
}