package custis.easyabac.api.checking;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;

import java.util.List;
import java.util.Map;

public interface EnsureMethods extends PermitAwarePermissionChecker<Order, OrderAction> {

    void ensureDenied(Order order, OrderAction action) throws NotExpectedResultException;

    void ensureDenied(OrderAction action, Order order) throws NotExpectedResultException;

    void ensureDeniedAll(Order order, List<OrderAction> actions) throws NotExpectedResultException;

    void ensureDeniedAll(List<OrderAction> actions, Order order) throws NotExpectedResultException;

    void ensureDeniedView(Order order) throws NotExpectedResultException;

    void ensureDeniedViewOrApprove(Order order) throws NotExpectedResultException;

    void ensureDeniedAll(OrderAction action, List<Order> orders) throws NotExpectedResultException;

    void ensureDeniedAll(List<Order> orders, OrderAction action) throws NotExpectedResultException;

    void ensureDeniedAll(List<Order> orders, List<OrderAction> actions) throws NotExpectedResultException;

    void ensureDenied2All(List<OrderAction> actions, List<Order> orders) throws NotExpectedResultException;

    void ensureDeniedAll(Map<Order, OrderAction> orderActionMap) throws NotExpectedResultException;

    void ensureDenied2All(Map<OrderAction, Order> orderActionMap) throws NotExpectedResultException;

    void ensureDenied3All(Map<Order, List<OrderAction>> orderActionMap) throws NotExpectedResultException;

    void ensureDenied4All(Map<OrderAction, List<Order>> orderActionMap) throws NotExpectedResultException;

    void ensureDenied5All(Map<List<Order>, OrderAction> orderActionMap) throws NotExpectedResultException;

    void ensureDenied6All(Map<List<OrderAction>, Order> orderActionMap) throws NotExpectedResultException;

    void ensureDenied7All(Map<List<Order>, List<OrderAction>> orderActionMap) throws NotExpectedResultException;

    void ensureDenied8All(Map<List<OrderAction>, List<Order>> orderActionMap) throws NotExpectedResultException;
}
