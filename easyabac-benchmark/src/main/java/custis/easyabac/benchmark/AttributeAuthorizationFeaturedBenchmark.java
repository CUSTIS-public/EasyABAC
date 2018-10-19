package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.Options;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.core.trace.logging.LoggingViewTrace;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
public class AttributeAuthorizationFeaturedBenchmark extends AbstractAuthorizationBenchmark {

    private AuthService authorizationServiceWithTrace;
    private AuthService authorizationServiceWithAudit;
    private AuthService authorizationServiceFullFeatured;

    @Setup(Level.Trial)
    public void initService() throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AbacAuthModel model = creator.createModel(getClass().getResourceAsStream("/OrdersPolicy.yaml"));

        Options enableTraceOptions = new Options.OptionsBuilder()
                .enableTrace(true)
                .enableOptimization(false)
                .build();
        this.authorizationServiceWithTrace = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .trace(new LoggingViewTrace())
                .options(enableTraceOptions)
                .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                .build();

        Options enableAuditOptions = new Options.OptionsBuilder()
                .enableTrace(false)
                .enableAudit(true)
                .enableOptimization(false)
                .build();
        this.authorizationServiceWithAudit = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .options(enableAuditOptions)
                .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                .build();

        Options enableAllOptions = new Options.OptionsBuilder()
                .enableTrace(true)
                .enableAudit(true)
                .enableOptimization(false)
                .build();
        this.authorizationServiceFullFeatured = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .options(enableAllOptions)
                .trace(new LoggingViewTrace())
                .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                .build();
    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermittedWithTrace(Blackhole blackhole) {
        AuthResponse response = authorizationServiceWithTrace.authorize(approveSameBranchOrderRequest());
        blackhole.consume(response);
    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermittedWithAudit(Blackhole blackhole) {
        AuthResponse response = authorizationServiceWithAudit.authorize(approveSameBranchOrderRequest());
        blackhole.consume(response);
    }

    @Benchmark
    public void ensureApproveSameBranchOrderPermittedWithAllFeatures(Blackhole blackhole) {
        AuthResponse response = authorizationServiceFullFeatured.authorize(approveSameBranchOrderRequest());
        blackhole.consume(response);
    }

    private List<AuthAttribute> approveSameBranchOrderRequest() {
        Order order = getOrder();
        OrderAction action = getOrderApproveAction();

        List<AuthAttribute> authAttributes = new ArrayList<>();
        authAttributes.add(new AuthAttribute("order.action", "order." + action.getId()));
        authAttributes.add(new AuthAttribute("order.branchId", order.getBranchId()));
        authAttributes.add(new AuthAttribute("order.amount", "" + order.getAmount()));
        return authAttributes;
    }
}