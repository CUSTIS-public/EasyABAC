package custis.easyabac.api.checking;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;

import java.util.List;

public interface EnsureMethods extends PermitAwarePermissionChecker<Order, OrderAction> {

    void ensureDenied(Order order, OrderAction action) throws NotExpectedResultException;

    void ensureDenied(OrderAction action, Order order) throws NotExpectedResultException;

    void ensureDeniedAll(Order order, List<OrderAction> actions) throws NotExpectedResultException;

    void ensureDeniedAll(List<OrderAction> actions, Order order) throws NotExpectedResultException;
}
