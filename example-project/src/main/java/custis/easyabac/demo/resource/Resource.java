package custis.easyabac.demo.resource;

import custis.easyabac.demo.authn.AuthenticationContext;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.resource.dto.OrderDto;
import custis.easyabac.demo.resource.dto.UserDto;
import custis.easyabac.demo.service.OrderService;
import custis.easyabac.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
public class Resource {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public String orders(Model model) throws Exception {
        List<Order> orders = orderService.getAllOrders();

        List<UserDto> userDtos = userService.getAllUsers().stream()
                .map(user -> UserDto.of(user))
                .collect(toList());

        List<OrderDto> orderDtos = orders.stream()
                .map(order -> OrderDto.of(order))
                .collect(toList());

        model.addAttribute("users", userDtos);
        model.addAttribute("orders", orderDtos);
        model.addAttribute("currentUserId", AuthenticationContext.currentUserId());
        return "orders";
    }


}
