package custis.easyabac.api.getting;

import custis.easyabac.api.checking.EnsureMethods;
import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class GettingMethodsTest {

    private static AuthService authService;
    private static EasyABACPermissionCheckerFactory factory;
    private static GettingMethods checker;

    @Test
    public void testSingleResourceAndActions() {
        Order order = new Order("1", 0);
        List<OrderAction> actions = checker.getDeniedActions(order, asList(OrderAction.APPROVE, OrderAction.VIEW));

        System.out.println(actions);

        Map<Order, List<OrderAction>> actionsMap = checker.getDeniedActions_2(order, asList(OrderAction.APPROVE, OrderAction.VIEW));

        System.out.println(actionsMap);
    }

    @Test
    public void testListAndList() {
        Order order = new Order("1", 0);
        Order order2 = new Order("2", 0);

        Map<Order, List<OrderAction>> actions = checker.getDeniedActions(
                asList(order, order2),
                asList(OrderAction.APPROVE, OrderAction.VIEW)
        );

        System.out.println(actions);
    }

    @BeforeClass
    public static void initialize() throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AbacAuthModel model = creator.createModel(EnsureMethods.class.getResourceAsStream("/deny.yaml"));
        EasyAbacBuilder builder = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.DIRECT_INSTANCE);
        authService = builder.build();
        factory = new EasyABACPermissionCheckerFactory(authService);
        checker = factory.getPermissionChecker(GettingMethods.class);
    }
}
