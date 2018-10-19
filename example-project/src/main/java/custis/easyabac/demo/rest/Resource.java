package custis.easyabac.demo.rest;

import custis.easyabac.api.EntityPermissionChecker;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.demo.authn.AuthenticationContext;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderAction;
import custis.easyabac.demo.model.User;
import custis.easyabac.demo.service.OrderService;
import custis.easyabac.demo.service.UserService;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.starter.EasyAbacDebugBuilderHelper;
import custis.easyabac.starter.EntityPermissionCheckerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class Resource {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public String orders(Model model) throws EasyAbacInitException {
        List<User> users = userService.getAllUsers();
        List<Order> orders = orderService.getAllOrders();


        List<OrderAction> actions = Arrays.asList(OrderAction.values());
        InputStream modelStream = getClass().getResourceAsStream("/policy.yaml");
        EasyAbac easyAbac = EasyAbacDebugBuilderHelper.defaultDebugBuilder(
                modelStream,
                () -> userService.findById(AuthenticationContext.currentUserId()))
                .build();
        EntityPermissionChecker<Order, OrderAction> permissionChecker = EntityPermissionCheckerHelper.newPermissionChecker(easyAbac);
        Map<Order, List<OrderAction>> result = permissionChecker.getPermittedActions(orders, actions);


        model.addAttribute("users", users);
        model.addAttribute("orders", orders);
        model.addAttribute("currentUserId", AuthenticationContext.currentUserId());
        return "orders";
    }


}
