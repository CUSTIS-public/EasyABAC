package custis.easyabac.api.getting;

import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AttributiveAuthorizationServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class GettingMethodsTest {

    private static AttributiveAuthorizationService attributiveAuthorizationService = new AttributiveAuthorizationServiceImpl();
    private static EasyABACPermissionCheckerFactory factory;
    private static GettingMethods checker;

    @Test
    public void testSingleResourceAndActions() {
        Order order = new Order("1", BigDecimal.ZERO);
        List<OrderAction> actions = checker.getDeniedActions(order, asList(OrderAction.APPROVE, OrderAction.VIEW));

        System.out.println(actions);

        Map<Order, List<OrderAction>> actionsMap = checker.getDeniedActions_2(order, asList(OrderAction.APPROVE, OrderAction.VIEW));

        System.out.println(actionsMap);
    }

    @Test
    public void testListAndList() {
        Order order = new Order("1", BigDecimal.ZERO);
        Order order2 = new Order("2", BigDecimal.ZERO);

        Map<Order, List<OrderAction>> actions = checker.getDeniedActions(
                asList(order, order2),
                asList(OrderAction.APPROVE, OrderAction.VIEW)
        );

        System.out.println(actions);
    }

    @BeforeClass
    public static void initialize() {
        factory = new EasyABACPermissionCheckerFactory(attributiveAuthorizationService);
        checker = factory.getPermissionChecker(GettingMethods.class);
    }
}