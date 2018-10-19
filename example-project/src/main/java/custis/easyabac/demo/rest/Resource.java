package custis.easyabac.demo.rest;

import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.demo.authn.AuthenticationContext;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderAction;
import custis.easyabac.demo.rest.representation.OrderRepresentation;
import custis.easyabac.demo.rest.representation.UserRepresentation;
import custis.easyabac.demo.service.OrderService;
import custis.easyabac.demo.service.UserService;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.starter.EasyAbacBuilderHelper;
import custis.easyabac.starter.PermitAwareCheckerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
public class Resource {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/api/users")
    public List<UserRepresentation> getUsers() {
        return userService.getAllUsers()
                .stream()
                .map(user -> UserRepresentation.of(user))
                .collect(toList());
    }

    @GetMapping("/api/orders")
    public List<OrderRepresentation> getOrders() throws EasyAbacInitException {
        List<OrderAction> actions = Arrays.asList(OrderAction.values());
        List<Order> orders = orderService.getAllOrders();

        InputStream modelStream = getClass().getResourceAsStream("/policy.yaml");
        EasyAbacBuilder builder = EasyAbacBuilderHelper.defaultDebugBuilder(modelStream, () -> userService.findById(AuthenticationContext.currentUserId()));
        EasyAbac easyAbac = builder.build();

        PermitAwarePermissionChecker<Order, OrderAction> permissionChecker = PermitAwareCheckerHelper.newInstance(easyAbac);

        Map<Order, List<OrderAction>> result = permissionChecker.getPermittedActions(orders, actions);

        return orders
                .stream()
                .map(order -> OrderRepresentation.of(order, result.getOrDefault(order, Collections.emptyList())))
                .collect(toList());
    }

}
