package custis.easyabac.api.getting;

import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AttributiveAuthorizationServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

public class GettingMethodsTest {

    private static AttributiveAuthorizationService attributiveAuthorizationService = new AttributiveAuthorizationServiceImpl();
    private static EasyABACPermissionCheckerFactory factory;
    private static GettingMethods checker;

    @Test
    public void testSingleResourceAndAction() {
        Order order = new Order("1", BigDecimal.ZERO);
        checker.getDeniedActions(order, OrderAction.APPROVE);

        checker.getDeniedResources(order, OrderAction.APPROVE);

        checker.getDeniedActions(OrderAction.APPROVE, order);

        checker.getDeniedResources(OrderAction.APPROVE, order);
    }

    @BeforeClass
    public static void initialize() {
        factory = new EasyABACPermissionCheckerFactory(attributiveAuthorizationService);
        checker = factory.getPermissionChecker(GettingMethods.class);
    }
}
