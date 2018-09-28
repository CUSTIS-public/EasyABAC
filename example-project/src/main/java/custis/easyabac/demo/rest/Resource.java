package custis.easyabac.demo.rest;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.demo.authz.abac.OrderAction;
import custis.easyabac.demo.model.Branch;
import custis.easyabac.demo.model.Customer;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.permissionchecker.BranchPermissionChecker;
import custis.easyabac.demo.permissionchecker.OrderPermissionChecker;
import custis.easyabac.demo.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class Resource {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private OrderPermissionChecker orderPermissionChecker;

    @Autowired
    private BranchPermissionChecker branchPermissionChecker;

    @RequestMapping("/")
    public String index() throws NotExpectedResultException {
        Order order = new Order(new Customer("asdasda", "asdasd", new Branch("gggggg")),
                new Branch("pppppp"), BigDecimal.ZERO);
        orderPermissionChecker.isPermitted(order, OrderAction.VIEW);
        orderPermissionChecker.isPermittedRead(order);
        orderPermissionChecker.ensureDeniedAll(new HashMap<Order, OrderAction>() {
            {
                put(order, OrderAction.VIEW);
                put(order, OrderAction.CREATE);
            }
        });


        return "AZAZAZAZA";
    }
}
