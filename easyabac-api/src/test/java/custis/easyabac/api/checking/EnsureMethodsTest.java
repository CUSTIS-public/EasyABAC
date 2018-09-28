package custis.easyabac.api.checking;

import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AttributiveAuthorizationServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class EnsureMethodsTest {

    private static AttributiveAuthorizationService attributiveAuthorizationService = new AttributiveAuthorizationServiceImpl();
    private static EasyABACPermissionCheckerFactory factory;
    private static EnsureMethods checker;

    @Test
    public void testSingleResourceAndAction() {
        Order order = new Order("1", BigDecimal.ZERO);
        checker.ensureDenied(order, OrderAction.APPROVE);

        checker.ensureDenied(OrderAction.APPROVE, order);
    }

    @Test
    public void testSingleResourceAndListOfActions() {
        Order order = new Order("1", BigDecimal.ZERO);
        checker.ensureDeniedAll(order, Arrays.asList(OrderAction.APPROVE, OrderAction.VIEW));

        checker.ensureDeniedAll(Arrays.asList(OrderAction.APPROVE, OrderAction.VIEW), order);
    }

    @BeforeClass
    public static void initialize() {
        factory = new EasyABACPermissionCheckerFactory(attributiveAuthorizationService);
        checker = factory.getPermissionChecker(EnsureMethods.class);
    }
}
