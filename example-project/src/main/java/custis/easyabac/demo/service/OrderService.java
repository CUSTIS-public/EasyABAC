package custis.easyabac.demo.service;

import custis.easyabac.demo.model.BranchId;
import custis.easyabac.demo.model.CustomerId;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderId;

import java.math.BigDecimal;

public interface OrderService {

    void createOrder(CustomerId customerId, BranchId branchId, BigDecimal amount);

    Order getOrder(OrderId id);

    void approveOrder(OrderId id);

    void rejectOrder(OrderId id);
}
