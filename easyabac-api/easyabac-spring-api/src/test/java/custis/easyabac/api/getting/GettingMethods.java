package custis.easyabac.api.getting;

import custis.easyabac.api.PermissionChecker;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;

import java.util.List;
import java.util.Map;

public interface GettingMethods extends PermissionChecker<Order, OrderAction> {

    List<OrderAction> getDeniedActions(Order order, List<OrderAction> action);

    Map<Order, List<OrderAction>> getDeniedActions_2(Order order, List<OrderAction> action);

    Map<Order, List<OrderAction>> getDeniedActions(List<Order> orders, List<OrderAction> actions);

}
