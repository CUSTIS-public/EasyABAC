package custis.easyabac.api.checking;

import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.DummyAttributiveAuthorizationService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnsureMethodsTest {

    private static AttributiveAuthorizationService attributiveAuthorizationService = new DummyAttributiveAuthorizationService();
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

    @Test
    public void testSingleResourceAndHardcodedActions() {
        Order order = new Order("1", BigDecimal.ZERO);
        checker.ensureDeniedView(order);

        checker.ensureDeniedViewOrApprove(order);
    }

    @Test
    public void testSingleActionAndListOfResources() {
        Order order = new Order("1", BigDecimal.ZERO);
        Order order2 = new Order("2", BigDecimal.ZERO);
        checker.ensureDeniedAll(OrderAction.VIEW, Arrays.asList(order, order2));

        checker.ensureDeniedAll(Arrays.asList(order, order2), OrderAction.VIEW);
    }

    @Test
    public void testListOfActionsAndListOfResources() {
        Order order = new Order("1", BigDecimal.ZERO);
        Order order2 = new Order("2", BigDecimal.ZERO);
        checker.ensureDeniedAll(Arrays.asList(order, order2), Arrays.asList(OrderAction.VIEW, OrderAction.APPROVE));

        checker.ensureDeniedAll_2(Arrays.asList(OrderAction.VIEW, OrderAction.APPROVE), Arrays.asList(order, order2));
    }

    @Test
    public void testMapOfSingleObjects() {
        Order order = new Order("1", BigDecimal.ZERO);
        Map<Order, OrderAction> map = Stream.of(order)
                .collect(Collectors.toMap(o -> order, o -> OrderAction.VIEW));
        checker.ensureDeniedAll(map);

        Map<OrderAction, Order> map2 = Stream.of(order)
                .collect(Collectors.toMap(o -> OrderAction.VIEW, o -> order));
        checker.ensureDeniedAll_2(map2);
    }

    @Test
    public void testMapOfSingleObjectAndList() {
        Order order = new Order("1", BigDecimal.ZERO);
        Map<Order, List<OrderAction>> map = Stream.of(order)
                .collect(Collectors.toMap(o -> order, o -> Arrays.asList(OrderAction.VIEW)));
        checker.ensureDeniedAll_3(map);

        Map<OrderAction, List<Order>> map2 = Stream.of(order)
                .collect(Collectors.toMap(o -> OrderAction.VIEW, o -> Arrays.asList(order)));
        checker.ensureDeniedAll_4(map2);
    }

    @Test
    public void testMapOfListAndSingleObject() {
        Order order = new Order("1", BigDecimal.ZERO);
        Map<List<Order>, OrderAction> map = Stream.of(order)
                .collect(Collectors.toMap(o -> Arrays.asList(order), o -> OrderAction.VIEW));
        checker.ensureDeniedAll_5(map);

        Map<List<OrderAction>, Order> map2 = Stream.of(order)
                .collect(Collectors.toMap(o -> Arrays.asList(OrderAction.VIEW), o -> order));
        checker.ensureDeniedAll_6(map2);
    }

    @Test
    public void testMapOfLists() {
        Order order = new Order("1", BigDecimal.ZERO);
        Map<List<Order>, List<OrderAction>> map = Stream.of(order)
                .collect(Collectors.toMap(o -> Arrays.asList(order), o -> Arrays.asList(OrderAction.VIEW)));
        checker.ensureDeniedAll_7(map);

        Map<List<OrderAction>, List<Order>> map2 = Stream.of(order)
                .collect(Collectors.toMap(o -> Arrays.asList(OrderAction.VIEW), o -> Arrays.asList(order)));
        checker.ensureDeniedAll_8(map2);
    }

    @BeforeClass
    public static void initialize() {
        factory = new EasyABACPermissionCheckerFactory(attributiveAuthorizationService);
        checker = factory.getPermissionChecker(EnsureMethods.class);
    }
}
