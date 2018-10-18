package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.core.pdp.RequestId;

import java.util.*;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class MDPBenchmark extends AbstractAuthorizationBenchmark {

    private static final int BATCH_SIZE = 3;

    private void run() throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();

        AbacAuthModel model = creator.createModel(MDPBenchmark.class.getResourceAsStream("/OrdersPolicy.yaml"));

        Datasource customerBranchIdDatasource = getCustomerBranchIdDatasource();
        AttributiveAuthorizationService authorizationService = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.DIRECT_INSTANCE)
                .datasources(Collections.singletonList(customerBranchIdDatasource))
                .subjectAttributesProvider(getSubjectAttributesProvider(getManagerSubject(), model))
                .build();

        Map<RequestId, List<AuthAttribute>> mdp = new HashMap<>();
        OrderAction action = getOrderApproveAction();
        Subject managerSubject = getManagerSubject();
        String customerBranchId = customerBranchIdDatasource.find().iterator().next();
        Random rand = new Random();

        for (int i = 0; i < BATCH_SIZE; i++) {

            boolean matchedOrder = i % 3 == 0;
            Order order = new Order("order-" + rand.nextInt(),
                    rand.nextInt(2000),
                    matchedOrder ? managerSubject.getBranchId() : "branch-" + rand.nextInt(10),
                    matchedOrder ? customerBranchId : "branch-" + rand.nextInt(10));

            List<AuthAttribute> authAttributes = new ArrayList<>();
            authAttributes.add(new AuthAttribute("order.action", "order." + action.getId()));
            authAttributes.add(new AuthAttribute("order.branchId", order.getBranchId()));
            authAttributes.add(new AuthAttribute("order.amount", "" + order.getAmount()));

            mdp.put(RequestId.newRandom(), authAttributes);
        }

        Map<RequestId, AuthResponse> responses = authorizationService.authorizeMultiple(mdp);

        Map<AuthResponse.Decision, Long> decisions = responses.values().stream()
                .collect(groupingBy(AuthResponse::getDecision, counting()));

        decisions.forEach((decision, count) -> System.out.printf("Decision: %s has %d entries\n", decision, count));
    }

    public static void main(String[] args) throws EasyAbacInitException {
        new MDPBenchmark().run();
    }
}