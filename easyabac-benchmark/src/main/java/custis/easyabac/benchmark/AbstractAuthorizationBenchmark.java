package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Customer;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;

public class AbstractAuthorizationBenchmark {

    Order getOrder() {
        return new Order("1", 100, "123", "456");
    }

    OrderAction getOrderAction() {
        return OrderAction.APPROVE;
    }

    Customer getCustomer() {
        return new Customer("456", "123");
    }

    Subject getSubject() {
        return new Subject("1", "MANAGER", "123", 500);
    }
}
