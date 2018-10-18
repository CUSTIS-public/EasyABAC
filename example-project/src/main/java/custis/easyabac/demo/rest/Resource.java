package custis.easyabac.demo.rest;

import custis.easyabac.demo.rest.representation.OrderRepresentation;
import custis.easyabac.demo.rest.representation.UserRepresentation;
import custis.easyabac.demo.service.OrderService;
import custis.easyabac.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<OrderRepresentation> getOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(order -> OrderRepresentation.of(order))
                .collect(toList());
    }

}
