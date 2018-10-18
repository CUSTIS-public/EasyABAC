package custis.easyabac.demo.rest;

import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;
import custis.easyabac.demo.authn.AuthenticationContext;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderAction;
import custis.easyabac.demo.rest.representation.OrderRepresentation;
import custis.easyabac.demo.rest.representation.UserRepresentation;
import custis.easyabac.demo.service.OrderService;
import custis.easyabac.demo.service.UserService;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.starter.EasyAbacBuilderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.Arrays;
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
        InputStream is = Resource.class.getResourceAsStream("/policy.yaml");
        EasyAbacBuilder builder = EasyAbacBuilderHelper.defaultBuilder(is, () -> userService.findById(AuthenticationContext.currentUserId()));
        AttributiveAuthorizationService easyAbac = builder.build();
        PermitAwarePermissionChecker<Order, OrderAction> permissionChecker = new EasyABACPermissionChecker(easyAbac);

        List<Order> orders = orderService.getAllOrders();
        Map<Order, List<OrderAction>> permitted = permissionChecker.getPermittedActions(orders, Arrays.asList(OrderAction.values()));

        return orders
                .stream()
                .map(order -> OrderRepresentation.of(order, permitted.get(order)))
                .collect(toList());
    }

}
