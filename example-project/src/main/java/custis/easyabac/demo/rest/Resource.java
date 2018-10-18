package custis.easyabac.demo.rest;

import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.User;
import custis.easyabac.demo.service.OrderService;
import custis.easyabac.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Resource {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/api/users")
    public List<User> getEmployees() {
        return userService.getAllUsers();
    }

    @GetMapping("/api/orders")
    public List<Order> getOrders() {
        return orderService.getAllOrders();
    }

}
