package custis.easyabac.api.checking;

import custis.easyabac.api.impl.EasyABACPermissionCheckerFactory;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.attribute.AttributeWithValue;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static custis.easyabac.model.attribute.Attribute.SUBJECT_ID;

public class EnsureMethodsTest {

    private static AuthService authService;
    private static EasyABACPermissionCheckerFactory factory;
    private static EnsureMethods checker;

    @Test
    public void testSingleResourceAndAction() {
        Order order = new Order("1", 0);
        checker.ensureDenied(order, OrderAction.APPROVE);

        checker.ensureDenied(OrderAction.APPROVE, order);
    }

    @Test
    public void testSingleResourceAndListOfActions() {
        Order order = new Order("1", 0);
        checker.ensureDeniedAll(order, Arrays.asList(OrderAction.APPROVE, OrderAction.VIEW));

        checker.ensureDeniedAll(Arrays.asList(OrderAction.APPROVE, OrderAction.VIEW), order);
    }

    @Test
    public void testSingleResourceAndHardcodedActions() {
        Order order = new Order("1", 0);
        checker.ensureDeniedView(order);

        checker.ensureDeniedViewOrApprove(order);
    }

    @Test
    public void testSingleActionAndListOfResources() {
        Order order = new Order("1", 0);
        Order order2 = new Order("2", 0);
        checker.ensureDeniedAll(OrderAction.VIEW, Arrays.asList(order, order2));

        checker.ensureDeniedAll(Arrays.asList(order, order2), OrderAction.VIEW);
    }

    @Test
    public void testListOfActionsAndListOfResources() {
        Order order = new Order("1", 0);
        Order order2 = new Order("2", 0);
        checker.ensureDeniedAll(Arrays.asList(order, order2), Arrays.asList(OrderAction.VIEW, OrderAction.APPROVE));

        checker.ensureDeniedAll_2(Arrays.asList(OrderAction.VIEW, OrderAction.APPROVE), Arrays.asList(order, order2));
    }

    @Test
    public void testMapOfSingleObjects() {
        Order order = new Order("1", 0);
        Map<Order, OrderAction> map = Stream.of(order)
                .collect(Collectors.toMap(o -> order, o -> OrderAction.VIEW));
        checker.ensureDeniedAll(map);

        Map<OrderAction, Order> map2 = Stream.of(order)
                .collect(Collectors.toMap(o -> OrderAction.VIEW, o -> order));
        checker.ensureDeniedAll_2(map2);
    }

    @Test
    public void testMapOfSingleObjectAndList() {
        Order order = new Order("1", 0);
        Map<Order, List<OrderAction>> map = Stream.of(order)
                .collect(Collectors.toMap(o -> order, o -> Arrays.asList(OrderAction.VIEW)));
        checker.ensureDeniedAll_3(map);

        Map<OrderAction, List<Order>> map2 = Stream.of(order)
                .collect(Collectors.toMap(o -> OrderAction.VIEW, o -> Arrays.asList(order)));
        checker.ensureDeniedAll_4(map2);
    }

    @Test
    public void testMapOfListAndSingleObject() {
        Order order = new Order("1", 0);
        Map<List<Order>, OrderAction> map = Stream.of(order)
                .collect(Collectors.toMap(o -> Arrays.asList(order), o -> OrderAction.VIEW));
        checker.ensureDeniedAll_5(map);

        Map<List<OrderAction>, Order> map2 = Stream.of(order)
                .collect(Collectors.toMap(o -> Arrays.asList(OrderAction.VIEW), o -> order));
        checker.ensureDeniedAll_6(map2);
    }

    @Test
    public void testMapOfLists() {
        Order order = new Order("1", 0);
        Map<List<Order>, List<OrderAction>> map = Stream.of(order)
                .collect(Collectors.toMap(o -> Arrays.asList(order), o -> Arrays.asList(OrderAction.VIEW)));
        checker.ensureDeniedAll_7(map);

        Map<List<OrderAction>, List<Order>> map2 = Stream.of(order)
                .collect(Collectors.toMap(o -> Arrays.asList(OrderAction.VIEW), o -> Arrays.asList(order)));
        checker.ensureDeniedAll_8(map2);
    }

    @BeforeClass
    public static void initialize() throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AbacAuthModel model = creator.createModel(EnsureMethods.class.getResourceAsStream("/deny.yaml"));
        EasyAbacBuilder builder = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.DIRECT_INSTANCE);
        builder.subjectAttributesProvider(() -> Collections.singletonList(new AttributeWithValue(SUBJECT_ID, Collections.singletonList("subject_id"))));
        authService = builder.build();
        factory = new EasyABACPermissionCheckerFactory(authService);
        checker = factory.getPermissionChecker(EnsureMethods.class);
    }
}
