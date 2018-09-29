package custis.easyabac.api.getting;

import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;

import java.util.List;

public interface GettingMethods extends PermitAwarePermissionChecker<Order, OrderAction> {

    List<OrderAction> getDeniedActions(Order order, OrderAction action);

    List<Order> getDeniedResources(Order order, OrderAction action);

    List<OrderAction> getDeniedActions(OrderAction action, Order order);

    List<Order> getDeniedResources(OrderAction action, Order order);
}
